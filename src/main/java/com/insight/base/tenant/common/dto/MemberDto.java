package com.insight.base.tenant.common.dto;

import com.insight.util.Json;

import java.io.Serializable;

/**
 * @author 宣炳刚
 * @date 2019/12/6
 * @remark 角色成员DTO
 */
public class MemberDto implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 角色成员ID
     */
    private String id;

    /**
     * 成员类型:0.未定义;1.用户;2.用户组;3.职位
     */
    private Integer type;

    /**
     * 成员名称
     */
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
