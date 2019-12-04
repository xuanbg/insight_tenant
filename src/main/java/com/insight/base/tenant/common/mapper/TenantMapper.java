package com.insight.base.tenant.common.mapper;

import com.insight.base.tenant.common.dto.AppListDto;
import com.insight.base.tenant.common.dto.TenantListDto;
import com.insight.base.tenant.common.entity.CompanyInfo;
import com.insight.base.tenant.common.entity.Tenant;
import com.insight.util.common.JsonTypeHandler;
import com.insight.util.pojo.Log;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019/11/30
 * @remark 租户DAL
 */
@Mapper
public interface TenantMapper {

    /**
     * 获取租户列表
     *
     * @param key 查询关键词
     * @return 租户列表
     */
    @Select("<script>select id, code, name, remark, expire_date, status, is_invalid from ibt_tenant " +
            "<if test = 'key != null'>where code = #{key} or name like concat('%',#{key},'%')</if>" +
            "order by created_time</script>")
    List<TenantListDto> getTenants(@Param("key") String key);

    /**
     * 获取租户详情
     *
     * @param id 租户ID
     * @return 租户详情
     */
    @Results({@Result(property = "companyInfo", column = "company_info", javaType = CompanyInfo.class, typeHandler = JsonTypeHandler.class)})
    @Select("select * from ibt_tenant where id = #{id};")
    Tenant getTenant(String id);

    /**
     * 获取指定编码的租户数量
     *
     * @param code 租户编码
     * @return 租户数量
     */
    @Select("select count(*) from ibt_tenant where code = #{code};")
    int getTenantCount(@Param("code") String code);

    /**
     * 新增租户
     *
     * @param tenant 租户DTO
     */
    @Insert("insert ibt_tenant(id, code, name, alias, company_info, remark, expire_date, creator, creator_id, created_time) values " +
            "(#{id}, #{code}, #{name}, #{alias}, #{companyInfo, typeHandler = com.insight.util.common.JsonTypeHandler}, #{remark}, #{expireDate}, #{creator}, #{creatorId}, #{createdTime});")
    void addTenant(Tenant tenant);

    /**
     * 更新租户信息
     *
     * @param tenant 租户DTO
     */
    @Update("update ibt_tenant set name = #{name}, alias = #{alias}, company_info = #{companyInfo, typeHandler = com.insight.util.common.JsonTypeHandler}, remark = #{remark} where id = #{id};")
    void editTenant(Tenant tenant);

    /**
     * 审核租户
     *
     * @param id     租户ID
     * @param status 租户状态
     */
    @Update("update ibt_tenant set status = #{status} where id = #{id};")
    void auditTenant(@Param("id") String id, @Param("status") int status);

    /**
     * 续租
     *
     * @param id     租户ID
     * @param expire 到期日期
     */
    @Update("update ibt_tenant set expire_date = #{expire} where id = #{id};")
    void rentTenant(@Param("id") String id, @Param("expire") LocalDate expire);

    /**
     * 删除租户
     *
     * @param id 租户ID
     */
    @Delete("delete from ibt_tenant where id = #{id};")
    void deleteTenant(String id);

    /**
     * 禁用/启用租户
     *
     * @param id     租户ID
     * @param status 禁用/启用状态
     */
    @Update("update ibt_tenant set is_invalid = #{status} where id = #{id};")
    void changeTenantStatus(@Param("id") String id, @Param("status") boolean status);

    /**
     * 获取租户绑定的应用集合
     *
     * @param id 租户ID
     * @return 租户绑定的应用集合
     */
    @Select("select a.id, a.name, a.icon, a.domain from ibt_tenant_app r join ibs_application a on a.id = r.app_id where r.tenant_id = #{id};")
    List<AppListDto> getTenantApps(String id);

    /**
     * 设置应用与指定ID的租户的绑定关系
     *
     * @param id     租户ID
     * @param appIds 应用ID集合
     */
    @Insert("<script>insert ibt_tenant_app (`id`, `tenant_id`, `app_id`) values " +
            "<foreach collection = \"list\" item = \"item\" index = \"index\" separator = \",\">" +
            "(replace(uuid(), '-', ''), #{id}, #{item})</foreach>;</script>")
    void addAppsToTenant(@Param("id") String id, @Param("list") List<String> appIds);

    /**
     * 解除应用与指定ID的租户的绑定关系
     *
     * @param id     租户ID
     * @param appIds 应用ID集合
     */
    @Delete("<script>delete from ibt_tenant_app where tenant_id = #{id} and app_id in " +
            "(<foreach collection = \"list\" item = \"item\" index = \"index\" separator = \",\">" +
            "#{item}</foreach>);</script>")
    void removeAppsFromTenant(@Param("id") String id, @Param("list") List<String> appIds);

    /**
     * 获取操作日志列表
     *
     * @param tenantId 租户ID
     * @param business 业务类型
     * @param key      查询关键词
     * @return 操作日志列表
     */
    @Select("<script>select id, type, business, business_id, dept_id, creator, creator_id, created_time " +
            "from ibl_operate_log where business = #{business} " +
            "<if test = 'tenantId != null'>and tenant_id = #{tenantId} </if>" +
            "<if test = 'tenantId == null'>and tenant_id is null </if>" +
            "<if test = 'key!=null'>and (type = #{key} or business_id = #{key} or " +
            "dept_id = #{key} or creator = #{key} or creator_id = #{key}) </if>" +
            "order by created_time</script>")
    List<Log> getLogs(@Param("tenantId") String tenantId, @Param("business") String business, @Param("key") String key);

    /**
     * 获取操作日志列表
     *
     * @param id 日志ID
     * @return 操作日志列表
     */
    @Results({@Result(property = "content", column = "content", javaType = Object.class, typeHandler = JsonTypeHandler.class)})
    @Select("select * from ibl_operate_log where id = #{id};")
    Log getLog(String id);

    /**
     * 记录操作日志
     *
     * @param log 日志DTO
     */
    @Insert("insert ibl_operate_log(id, tenant_id, type, business, business_id, content, dept_id, creator, creator_id, created_time) values " +
            "(#{id}, #{tenantId}, #{type}, #{business}, #{businessId}, #{content, typeHandler = com.insight.util.common.JsonTypeHandler}, " +
            "#{deptId}, #{creator}, #{creatorId}, #{createdTime});")
    void addLog(Log log);
}
