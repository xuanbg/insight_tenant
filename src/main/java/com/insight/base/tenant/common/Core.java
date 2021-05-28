package com.insight.base.tenant.common;

import com.insight.base.tenant.common.client.RabbitClient;
import com.insight.base.tenant.common.dto.RoleDto;
import com.insight.base.tenant.common.mapper.TenantMapper;
import com.insight.utils.Generator;
import com.insight.utils.pojo.LoginInfo;
import com.insight.utils.pojo.MemberDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019/11/30
 * @remark 租户核心类
 */
@Component
public class Core {
    private final TenantMapper mapper;

    /**
     * 构造方法
     *
     * @param mapper TenantMapper
     */
    public Core(TenantMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 通过消息队列初始化应用内置角色
     *
     * @param info     用户关键信息
     * @param tenantId 租户ID
     * @param appId    应用ID
     * @param members  角色成员集合
     */
    public void addRole(LoginInfo info, Long tenantId, Long appId, List<MemberDto> members) {
        RoleDto role = new RoleDto();
        role.setTenantId(tenantId);
        role.setAppId(appId);
        role.setMembers(members);
        role.setCreator(info.getUserName());
        role.setCreatorId(info.getUserId());

        RabbitClient.sendTopic("tenant.addRole", role);
    }

    /**
     * 获取租户编码
     *
     * @return 租户编码
     */
    public String getCode() {
        String group = "Base:Tenant";
        String format = "TI-#5";
        while (true) {
            String code = Generator.newCode(format, group, false);
            int count = mapper.getTenantCount(code);
            if (count > 0) {
                continue;
            }

            return code;
        }
    }
}
