package com.insight.base.tenant.common.mapper;

import com.insight.base.tenant.common.dto.AppListDto;
import com.insight.base.tenant.common.dto.TenantListDto;
import com.insight.base.tenant.common.dto.UserListDto;
import com.insight.base.tenant.common.entity.CompanyInfo;
import com.insight.base.tenant.common.entity.Tenant;
import com.insight.base.tenant.common.entity.TenantApp;
import com.insight.utils.pojo.base.JsonTypeHandler;
import com.insight.utils.pojo.base.Search;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019/11/30
 * @remark 租户DAL
 */
@Mapper
public interface TenantMapper {

    /**
     * 根据登录账号查询用户数量
     *
     * @param key 关键词(账号/手机号/E-mail/微信unionId)
     * @return 用户数量
     */
    @Select("select count(*) from ibu_user WHERE account = #{key} or mobile = #{key} or email = #{key} or union_id = #{key} limit 1;")
    int getUserCount(String key);

    /**
     * 获取租户列表
     *
     * @param search 查询关键词
     * @return 租户列表
     */
    @Results({@Result(property = "companyInfo", column = "company_info", javaType = CompanyInfo.class, typeHandler = JsonTypeHandler.class)})
    @Select("""
            <script>select id, code, name, alias, company_info, remark, status, invalid
            from ibt_tenant
            where id > 13
              <if test = 'keyword != null'>and (code = #{keyword} or name like concat('%',#{keyword},'%') or alias = #{keyword})</if>
            </script>
            """)
    List<TenantListDto> getTenants(Search search);

    /**
     * 获取租户详情
     *
     * @param id 租户ID
     * @return 租户详情
     */
    @Results({@Result(property = "companyInfo", column = "company_info", javaType = CompanyInfo.class, typeHandler = JsonTypeHandler.class)})
    @Select("select * from ibt_tenant where id = #{id};")
    Tenant getTenant(Long id);

    /**
     * 获取租户绑定的应用集合
     *
     * @param id 租户ID
     * @return 租户绑定的应用集合
     */
    @Select("select a.id, r.tenant_id, a.name, a.icon, a.domain, r.expire_date from ibt_tenant_app r join ibs_application a on a.id = r.app_id where r.tenant_id = #{id};")
    List<AppListDto> getTenantApps(Long id);

    /**
     * 获取指定ID的租户的用户集合
     *
     * @param search 查询关键词
     * @return 用户集合
     */
    @Select("select u.id, u.code, u.name, u.account, u.mobile, u.remark, u.builtin, u.invalid " +
            "from ibu_user u join ibt_tenant_user r on r.user_id = u.id and r.tenant_id = #{tenantId}")
    List<UserListDto> getTenantUsers(Search search);

    /**
     * 获取指定编码的租户数量
     *
     * @param code 租户编码
     * @return 租户数量
     */
    @Select("select count(*) from ibt_tenant where code = #{code};")
    int getTenantCount(String code);

    /**
     * 新增租户
     *
     * @param tenant 租户DTO
     */
    @Insert("insert ibt_tenant(id, code, name, alias, area_code, company_info, remark, creator, creator_id, created_time) values " +
            "(#{id}, #{code}, #{name}, #{alias}, #{areaCode}, #{companyInfo, typeHandler = com.insight.utils.pojo.base.JsonTypeHandler}, " +
            "#{remark}, #{creator}, #{creatorId}, #{createdTime});")
    void addTenant(Tenant tenant);

    /**
     * 更新租户信息
     *
     * @param tenant 租户DTO
     */
    @Update("update ibt_tenant set name = #{name}, alias = #{alias}, area_code = #{areaCode}, " +
            "company_info = #{companyInfo, typeHandler = com.insight.utils.pojo.base.JsonTypeHandler}, remark = #{remark} where id = #{id};")
    void editTenant(Tenant tenant);

    /**
     * 审核租户
     *
     * @param id     租户ID
     * @param status 租户状态
     */
    @Update("update ibt_tenant set status = #{status} where id = #{id};")
    void auditTenant(@Param("id") Long id, @Param("status") int status);

