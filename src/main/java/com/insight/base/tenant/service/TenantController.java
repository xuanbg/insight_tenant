package com.insight.base.tenant.service;

import com.insight.util.pojo.Reply;
import com.insight.util.pojo.SearchDTO;
import com.insight.util.service.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author 宣炳刚
 * @date 2017/12/18
 * @remark 租户服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/base")
public class TenantController extends BaseController {
    private final TenantService service;

    @Autowired
    public TenantController(StringRedisTemplate redis, TenantService service) {
        super.setRedis(redis);
        this.service = service;
    }

    /**
     * 根据设定的条件查询租户信息(分页)
     *
     * @param token  访问令牌
     * @param search 租户查询实体对象
     * @return Reply
     */
    @GetMapping("/v1.0/tenants")
    public Reply getTenants(@RequestHeader("Authorization") String token, SearchDTO search) {

        Reply result = verify("getTenants");
        if (!result.getSuccess()) {
            return result;
        }

        return service.getTenants(search);
    }
}
