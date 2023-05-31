package com.example.hxds.bff.driver.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MultipartSupportConfig {

//    @Bean
//    public Encoder feignEncoder() {
//        return new SpringEncoder(feignHttpMessageConverter());
//    }
//
//    private ObjectFactory feignHttpMessageConverter() {
//        HttpMessageConverters httpMessageConverters = new HttpMessageConverters(getFastJsonConverter());
//        return () -> httpMessageConverters;
//    }
//
//    private FastJsonHttpMessageConverter getFastJsonConverter() {
//        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
//        // -------------------设置MediaType   没有这里会报错 Content-type 通配符问题
//        List<MediaType> supportedMediaTypes = new ArrayList<>();
//        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
//        supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
//        supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
//        supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
//        supportedMediaTypes.add(MediaType.APPLICATION_PDF);
//        supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
//        supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
//        supportedMediaTypes.add(MediaType.APPLICATION_XML);
//        supportedMediaTypes.add(MediaType.IMAGE_GIF);
//        supportedMediaTypes.add(MediaType.IMAGE_JPEG);
//        supportedMediaTypes.add(MediaType.IMAGE_PNG);
//        supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
//        supportedMediaTypes.add(MediaType.TEXT_HTML);
//        supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
//        supportedMediaTypes.add(MediaType.TEXT_PLAIN);
//        supportedMediaTypes.add(MediaType.TEXT_XML);
//        fastJsonHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
//        //修改配置返回内容的过滤
//        //QuoteFieldNames———-输出key时是否使用双引号,默认为true
//        //WriteMapNullValue——–是否输出值为null的字段,默认为false
//        //WriteNullNumberAsZero—-数值字段如果为null,输出为0,而非null
//        //WriteNullListAsEmpty—–List字段如果为null,输出为[],而非null
//        //WriteNullStringAsEmpty—字符类型字段如果为null,输出为”“,而非null
//        //WriteNullBooleanAsFalse–Boolean字段如果为null,输出为false,而非null
//        //DisableCircularReferenceDetect ：消除对同一对象循环引用的问题，默认为false（如果不配置有可能会进入死循环）
//        FastJsonConfig config = new FastJsonConfig();
//        config.setSerializerFeatures(
//                SerializerFeature.QuoteFieldNames,
//                SerializerFeature.BrowserCompatible,
//                SerializerFeature.WriteSlashAsSpecial,
//                SerializerFeature.DisableCircularReferenceDetect
//        );
//        fastJsonHttpMessageConverter.setFastJsonConfig(config);
//        return fastJsonHttpMessageConverter;
//
//    }


}