    /**
     * 应用续租
     *
     * @param tenantApp 租户应用DTO
     */
    @Update("update ibt_tenant_app set expire_date = #{expireDate} where tenant_id = #{tenantId} and app_id = #{appId};")
    void rentTenant(TenantApp tenantApp);

    /**
     * 删除租户
     *
     * @param id 租户ID
     */
    @Delete("delete t, a, u, o, om, g, gm, r, rm, f from ibt_tenant t " +
            "left join ibt_tenant_app a on a.tenant_id = t.id left join ibt_tenant_user u on u.tenant_id = t.id " +
            "left join ibo_organize o on o.tenant_id = t.id left join ibo_organize_member om on om.post_id = o.id " +
            "left join ibu_group g on g.tenant_id = t.id left join ibu_group_member gm on gm.group_id = g.id " +
            "left join ibr_role r on r.tenant_id = t.id left join ibr_role_member rm on rm.role_id = r.id " +
            "left join ibr_role_permit f on f.role_id = r.id where t.id = #{id};")
    void deleteTenant(Long id);

    /**
     * 禁用/启用租户
     *
     * @param id     租户ID
     * @param status 禁用/启用状态
     */
    @Update("update ibt_tenant set invalid = #{status} where id = #{id};")
    void changeTenantStatus(@Param("id") Long id, @Param("status") boolean status);

    /**
     * 获取租户绑定应用的角色数量
     *
     * @param id     租户ID
     * @param appIds 应用ID集合
     * @return 租户绑定应用的角色数量
     */
    @Delete("<script>select count(*) from ibr_role where tenant_id = #{id} and app_id in " +
            "(<foreach collection = \"list\" item = \"item\" index = \"index\" separator = \",\">" +
            "#{item}</foreach>);</script>")
    int getAppsRoleCount(@Param("id") Long id, @Param("list") List<Long> appIds);

    /**
     * 获取租户未绑定的应用集合
     *
     * @param id 租户ID
     * @return 应用集合
     */
    @Select("select a.id, #{id} as tenant_id, a.name, a.alias, a.icon, a.domain, date_add(curdate(), interval 90 day) as expire_date from ibs_application a " +
            "left join ibt_tenant_app r on r.app_id = a.id and r.tenant_id = #{id} where a.id != 134660498556715024 and r.id is null;")
    List<AppListDto> getUnboundApps(Long id);

    /**
     * 设置应用与指定ID的租户的绑定关系
     *
     * @param id     租户ID
     * @param appIds 应用ID集合
     */
    @Insert("<script>insert ibt_tenant_app (`tenant_id`, `app_id`, expire_date) values " +
            "<foreach collection = \"list\" item = \"item\" index = \"index\" separator = \",\">" +
            "(#{id}, #{item}, date_add(curdate(), interval 90 day))</foreach>;</script>")
    void addAppsToTenant(@Param("id") Long id, @Param("list") List<Long> appIds);

    /**
     * 解除应用与指定ID的租户的绑定关系
     *
     * @param id     租户ID
     * @param appIds 应用ID集合
     */
    @Delete("<script>delete from ibt_tenant_app where tenant_id = #{id} and app_id in " +
            "(<foreach collection = \"list\" item = \"item\" index = \"index\" separator = \",\">" +
            "#{item}</foreach>);</script>")
    void removeAppsFromTenant(@Param("id") Long id, @Param("list") List<Long> appIds);

    /**
     * 新增租户-用户关系
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     */
    @Insert("insert ibt_tenant_user(tenant_id, user_id) values (#{tenantId}, #{userId});")
    void addRelation(@Param("tenantId") Long tenantId, @Param("userId") Long userId);


    /**
     * 新增组织机构-用户关系
     *
     * @param orgId  组织机构ID
     * @param userId 用户ID
     */
    @Insert("insert ibo_organize_member(post_id, user_id) values (#{orgId}, #{userId});")
    void addOrgRelation(Long orgId, Long userId);
}
