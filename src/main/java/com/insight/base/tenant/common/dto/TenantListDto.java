package com.insight.base.tenant.common.dto;

import com.insight.base.tenant.common.entity.CompanyInfo;
import com.insight.utils.Json;

import java.io.Serializable;

/**
 * @author 宣炳刚
 * @date 2019/05/20
 * @remark 租户查询对象实体
 */
public class TenantListDto implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * UUID主键
     */
    private String id;

    /**
     * 租户编码
     */
    private String code;

    /**
     * 租户名称
     */
    private String name;

    /**
     * 租户别名
     */
    private String alias;

    /**
     * 企业信息
     */
    private CompanyInfo companyInfo;

    /**
     * 描述
     */
    private String remark;

    /**
     * 租户状态：0、待审核；1、已通过；2、未通过
     */
    private Integer status;

    /**
     * 是否失效：0、正常；1、失效
     */
    private Boolean isInvalid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public CompanyInfo getCompanyInfo() {
        return companyInfo;
    }

    public void setCompanyInfo(CompanyInfo companyInfo) {
        this.companyInfo = companyInfo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getInvalid() {
        return isInvalid;
    }

    public void setInvalid(Boolean invalid) {
        this.isInvalid = invalid;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
