package com.insight.base.tenant.common.dto;

import com.insight.base.tenant.common.entity.CompanyInfo;
import com.insight.util.Json;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 宣炳刚
 * @date 2019/05/20
 * @remark 租户查询对象实体
 */
public class TenantDTO  implements Serializable {
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
     * 过期日期
     */
    private Date expireDate;

    /**
     * 租户状态：0、待审核；1、已通过；2、未通过
     */
    private Integer status;

    /**
     * 审核人
     */
    private String auditor;

    /**
     * 审核时间
     */
    private Date auditedTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createdTime;

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

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public Date getAuditedTime() {
        return auditedTime;
    }

    public void setAuditedTime(Date auditedTime) {
        this.auditedTime = auditedTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString(){
        return Json.toJson(this);
    }
}
