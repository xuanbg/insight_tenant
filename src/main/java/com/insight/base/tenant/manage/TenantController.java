package com.insight.base.tenant.manage;

import com.insight.base.tenant.common.client.LogClient;
import com.insight.base.tenant.common.client.LogServiceClient;
import com.insight.base.tenant.common.dto.AppListDto;
import com.insight.base.tenant.common.entity.OperateType;
import com.insight.base.tenant.common.entity.Tenant;
import com.insight.base.tenant.common.entity.TenantApp;
import com.insight.utils.Json;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.BusinessException;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
    private static final String BUSINESS = "Tenant";
    private final LogServiceClient client;
    private final TenantService service;

    /**
     * 构造方法
     *
     * @param client  Feign客户端
     * @param service 自动注入的TenantService
     */
    public TenantController(LogServiceClient client, TenantService service) {
        this.client = client;
        this.service = service;
    }

    /**
     * 根据设定的条件查询租户信息(分页)
     *
     * @param search 查询实体类
     * @return Reply
     */
    @GetMapping("/v1.0/tenants")
    public Reply getTenants(Search search) {
        return service.getTenants(search);
    }

    /**
     * 查询指定ID的租户信息
     *
     * @param id 租户ID
     * @return Reply
     */
    @GetMapping("/v1.0/tenants/{id}")
    public Tenant getTenant(@PathVariable Long id) {
        return service.getTenant(id);
    }

    /**
     * 查询指定ID的租户绑定的应用集合
     *
     * @param id 租户ID
     * @return Reply
     */
    @GetMapping("/v1.0/tenants/{id}/apps")
    public List<AppListDto> getTenantApps(@PathVariable Long id) {
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
    public Reply getTenantUsers(@PathVariable Long id, Search search) {
        search.setTenantId(id);

        return service.getTenantUsers(search);
    }

    /**
     * 新增租户
     *
     * @param loginInfo   用户关键信息
     * @param tenant 租户实体数据
     * @return Reply
     */
    @PostMapping("/v1.0/tenants")
    public Long addTenant(@RequestHeader("loginInfo") String loginInfo, @Valid @RequestBody Tenant tenant) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);

        var id = service.addTenant(info, tenant);
        LogClient.writeLog(info, BUSINESS, OperateType.NEW, id, tenant);
        return id;
    }

    /**
     * 更新租户数据
     *
     * @param loginInfo   用户关键信息
     * @param id     租户ID
     * @param tenant 租户实体数据
     */
    @PutMapping("/v1.0/tenants/{id}")
    public void updateTenant(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id, @Valid @RequestBody Tenant tenant) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        tenant.setId(id);

        service.updateTenant(info, tenant);
        LogClient.writeLog(info, BUSINESS, OperateType.EDIT, id, tenant);
    }

    /**
     * 审核租户
     *
     * @param loginInfo   用户关键信息
     * @param id     租户ID
     * @param tenant 租户实体数据
     */
    @PutMapping("/v1.0/tenants/{id}/status")
    public void auditTenant(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id, @RequestBody Tenant tenant) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        tenant.setId(id);

        service.auditTenant(info, tenant);
        LogClient.writeLog(info, BUSINESS, OperateType.EDIT, id, tenant);
    }

    /**
     * 禁用租户信息
     *
     * @param loginInfo 用户关键信息
     * @param id   租户ID
     */
    @PutMapping("/v1.0/tenants/{id}/disable")
    public void disableTenant(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);

        service.updateTenantStatus(info, id, true);
        LogClient.writeLog(info, BUSINESS, OperateType.DISABLE, id, null);
    }

    /**
     * 启用租户信息
     *
     * @param loginInfo 用户关键信息
     * @param id   租户ID
     */
    @PutMapping("/v1.0/tenants/{id}/enable")
    public void enableTenant(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);

        service.updateTenantStatus(info, id, false);
        LogClient.writeLog(info, BUSINESS, OperateType.ENABLE, id, null);
    }

    /**
     * 删除指定ID的租户
     *
     * @param loginInfo 用户关键信息
     * @param id   租户ID
     */
    @DeleteMapping("/v1.0/tenants/{id}")
    public void deleteTenant(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);

        service.deleteTenant(info, id);
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, null);
    }

    /**
     * 查询指定ID的租户绑定的应用集合
     *
     * @param id 租户ID
     * @return Reply
     */
    @GetMapping("/v1.0/tenants/{id}/unbounds")
    public List<AppListDto> getUnboundApps(@PathVariable Long id) {
        return service.getUnboundApps(id);
    }

    /**
     * 设置应用与指定ID的租户的绑定关系
     *
     * @param loginInfo   用户关键信息
     * @param id     租户ID
     * @param appIds 应用ID集合
     */
    @PostMapping("/v1.0/tenants/{id}/apps")
    public void addAppsToTenant(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id, @RequestBody List<Long> appIds) {
        if (appIds == null || appIds.isEmpty()) {
            throw new BusinessException("请选择需要绑定的应用");
        }

        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        service.addAppsToTenant(info, id, appIds);
        LogClient.writeLog(info, BUSINESS, OperateType.NEW, id, appIds);
    }

    /**
     * 移除应用与指定ID的租户的绑定关系
     *
     * @param loginInfo   用户关键信息
     * @param id     租户ID
     * @param appIds 应用ID集合
     */
    @DeleteMapping("/v1.0/tenants/{id}/apps")
    public void removeAppsFromTenant(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id, @RequestBody List<Long> appIds) {
        if (appIds == null || appIds.isEmpty()) {
            throw new BusinessException("请选择需要解除绑定的应用");
        }

        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        service.removeAppsFromTenant(info, id, appIds);
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, appIds);
    }

    /**
     * 续租应用
     *
     * @param loginInfo 用户关键信息
     * @param id   租户ID
     * @param dto  租户应用实体数据
     */
    @PutMapping("/v1.0/tenants/{id}/apps")
    public void rentTenantApp(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id, @RequestBody TenantApp dto) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        dto.setTenantId(id);

        service.rentTenantApp(info, dto);
        LogClient.writeLog(info, BUSINESS, OperateType.EDIT, id, dto);
    }

    /**
     * 查询日志
     *
     * @param loginInfo 用户登录信息
     * @param search    查询条件
     * @return 日志集合
     */
    @GetMapping("/v1.0/tenants/{id}/logs")
    public Reply getAirportLogs(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id, Search search) {
        var info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        return client.getLogs(id, "Tenant", search.getKeyword());
    }

    /**
     * 获取日志
     *
     * @param loginInfo 用户登录信息
     * @param id        日志ID
     * @return 日志VO
     */
    @GetMapping("/v1.0/tenants/logs/{id}")
    public Reply getAirportLog(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id) {
        var info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        return client.getLog(id);
    }
}
