package com.example.hxds.odr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import javax.annotation.Resource;

@Configuration
public class RedisConfiguration {

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    //这是最重要的
    @Bean
    public ChannelTopic expiredTopic(){
        /*
        自定义Redis队列的名字，如果有缓存销毁，就会自动的往队列中发送消息
        每个子系统都有各自的Redis逻辑库，订单子系统不会监听到其他子系统缓存数据的销毁
         */
        return new ChannelTopic("__keyevent@5__:expired");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(){
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        return redisMessageListenerContainer;
    }
}
