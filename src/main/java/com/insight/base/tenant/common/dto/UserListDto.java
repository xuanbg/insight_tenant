package com.insight.base.tenant.common.dto;

import com.insight.utils.Json;

import java.io.Serializable;

/**
 * @author 宣炳刚
 * @date 2020/3/9
 * @remark 用户列表DTO
 */
public class UserListDto implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 用户ID
     */
    private String id;

    /**
     * 用户编码
     */
    private String code;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 登录账号
     */
    private String account;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 用户E-mail
     */
    private String email;

    /**
     * 备注
     */
    private String remark;

    /**
     * 用户是否失效
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getInvalid() {
        return isInvalid;
    }

    public void setInvalid(Boolean invalid) {
        isInvalid = invalid;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
