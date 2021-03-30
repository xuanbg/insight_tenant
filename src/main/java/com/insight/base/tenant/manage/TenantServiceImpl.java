package com.insight.base.tenant.manage;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insight.base.tenant.common.Core;
import com.insight.base.tenant.common.client.LogClient;
import com.insight.base.tenant.common.client.LogServiceClient;
import com.insight.base.tenant.common.client.RabbitClient;
import com.insight.base.tenant.common.dto.AppListDto;
import com.insight.base.tenant.common.dto.Organize;
import com.insight.base.tenant.common.dto.TenantListDto;
import com.insight.base.tenant.common.dto.UserListDto;
import com.insight.base.tenant.common.entity.Tenant;
import com.insight.base.tenant.common.entity.TenantApp;
import com.insight.base.tenant.common.mapper.TenantMapper;
import com.insight.utils.Redis;
import com.insight.utils.ReplyHelper;
import com.insight.utils.Util;
import com.insight.utils.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/12/18
 * @remark 租户服务接口实现类
 */
@Service
public class TenantServiceImpl implements TenantService {
    private static final String BUSINESS = "租户管理";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Core core;
    private final LogServiceClient client;
    private final TenantMapper mapper;

    /**
     * 构造方法
     *
     * @param core   Core
     * @param client LogServiceClient
     * @param mapper TenantMapper
     */
    public TenantServiceImpl(Core core, LogServiceClient client, TenantMapper mapper) {
        this.core = core;
        this.client = client;
        this.mapper = mapper;
    }

    /**
     * 根据设定的条件查询租户信息(分页)
     *
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @Override
    public Reply getTenants(String keyword, int page, int size) {
        PageHelper.startPage(page, size);
        List<TenantListDto> tenants = mapper.getTenants(keyword);
        PageInfo<TenantListDto> pageInfo = new PageInfo<>(tenants);

        return ReplyHelper.success(tenants, pageInfo.getTotal());
    }

    /**
     * 查询指定ID的租户信息
     *
     * @param id 租户ID
     * @return Reply
     */
    @Override
    public Reply getTenant(String id) {
        Tenant tenant = mapper.getTenant(id);
        if (tenant == null) {
            return ReplyHelper.fail("ID不存在,未读取数据");
        }

        return ReplyHelper.success(tenant);
    }

    /**
     * 查询指定ID的租户绑定的应用集合
     *
     * @param id 租户ID
     * @return Reply
     */
    @Override
    public Reply getTenantApps(String id) {
        List<AppListDto> list = mapper.getTenantApps(id);

        return ReplyHelper.success(list);
    }

    /**
     * 获取指定ID的租户的用户集合
     *
     * @param tenantId 租户ID
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    @Override
    public Reply getTenantUsers(String tenantId, int page, int size) {
        PageHelper.startPage(page, size);
        List<UserListDto> users = mapper.getTenantUsers(tenantId);
        PageInfo<UserListDto> pageInfo = new PageInfo<>(users);

        return ReplyHelper.success(users, pageInfo.getTotal());
    }

    /**
     * 新增租户
     *
     * @param info 访问令牌
     * @param dto  租户实体数据
     * @return Reply
     */
    @Override
    public Reply addTenant(LoginInfo info, Tenant dto) {
        String alias = dto.getAlias();
        int count = mapper.getUserCount(alias);
        if (count > 0) {
            return ReplyHelper.fail("简称「" + alias + "」已被使用,请使用其它简称");
        }

        String id = Util.uuid();
        dto.setId(id);
        dto.setCode(core.getCode());
        dto.setCreator(info.getUserName());
        dto.setCreatorId(info.getUserId());
        dto.setCreatedTime(LocalDateTime.now());

        mapper.addTenant(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.INSERT, id, dto);

        return ReplyHelper.created(id);
    }

