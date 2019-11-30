package com.insight.base.tenant.manage;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insight.base.tenant.common.Core;
import com.insight.base.tenant.common.dto.TenantListDto;
import com.insight.base.tenant.common.entity.Tenant;
import com.insight.base.tenant.common.mapper.TenantMapper;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.Log;
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.OperateType;
import com.insight.util.pojo.Reply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.insight.util.Generator.uuid;

/**
 * @author 宣炳刚
 * @date 2017/12/18
 * @remark 租户服务接口实现类
 */
@Service
public class TenantServiceImpl implements TenantService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Core core;
    private final TenantMapper mapper;

    /**
     * 构造方法
     *
     * @param core   Core
     * @param mapper TenantMapper
     */
    public TenantServiceImpl(Core core, TenantMapper mapper) {
        this.core = core;
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
     * 新增租户
     *
     * @param info 访问令牌
     * @param dto  租户实体数据
     * @return Reply
     */
    @Override
    public Reply addTenant(LoginInfo info, Tenant dto) {
        String id = uuid();
        dto.setId(id);
        dto.setCode(core.getCode());
        dto.setExpireDate(LocalDate.now().plusDays(90));
        dto.setCreator(info.getUserName());
        dto.setCreatorId(info.getUserId());
        dto.setCreatedTime(LocalDateTime.now());

        mapper.addTenant(dto);
        core.writeLog(info, OperateType.INSERT, "租户管理", id, dto);

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
        core.writeLog(info, OperateType.UPDATE, "租户管理", id, dto);

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
    public Reply auditTenant(LoginInfo info, Tenant dto) {
        String id = dto.getId();
        Tenant tenant = mapper.getTenant(id);
        if (tenant == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        int status = dto.getStatus();
        if (status < 1 || status > 2) {
            return ReplyHelper.fail("审核状态码错误");
        }

        mapper.auditTenant(id, status);
        core.writeLog(info, OperateType.UPDATE, "租户管理", id, dto);

        return ReplyHelper.success();
    }

    /**
     * 续租
     *
     * @param info 用户关键信息
     * @param dto  租户实体数据
     * @return Reply
     */
    @Override
    public Reply rentTenant(LoginInfo info, Tenant dto) {
        String id = dto.getId();
        Tenant tenant = mapper.getTenant(id);
        if (tenant == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        LocalDate expire = dto.getExpireDate();
        if (expire == null || LocalDate.now().isAfter(expire)) {
            return ReplyHelper.fail("有效日期无效");
        }

        mapper.rentTenant(id, expire);
        core.writeLog(info, OperateType.UPDATE, "租户管理", id, dto);

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
        core.writeLog(info, OperateType.UPDATE, "租户管理", id, tenant);

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
        core.writeLog(info, OperateType.UPDATE, "租户管理", id, tenant);

        return ReplyHelper.success();
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
        return null;
    }

    /**
     * 查询指定ID的租户绑定的应用集合
     *
     * @param id 租户ID
     * @return Reply
     */
    @Override
    public Reply getTenantApps(String id) {
        return null;
    }

    /**
     * 获取日志列表
     *
     * @param tenantId 租户ID
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    @Override
    public Reply getTemplateLogs(String tenantId, String keyword, int page, int size) {
        PageHelper.startPage(page, size);
        List<Log> logs = core.getLogs(tenantId, "租户管理", keyword);
        PageInfo<Log> pageInfo = new PageInfo<>(logs);

        return ReplyHelper.success(logs, pageInfo.getTotal());
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @Override
    public Reply getTemplateLog(String id) {
        Log log = core.getLog(id);
        if (log == null) {
            return ReplyHelper.fail("ID不存在,未读取数据");
        }

        return ReplyHelper.success(log);
    }
}
