package com.insight.base.tenant.common;

import com.insight.base.tenant.common.mapper.TenantMapper;
import com.insight.util.pojo.Log;
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.OperateType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.insight.util.Generator.newCode;
import static com.insight.util.Generator.uuid;

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
     * 获取租户编码
     *
     * @return 租户编码
     */
    public String getCode() {
        while (true) {
            String code = newCode("TI-#5", "Tenant:", false);
            int count = mapper.getTenantCount(code);
            if (count > 0) {
                continue;
            }

            return code;
        }
    }

    /**
     * 记录操作日志
     *
     * @param info     用户关键信息
     * @param type     操作类型
     * @param business 业务名称
     * @param id       业务ID
     * @param content  日志内容
     */
    @Async
    public void writeLog(LoginInfo info, OperateType type, String business, String id, Object content) {
        Log log = new Log();
        log.setId(uuid());
        log.setType(type);
        log.setBusiness(business);
        log.setBusinessId(id);
        log.setContent(content);
        log.setCreator(info.getUserName());
        log.setCreatorId(info.getUserId());
        log.setCreatedTime(LocalDateTime.now());

        mapper.addLog(log);
    }

    /**
     * 获取操作日志列表
     *
     * @param tenantId 租户ID
     * @param business 业务类型
     * @param key      查询关键词
     * @return 操作日志列表
     */
    public List<Log> getLogs(String tenantId, String business, String key) {
        return mapper.getLogs(tenantId, business, key);
    }

    /**
     * 获取操作日志
     *
     * @param id 日志ID
     * @return 操作日志
     */
    public Log getLog(String id) {
        return mapper.getLog(id);
    }
}
