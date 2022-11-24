package com.insight.base.tenant.manage;

import com.insight.base.tenant.common.entity.Tenant;
import com.insight.base.tenant.common.entity.TenantApp;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/12/18
 * @remark 租户服务接口
 */
public interface TenantService {

    /**
     * 根据设定的条件查询租户信息(分页)
     *
     * @param search 查询实体类
     * @return Reply
     */
    Reply getTenants(Search search);

    /**
     * 查询指定ID的租户信息
     *
     * @param id 租户ID
     * @return Reply
     */
    Reply getTenant(Long id);

    /**
     * 查询指定ID的租户绑定的应用集合
     *
     * @param id 租户ID
     * @return Reply
     */
    Reply getTenantApps(Long id);

    /**
     * 获取指定ID的租户的用户集合
     *
     * @param search 查询实体类
     * @return Reply
     */
    Reply getTenantUsers(Search search);

    /**
     * 新增租户
     *
     * @param info 用户关键信息
     * @param dto  租户实体数据
     * @return Reply
     */
    Reply addTenant(LoginInfo info, Tenant dto);

    /**
     * 更新租户数据
     *
     * @param info 用户关键信息
     * @param dto  租户实体数据
     * @return Reply
     */
    Reply updateTenant(LoginInfo info, Tenant dto);

    /**
     * 审核租户
     *
     * @param info 用户关键信息
     * @param dto  租户实体数据
     * @return Reply
     */
    Reply auditTenant(LoginInfo info, Tenant dto);

    /**
     * 启用、禁用租户信息
     *
     * @param info   用户信息
     * @param id     租户ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    Reply updateTenantStatus(LoginInfo info, Long id, boolean status);

    /**
     * 删除指定ID的租户
     *
     * @param info 用户信息
     * @param id   租户ID
     * @return Reply
     */
    Reply deleteTenant(LoginInfo info, Long id);

    /**
     * 获取租户可用应用集合
     *
     * @param id 租户ID
     * @return Reply
     */
    Reply getUnboundApps(Long id);

    /**
     * 设置应用与指定ID的租户的绑定关系
     *
     * @param info   用户信息
     * @param id     租户ID
     * @param appIds 应用ID集合
     * @return Reply
     */
    Reply addAppsToTenant(LoginInfo info, Long id, List<Long> appIds);

    /**
     * 解除应用与指定ID的租户的绑定关系
     *
     * @param info   用户信息
     * @param id     租户ID
     * @param appIds 应用ID集合
     * @return Reply
     */
    Reply removeAppsFromTenant(LoginInfo info, Long id, List<Long> appIds);

    /**
     * 续租应用
     *
     * @param info 用户信息
     * @param dto  租户应用实体数据
     * @return Reply
     */
    Reply rentTenantApp(LoginInfo info, TenantApp dto);

    /**
     * 获取日志列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    Reply getTenantLogs(Search search);

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    Reply getTenantLog(Long id);
}
