package com.insight.base.tenant.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 宣炳刚
 * @date 2019-09-03
 * @remark Topic交换机配置
 */
@Configuration
public class TopicExchangeConfig {

    /**
     * Topic交换机
     *
     * @return TopicExchange
     */
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("amq.topic");
    }

    /**
     * 新增用户队列
     *
     * @return Queue
     */
    @Bean
    public Queue userQueue() {
        return new Queue("insight.user");
    }

    /**
     * 新增角色队列
     *
     * @return Queue
     */
    @Bean
    public Queue roleQueue() {
        return new Queue("insight.role");
    }

    /**
     * 新增角色队列
     *
     * @return Queue
     */
    @Bean
    public Queue organizeQueue() {
        return new Queue("insight.organize");
    }

    /**
     * 默认用户绑定
     * @return Binding
     */
    @Bean
    public Binding userBinding(){
        return BindingBuilder.bind(userQueue()).to(exchange()).with("tenant.addUser");
    }

    /**
     * 默认角色绑定
     * @return Binding
     */
    @Bean
    public Binding roleBinding(){
        return BindingBuilder.bind(roleQueue()).to(exchange()).with("tenant.addRole");
    }

    /**
     * 默认角色绑定
     * @return Binding
     */
    @Bean
    public Binding organizeBinding(){
        return BindingBuilder.bind(organizeQueue()).to(exchange()).with("tenant.addOrganize");
    }
}
