package com.insight.base.tenant.manage;

import com.insight.base.tenant.common.entity.Tenant;
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.Reply;

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
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    Reply getTenants(String keyword, int page, int size);

    /**
     * 查询指定ID的租户信息
     *
     * @param id 租户ID
     * @return Reply
     */
    Reply getTenant(String id);

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
     * 续租
     *
     * @param info 用户信息
     * @param dto  租户实体数据
     * @return Reply
     */
    Reply rentTenant(LoginInfo info, Tenant dto);

    /**
     * 启用、禁用租户信息
     *
     * @param info   用户信息
     * @param id     租户ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    Reply updateTenantStatus(LoginInfo info, String id, boolean status);

    /**
     * 删除指定ID的租户
     *
     * @param info 用户信息
     * @param id   租户ID
     * @return Reply
     */
    Reply deleteTenant(LoginInfo info, String id);

    /**
     * 查询指定ID的租户绑定的应用集合
     *
     * @param id 租户ID
     * @return Reply
     */
    Reply getTenantApps(String id);

    /**
     * 设置应用与指定ID的租户的绑定关系
     *
     * @param info   用户信息
     * @param id     租户ID
     * @param appIds 应用ID集合
     * @return Reply
     */
    Reply addAppsToTenant(LoginInfo info, String id, List<String> appIds);

    /**
     * 解除应用与指定ID的租户的绑定关系
     *
     * @param info   用户信息
     * @param id     租户ID
     * @param appIds 应用ID集合
     * @return Reply
     */
    Reply removeAppsFromTenant(LoginInfo info, String id, List<String> appIds);

    /**
     * 获取日志列表
     *
     * @param tenantId 租户ID
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    Reply getTemplateLogs(String tenantId, String keyword, int page, int size);

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    Reply getTemplateLog(String id);
}
