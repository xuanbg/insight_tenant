package com.insight.base.tenant.service;

import com.insight.base.tenant.common.dto.TenantDTO;
import com.insight.base.tenant.common.entity.Tenant;
import com.insight.util.pojo.Reply;
import com.insight.util.pojo.SearchDTO;
import com.insight.util.pojo.UserInfo;

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
     * @param search 租户查询实体对象
     * @return Reply
     */
    Reply getTenants(SearchDTO search);

    /**
     * 新增租户
     *
     * @param info   访问令牌
     * @param tenant 租户实体数据
     * @return Reply
     */
    Reply addTenant(UserInfo info, TenantDTO tenant);

    /**
     * 查询指定ID的租户信息
     *
     * @param id 租户ID
     * @return Reply
     */
    Reply getTenant(String id);

    /**
     * 更新租户数据
     *
     * @param info   访问令牌
     * @param tenant 租户实体数据
     * @return Reply
     */
    Reply updateTenant(UserInfo info, TenantDTO tenant);

    /**
     * 审核租户
     *
     * @param info   访问令牌
     * @param tenant 租户实体数据
     * @return Reply
     */
    Reply auditTenant(UserInfo info, TenantDTO tenant);

    /**
     * 续租
     *
     * @param info 用户信息
     * @param id   租户id
     * @param days 续租天数
     * @return Reply
     */
    Reply rentTenant(UserInfo info, String id, Integer days);

    /**
     * 启用、禁用租户信息
     *
     * @param info   用户信息
     * @param tenant 租户信息
     * @return Reply
     */
    Reply updateTenantStatus(UserInfo info, Tenant tenant);

    /**
     * 删除指定ID的租户
     *
     * @param info 用户信息
     * @param id   租户ID
     * @return Reply
     */
    Reply deleteTenant(UserInfo info, String id);

    /**
     * 设置应用与指定ID的租户的绑定关系
     *
     * @param info   用户信息
     * @param id     租户ID
     * @param appIds 应用ID集合
     * @return Reply
     */
    Reply addAppsToTenant(UserInfo info, String id, List<String> appIds);

    /**
     * 查询指定ID的租户绑定的应用集合
     *
     * @param tenantId 租户ID
     * @return Reply
     */
    Reply getTenantApps(String tenantId);

    /**
     * 建立当前用户与指定ID的租户的绑定关系
     *
     * @param info 访问令牌
     * @param id   租户ID
     * @return Reply
     */
    Reply addUserToTenant(UserInfo info, String id);

    /**
     * 解除当前用户与指定ID的租户的绑定关系
     *
     * @param info 访问令牌
     * @param id   租户ID
     * @return Reply
     */
    Reply removeUserFromTenant(UserInfo info, String id);

    /**
     * 获取当前用户管理的全部租户信息
     *
     * @param userId 用户ID
     * @return Reply
     */
    Reply getMyTenants(String userId);

    /**
     * 获取租户下用户信息
     *
     * @param id 租户ID
     * @return Reply
     */
    Reply getTenantUsers(String id);
}
