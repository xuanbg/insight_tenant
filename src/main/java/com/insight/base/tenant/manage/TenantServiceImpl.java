package com.insight.base.tenant.manage;

import com.github.pagehelper.PageHelper;
import com.insight.base.tenant.common.Core;
import com.insight.base.tenant.common.client.LogClient;
import com.insight.base.tenant.common.client.LogServiceClient;
import com.insight.base.tenant.common.client.RabbitClient;
import com.insight.base.tenant.common.dto.AppListDto;
import com.insight.base.tenant.common.dto.Organize;
import com.insight.base.tenant.common.entity.Tenant;
import com.insight.base.tenant.common.entity.TenantApp;
import com.insight.base.tenant.common.mapper.TenantMapper;
import com.insight.utils.Redis;
import com.insight.utils.ReplyHelper;
import com.insight.utils.SnowflakeCreator;
import com.insight.utils.Util;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.BusinessException;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;
import com.insight.utils.pojo.message.OperateType;
import com.insight.utils.pojo.user.MemberDto;
import com.insight.utils.pojo.user.User;
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
    private final SnowflakeCreator creator;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Core core;
    private final LogServiceClient client;
    private final TenantMapper mapper;

    /**
     * 构造方法
     *
     * @param creator 雪花算法ID生成器
     * @param core    Core
     * @param client  LogServiceClient
     * @param mapper  TenantMapper
     */
    public TenantServiceImpl(SnowflakeCreator creator, Core core, LogServiceClient client, TenantMapper mapper) {
        this.creator = creator;
        this.core = core;
        this.client = client;
        this.mapper = mapper;
    }

    /**
     * 根据设定的条件查询租户信息(分页)
     *
     * @param search 查询实体类
     * @return Reply
     */
    @Override
    public Reply getTenants(Search search) {
        var page = PageHelper.startPage(search.getPageNum(), search.getPageSize())
                .setOrderBy(search.getOrderBy()).doSelectPage(() -> mapper.getTenants(search));

        var total = page.getTotal();
        return total > 0 ? ReplyHelper.success(page.getResult(), total) : ReplyHelper.resultIsEmpty();
    }

    /**
     * 查询指定ID的租户信息
     *
     * @param id 租户ID
     * @return Reply
     */
    @Override
    public Tenant getTenant(Long id) {
        Tenant tenant = mapper.getTenant(id);
        if (tenant == null) {
            throw new BusinessException("ID不存在,未读取数据");
        }

        return tenant;
    }

    /**
     * 查询指定ID的租户绑定的应用集合
     *
     * @param id 租户ID
     * @return Reply
     */
    @Override
    public List<AppListDto> getTenantApps(Long id) {
        return mapper.getTenantApps(id);
    }

    /**
     * 获取指定ID的租户的用户集合
     *
     * @param search 查询实体类
     * @return Reply
     */
    @Override
    public Reply getTenantUsers(Search search) {
        var page = PageHelper.startPage(search.getPageNum(), search.getPageSize())
                .setOrderBy(search.getOrderBy()).doSelectPage(() -> mapper.getTenantUsers(search));

        var total = page.getTotal();
        return total > 0 ? ReplyHelper.success(page.getResult(), total) : ReplyHelper.resultIsEmpty();
    }

    /**
     * 新增租户
     *
     * @param info 访问令牌
     * @param dto  租户实体数据
     * @return Reply
     */
    @Override
    public Long addTenant(LoginInfo info, Tenant dto) {
        String alias = dto.getAlias();
        int count = mapper.getUserCount(alias);
        if (count > 0) {
            throw new BusinessException("简称「" + alias + "」已被使用,请使用其它简称");
        }

        Long id = creator.nextId(4);
        dto.setId(id);
        dto.setCode(core.getCode());
        dto.setCreator(info.getUserName());
        dto.setCreatorId(info.getUserId());
        dto.setCreatedTime(LocalDateTime.now());

        mapper.addTenant(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.INSERT, id, dto);

        return id;
    }

    /**
     * 更新租户数据
     *
     * @param info 访问令牌
     * @param dto  租户实体数据
     */
    @Override
    public void updateTenant(LoginInfo info, Tenant dto) {
        Long id = dto.getId();
        Tenant tenant = mapper.getTenant(id);
        if (tenant == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        mapper.editTenant(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, dto);
    }

    /**
     * 审核租户
     *
     * @param info 访问令牌
     * @param dto  租户实体数据
     */
    @Override
    @Transactional
    public void auditTenant(LoginInfo info, Tenant dto) {
        Long id = dto.getId();
        Tenant tenant = mapper.getTenant(id);
        if (tenant == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        if (tenant.getStatus() == 1) {
            return;
        }

        int status = dto.getStatus();
        if (status < 1 || status > 2) {
            throw new BusinessException("审核状态码错误");
        }

        mapper.auditTenant(id, status);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, dto);
        if (status == 2) {
            return;
        }

        // 关联系统管理客户端应用
        Long appId = 134661270778413072L;
        List<Long> appIds = new ArrayList<>();
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
        Long userId = creator.nextId(3);
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
    }

    /**
     * 启用、禁用租户信息
     *
     * @param info   用户关键信息
     * @param id     租户ID
     * @param status 禁用/启用状态
     */
    @Override
    public void updateTenantStatus(LoginInfo info, Long id, boolean status) {
        Tenant tenant = mapper.getTenant(id);
        if (tenant == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        mapper.changeTenantStatus(id, status);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, tenant);
    }

    /**
     * 删除指定ID的租户
     *
     * @param info 用户关键信息
     * @param id   租户ID
     */
    @Override
    public void deleteTenant(LoginInfo info, Long id) {
        Tenant tenant = mapper.getTenant(id);
        if (tenant == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        mapper.deleteTenant(id);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, tenant);
    }

    /**
     * 获取租户可用应用集合
     *
     * @param id 租户ID
     * @return Reply
     */
    @Override
    public List<AppListDto> getUnboundApps(Long id) {
        return mapper.getUnboundApps(id);
    }

    /**
     * 设置应用与指定ID的租户的绑定关系
     *
     * @param info   用户关键信息
     * @param id     租户ID
     * @param appIds 应用ID集合
     */
    @Override
    public void addAppsToTenant(LoginInfo info, Long id, List<Long> appIds) {
        Tenant tenant = mapper.getTenant(id);
        if (tenant == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        if (tenant.getStatus() != 1) {
            throw new BusinessException("租户尚未通过审核,不能关联应用");
        }

        List<AppListDto> list = mapper.getTenantApps(id);
        list.forEach(i -> appIds.remove(i.getId()));
        if (appIds.isEmpty()) {
            return ;
        }

        mapper.addAppsToTenant(id, appIds);
        LogClient.writeLog(info, BUSINESS, OperateType.INSERT, id, appIds);

        // 为租户创建初始角色
        for (Long appId : appIds) {
            core.addRole(info, id, appId, null);
        }
    }

    /**
     * 解除应用与指定ID的租户的绑定关系
     *
     * @param info   用户信息
     * @param id     租户ID
     * @param appIds 应用ID集合
     */
    @Override
    public void removeAppsFromTenant(LoginInfo info, Long id, List<Long> appIds) {
        Tenant tenant = mapper.getTenant(id);
        if (tenant == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        int count = mapper.getAppsRoleCount(id, appIds);
        if (count > 0) {
            throw new BusinessException("应用对应的角色数据未删除,请先删除属于该租户的无效角色数据");
        }

        mapper.removeAppsFromTenant(id, appIds);
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, appIds);
    }

    /**
     * 续租应用
     *
     * @param info 用户关键信息
     * @param dto  租户应用实体数据
     */
    @Override
    public void rentTenantApp(LoginInfo info, TenantApp dto) {
        Long tenantId = dto.getTenantId();
        Tenant tenant = mapper.getTenant(tenantId);
        if (tenant == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        LocalDate expire = dto.getExpireDate();
        if (expire == null || LocalDate.now().isAfter(expire)) {
            throw new BusinessException("到期日期无效");
        }

        mapper.rentTenant(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, tenantId, dto);

        // 更新缓存数据
        String key = "App:" + dto.getAppId();
        if (Redis.hasKey(key)) {
            Redis.setHash(key, tenantId.toString(), dto.getExpireDate());
        }
    }

    /**
     * 获取日志列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    @Override
    public Reply getTenantLogs(Search search) {
        return client.getLogs(BUSINESS, search.getKeyword(), search.getPageNum(), search.getPageSize());
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @Override
    public Reply getTenantLog(Long id) {
        return client.getLog(id);
    }
}
