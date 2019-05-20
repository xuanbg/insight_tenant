package com.insight.base.tenant.service;

import com.insight.base.tenant.common.dto.TenantDTO;
import com.insight.base.tenant.common.entity.Tenant;
import com.insight.util.pojo.Reply;
import com.insight.util.pojo.SearchDTO;
import com.insight.util.pojo.UserInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/12/18
 * @remark 租户服务接口实现类
 */
@Service
public class TenantServiceImpl implements TenantService{
    /**
     * 根据设定的条件查询租户信息(分页)
     *
     * @param search 租户查询实体对象
     * @return Reply
     */
    @Override
    public Reply getTenants(SearchDTO search) {
        return null;
    }

    /**
     * 新增租户
     *
     * @param info   访问令牌
     * @param tenant 租户实体数据
     * @return Reply
     */
    @Override
    public Reply addTenant(UserInfo info, TenantDTO tenant) {
        return null;
    }

    /**
     * 查询指定ID的租户信息
     *
     * @param id 租户ID
     * @return Reply
     */
    @Override
    public Reply getTenant(String id) {
        return null;
    }

    /**
     * 更新租户数据
     *
     * @param info   访问令牌
     * @param tenant 租户实体数据
     * @return Reply
     */
    @Override
    public Reply updateTenant(UserInfo info, TenantDTO tenant) {
        return null;
    }

    /**
     * 审核租户
     *
     * @param info   访问令牌
     * @param tenant 租户实体数据
     * @return Reply
     */
    @Override
    public Reply auditTenant(UserInfo info, TenantDTO tenant) {
        return null;
    }

    /**
     * 续租
     *
     * @param info 用户信息
     * @param id   租户id
     * @param days 续租天数
     * @return Reply
     */
    @Override
    public Reply rentTenant(UserInfo info, String id, Integer days) {
        return null;
    }

    /**
     * 启用、禁用租户信息
     *
     * @param info   用户信息
     * @param tenant 租户信息
     * @return Reply
     */
    @Override
    public Reply updateTenantStatus(UserInfo info, Tenant tenant) {
        return null;
    }

    /**
     * 删除指定ID的租户
     *
     * @param info 用户信息
     * @param id   租户ID
     * @return Reply
     */
    @Override
    public Reply deleteTenant(UserInfo info, String id) {
        return null;
    }

    /**
     * 设置应用与指定ID的租户的绑定关系
     *
     * @param info   用户信息
     * @param id     租户ID
     * @param appIds 应用ID集合
     * @return Reply
     */
    @Override
    public Reply addAppsToTenant(UserInfo info, String id, List<String> appIds) {
        return null;
    }

    /**
     * 查询指定ID的租户绑定的应用集合
     *
     * @param tenantId 租户ID
     * @return Reply
     */
    @Override
    public Reply getTenantApps(String tenantId) {
        return null;
    }

    /**
     * 建立当前用户与指定ID的租户的绑定关系
     *
     * @param info 访问令牌
     * @param id   租户ID
     * @return Reply
     */
    @Override
    public Reply addUserToTenant(UserInfo info, String id) {
        return null;
    }

    /**
     * 解除当前用户与指定ID的租户的绑定关系
     *
     * @param info 访问令牌
     * @param id   租户ID
     * @return Reply
     */
    @Override
    public Reply removeUserFromTenant(UserInfo info, String id) {
        return null;
    }

    /**
     * 获取当前用户管理的全部租户信息
     *
     * @param userId 用户ID
     * @return Reply
     */
    @Override
    public Reply getMyTenants(String userId) {
        return null;
    }

    /**
     * 获取租户下用户信息
     *
     * @param id 租户ID
     * @return Reply
     */
    @Override
    public Reply getTenantUsers(String id) {
        return null;
    }
}
