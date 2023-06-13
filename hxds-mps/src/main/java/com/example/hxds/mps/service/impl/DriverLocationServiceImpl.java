package com.example.hxds.mps.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import com.example.hxds.mps.service.DriverLocationService;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class DriverLocationServiceImpl implements DriverLocationService {

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void updateLocationCache(Map param) {
        long driverId = MapUtil.getLong(param, "driverId");
        String latitude = MapUtil.getStr(param, "latitude");
        String longitude = MapUtil.getStr(param, "longitude");

        //接单范围
        int rangeDistance = MapUtil.getInt(param, "rangeDistance");
        //订单里程范围
        int orderDistance = MapUtil.getInt(param, "orderDistance");

        //封装成对象才能缓存到redis里面
        Point point = new Point(Convert.toDouble(longitude), Convert.toDouble(latitude));

        /*
        把司机实时定位信息缓存到redis里面，便于GEO的计算
        GEO是集合形式，如果设置过期时间，所有司机的定位缓存就全都失效了
        正确做法是司机上线后，更新GEO中的缓存定位
        driverId+"" 是将int型转换为字符串类型，并且留的是司机的定位点的坐标，并不是景点的名字
         */
        redisTemplate.opsForGeo().add("driver_location",point,driverId+"");

        //定向接单地址的经度
        String orientateLongitude = null;
        if(param.get("orientateLongitude") != null){
            orientateLongitude = MapUtil.getStr(param,"orientateLongitude");
        }

        //定向接单地址的纬度
        String orientateLatitude = null;
        if(param.get("orientateLatitude") != null){
            orientateLatitude = MapUtil.getStr(param,"orientateLatitude");
        }

        //定向接单经纬度的字符串
        String orientation = "none";
        if(orientateLongitude != null && orientateLatitude != null){
            orientation = orientateLatitude + "," + orientateLongitude;
        }

        //为了判断哪些司机在线，还需要单独弄一个线上缓存，缓存司机接单设置（定向接单，接单范围，订单总里程）

        String temp = rangeDistance+"#"+orderDistance+"#"+orientation;
        redisTemplate.opsForValue().set("driver_online#" + driverId,temp,60, TimeUnit.SECONDS);

    }

    @Override
    public void removeLocationCache(long driverId) {
        //删除司机定位缓存
        redisTemplate.opsForGeo().remove("driver_location",driverId + "");
        //删除司机上线缓存
        redisTemplate.delete("driver_online#" + driverId);
    }
}