    /**
     * 更新租户数据
     *
     * @param info 访问令牌
     * @param dto  租户实体数据
     * @return Reply
     */
    @Override
    public Reply updateTenant(LoginInfo info, Tenant dto) {
        String id = dto.getId();
        Tenant tenant = mapper.getTenant(id);
        if (tenant == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        mapper.editTenant(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, dto);

        return ReplyHelper.success();
    }

    /**
     * 审核租户
     *
     * @param info 访问令牌
     * @param dto  租户实体数据
     * @return Reply
     */
    @Override
    @Transactional
    public Reply auditTenant(LoginInfo info, Tenant dto) {
        String id = dto.getId();
        Tenant tenant = mapper.getTenant(id);
        if (tenant == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        if (tenant.getStatus() == 1) {
            return ReplyHelper.success();
        }

        int status = dto.getStatus();
        if (status < 1 || status > 2) {
            return ReplyHelper.fail("审核状态码错误");
        }

        mapper.auditTenant(id, status);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, dto);
        if (status == 2) {
            return ReplyHelper.success();
        }

        // 关联系统管理客户端应用
        String appId = "e46c0d4f85f24f759ad4d86b9505b1d4";
        List<String> appIds = new ArrayList<>();
        appIds.add(appId);
        mapper.addAppsToTenant(id, appIds);

        // 创建组织
        Organize organize = new Organize();
        organize.setId(id);
        organize.setTenantId(id);
        organize.setType(0);
        organize.setIndex(0);
        organize.setName(tenant.getName());
        organize.setAlias(tenant.getAlias());
        organize.setFullName(tenant.getName());
        organize.setCreator(info.getUserName());
        organize.setCreatorId(info.getUserId());
        RabbitClient.sendTopic("tenant.addOrganize", organize);

        // 创建租户系统管理员
        String userId = Util.uuid();
        mapper.addRelation(id, userId);

        User user = new User();
        user.setId(userId);
        user.setName("系统管理员");
        user.setAccount(tenant.getAlias());
        user.setPassword(Util.md5("123456"));
        user.setCreator(info.getUserName());
        user.setCreatorId(info.getUserId());
        RabbitClient.sendTopic("tenant.addUser", user);

        // 创建租户系统管理员角色
        MemberDto member = new MemberDto();
        member.setId(userId);
        member.setType(1);
        List<MemberDto> members = new ArrayList<>();
        members.add(member);
        core.addRole(info, id, appId, members);

        return ReplyHelper.success();
    }

    /**
     * 启用、禁用租户信息
     *
     * @param info   用户关键信息
     * @param id     租户ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    @Override
    public Reply updateTenantStatus(LoginInfo info, String id, boolean status) {
        Tenant tenant = mapper.getTenant(id);
        if (tenant == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        mapper.changeTenantStatus(id, status);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, tenant);

        return ReplyHelper.success();
    }

    /**
     * 删除指定ID的租户
     *
     * @param info 用户关键信息
     * @param id   租户ID
     * @return Reply
     */
    @Override
    public Reply deleteTenant(LoginInfo info, String id) {
        Tenant tenant = mapper.getTenant(id);
        if (tenant == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        mapper.deleteTenant(id);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, tenant);

        return ReplyHelper.success();
    }

    /**
     * 获取租户可用应用集合
     *
     * @param id 租户ID
     * @return Reply
     */
    @Override
    public Reply getUnboundApps(String id) {
        List<AppListDto> list = mapper.getUnboundApps(id);

        return ReplyHelper.success(list);
    }

    /**
     * 设置应用与指定ID的租户的绑定关系
     *
     * @param info   用户关键信息
     * @param id     租户ID
     * @param appIds 应用ID集合
     * @return Reply
     */
    @Override
    public Reply addAppsToTenant(LoginInfo info, String id, List<String> appIds) {
        Tenant tenant = mapper.getTenant(id);
        if (tenant == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        if (tenant.getStatus() != 1) {
            return ReplyHelper.fail("租户尚未通过审核,不能关联应用");
        }

        List<AppListDto> list = mapper.getTenantApps(id);
        list.forEach(i -> appIds.remove(i.getId()));
        if (appIds.isEmpty()) {
            return ReplyHelper.success();
        }

        mapper.addAppsToTenant(id, appIds);
        LogClient.writeLog(info, BUSINESS, OperateType.INSERT, id, appIds);

        // 为租户创建初始角色
        for (String appId : appIds) {
            core.addRole(info, id, appId, null);
        }

        return ReplyHelper.success();
    }

    /**
     * 解除应用与指定ID的租户的绑定关系
     *
     * @param info   用户信息
     * @param id     租户ID
     * @param appIds 应用ID集合
     * @return Reply
     */
    @Override
    public Reply removeAppsFromTenant(LoginInfo info, String id, List<String> appIds) {
        Tenant tenant = mapper.getTenant(id);
        if (tenant == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        int count = mapper.getAppsRoleCount(id, appIds);
        if (count > 0) {
            return ReplyHelper.fail("应用对应的角色数据未删除,请先删除属于该租户的无效角色数据");
        }

        mapper.removeAppsFromTenant(id, appIds);
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, appIds);

        return ReplyHelper.success();
    }

    /**
     * 续租应用
     *
     * @param info 用户关键信息
     * @param dto  租户应用实体数据
     * @return Reply
     */
    @Override
    public Reply rentTenantApp(LoginInfo info, TenantApp dto) {
        String tenantId = dto.getTenantId();
        Tenant tenant = mapper.getTenant(tenantId);
        if (tenant == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        LocalDate expire = dto.getExpireDate();
        if (expire == null || LocalDate.now().isAfter(expire)) {
            return ReplyHelper.fail("到期日期无效");
        }

        mapper.rentTenant(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, tenantId, dto);

        // 更新缓存数据
        String key = "App:" + dto.getAppId();
        if (Redis.hasKey(key)) {
            Redis.setHash(key, tenantId, dto.getExpireDate());
        }

        return ReplyHelper.success();
    }

    /**
     * 获取日志列表
     *
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @Override
    public Reply getTenantLogs(String keyword, int page, int size) {
        return client.getLogs(BUSINESS, keyword, page, size);
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @Override
    public Reply getTenantLog(String id) {
        return client.getLog(id);
    }
}
