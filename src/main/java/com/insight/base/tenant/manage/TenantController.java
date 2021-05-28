package com.insight.base.tenant.manage;

import com.insight.base.tenant.common.entity.Tenant;
import com.insight.base.tenant.common.entity.TenantApp;
import com.insight.utils.Json;
import com.insight.utils.ReplyHelper;
import com.insight.utils.pojo.LoginInfo;
import com.insight.utils.pojo.Reply;
import com.insight.utils.pojo.SearchDto;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/12/18
 * @remark 租户服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/base/tenant")
public class TenantController {
    private final TenantService service;

    /**
     * 构造方法
     *
     * @param service 自动注入的TenantService
     */
    public TenantController(TenantService service) {
        this.service = service;
    }

    /**
     * 根据设定的条件查询租户信息(分页)
     *
     * @param search 查询实体类
     * @return Reply
     */
    @GetMapping("/v1.0/tenants")
    public Reply getTenants(SearchDto search) {
        return service.getTenants(search);
    }

    /**
     * 查询指定ID的租户信息
     *
     * @param id 租户ID
     * @return Reply
     */
    @GetMapping("/v1.0/tenants/{id}")
    public Reply getTenant(@PathVariable Long id) {
        return service.getTenant(id);
    }

    /**
     * 查询指定ID的租户绑定的应用集合
     *
     * @param id 租户ID
     * @return Reply
     */
    @GetMapping("/v1.0/tenants/{id}/apps")
    public Reply getTenantApps(@PathVariable Long id) {
        return service.getTenantApps(id);
    }

    /**
     * 获取指定ID的租户的用户集合
     *
     * @param id     租户ID
     * @param search 查询实体类
     * @return Reply
     */
    @GetMapping("/v1.0/tenants/{id}/users")
    public Reply getTenantUsers(@PathVariable Long id, SearchDto search) {
        search.setTenantId(id);

        return service.getTenantUsers(search);
    }

    /**
     * 新增租户
     *
     * @param info   用户关键信息
     * @param tenant 租户实体数据
     * @return Reply
     */
    @PostMapping("/v1.0/tenants")
    public Reply addTenant(@RequestHeader("loginInfo") String info, @Valid @RequestBody Tenant tenant) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.addTenant(loginInfo, tenant);
    }

    /**
     * 更新租户数据
     *
     * @param info   用户关键信息
     * @param tenant 租户实体数据
     * @return Reply
     */
    @PutMapping("/v1.0/tenants")
    public Reply updateTenant(@RequestHeader("loginInfo") String info, @Valid @RequestBody Tenant tenant) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.updateTenant(loginInfo, tenant);
    }

    /**
     * 审核租户
     *
     * @param info   用户关键信息
     * @param tenant 租户实体数据
     * @return Reply
     */
    @PutMapping("/v1.0/tenants/status")
    public Reply auditTenant(@RequestHeader("loginInfo") String info, @RequestBody Tenant tenant) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.auditTenant(loginInfo, tenant);
    }

    /**
     * 禁用租户信息
     *
     * @param info 用户关键信息
     * @param id   租户ID
     * @return Reply
     */
    @PutMapping("/v1.0/tenants/disable")
    public Reply disableTenant(@RequestHeader("loginInfo") String info, @RequestBody Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.updateTenantStatus(loginInfo, id, true);
    }

    /**
     * 启用租户信息
     *
     * @param info 用户关键信息
     * @param id   租户ID
     * @return Reply
     */
    @PutMapping("/v1.0/tenants/enable")
    public Reply enableTenant(@RequestHeader("loginInfo") String info, @RequestBody Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.updateTenantStatus(loginInfo, id, false);
    }

    /**
     * 删除指定ID的租户
     *
     * @param info 用户关键信息
     * @param id   租户ID
     * @return Reply
     */
    @DeleteMapping("/v1.0/tenants")
    public Reply deleteTenant(@RequestHeader("loginInfo") String info, @RequestBody Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.deleteTenant(loginInfo, id);
    }

    /**
     * 查询指定ID的租户绑定的应用集合
     *
     * @param id 租户ID
     * @return Reply
     */
    @GetMapping("/v1.0/tenants/{id}/unbounds")
    public Reply getUnboundApps(@PathVariable Long id) {
        return service.getUnboundApps(id);
    }

    /**
     * 设置应用与指定ID的租户的绑定关系
     *
     * @param info   用户关键信息
     * @param id     租户ID
     * @param appIds 应用ID集合
     * @return Reply
     */
    @PostMapping("/v1.0/tenants/{id}/apps")
    public Reply addAppsToTenant(@RequestHeader("loginInfo") String info, @PathVariable Long id, @RequestBody List<Long> appIds) {
        if (appIds == null || appIds.isEmpty()) {
            return ReplyHelper.invalidParam("请选择需要绑定的应用");
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.addAppsToTenant(loginInfo, id, appIds);
    }

    /**
     * 移除应用与指定ID的租户的绑定关系
     *
     * @param info   用户关键信息
     * @param id     租户ID
     * @param appIds 应用ID集合
     * @return Reply
     */
    @DeleteMapping("/v1.0/tenants/{id}/apps")
    public Reply removeAppsFromTenant(@RequestHeader("loginInfo") String info, @PathVariable Long id, @RequestBody List<Long> appIds) {
        if (appIds == null || appIds.isEmpty()) {
            return ReplyHelper.invalidParam("请选择需要解除绑定的应用");
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.removeAppsFromTenant(loginInfo, id, appIds);
    }

    /**
     * 续租应用
     *
     * @param info 用户关键信息
     * @param id   租户ID
     * @param dto  租户应用实体数据
     * @return Reply
     */
    @PutMapping("/v1.0/tenants/{id}/apps")
    public Reply rentTenantApp(@RequestHeader("loginInfo") String info, @PathVariable Long id, @RequestBody TenantApp dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        dto.setTenantId(id);

        return service.rentTenantApp(loginInfo, dto);
    }

    /**
     * 获取日志列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    @GetMapping("/v1.0/tenants/logs")
    public Reply getTenantLogs(SearchDto search) {
        return service.getTenantLogs(search);
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @GetMapping("/v1.0/tenants/logs/{id}")
    Reply getTenantLog(@PathVariable Long id) {
        if (id == null) {
            return ReplyHelper.invalidParam();
        }

        return service.getTenantLog(id);
    }
}
