package com.insight.base.tenant.common.dto;

import com.insight.util.Json;

/**
 * @author 宣炳刚
 * @date 2019/12/4
 * @remark 租户绑定应用DTO
 */
public class AppListDto {
    private static final long serialVersionUID = -1L;

    /**
     * 应用ID
     */
    private String id;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用图标
     */
    private String icon;

    /**
     * 应用域名
     */
    private String domain;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
