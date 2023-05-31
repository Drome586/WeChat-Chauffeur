package com.example.hxds.bff.driver.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenFeignConf {

    @Bean
    Logger.Level feignLevel() {
        return Logger.Level.FULL;
    }
}
