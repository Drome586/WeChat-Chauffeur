package com.example.hxds.mps.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import com.example.hxds.mps.service.DriverLocationService;
import com.example.hxds.mps.util.CoordinateTransform;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.springframework.data.geo.*;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisCommands;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
        driverId+""  是将int型转换为字符串类型，并且留的是司机的定位点的坐标，并不是景点的名字
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

    @Override
    public ArrayList searchBefittingDriverAboutOrder(double startPlaceLatitude,
                                                     double startPlaceLongitude,
                                                     double endPlaceLatitude,
                                                     double endPlaceLongitude,
                                                     double mileage) {
        //搜索订单起始点5公里以内的司机（目前是写死的）,redis里面对应存储的值应该是driverId
        Point point = new Point(startPlaceLongitude, startPlaceLatitude);
        //设置GEO距离单位为千米
        Metric metric = RedisGeoCommands.DistanceUnit.KILOMETERS;
        Distance distance = new Distance(5, metric);
        Circle circle = new Circle(point, distance);

        //创建FEO参数，规定返回的数据类型
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()  //结果中包含距离
                .includeCoordinates()   //结果中包含坐标
                .sortAscending();   //升序排列

        //执行GEO计算，获得查询结果
        GeoResults<RedisGeoCommands.GeoLocation<String>> radius =
                redisTemplate.opsForGeo().radius("driver_location", circle, args);

        //符合条件的司机Id都保存到List里面
        ArrayList list = new ArrayList();

        if(radius != null){
            Iterator<GeoResult<RedisGeoCommands.GeoLocation<String>>> iterator = radius.iterator();
            while(iterator.hasNext()){
                //可以打断点看一下这个result里面的内容
                GeoResult<RedisGeoCommands.GeoLocation<String>> result = iterator.next();
                RedisGeoCommands.GeoLocation<String> content = result.getContent();
                String driverId = content.getName();
                //找到司机与乘客之间的距离
                double dist = result.getDistance().getValue();
                //查看司机是否在线
                if(!redisTemplate.hasKey("driver_online#"+driverId)){
                    continue;
                }
                //如果司机是线上的状态,拿到司机的上线缓存
                Object obj = redisTemplate.opsForValue().get("driver_online#" + driverId);
                if(obj==null){
                    continue;
                }
                //司机的线上缓存包含三部分，分别是司机接单范围，订单里程，定位点的坐标（定向接单点），数据是以 #分割的
                String value = obj.toString();
                String[] temp = value.split("#");
                int rangeDistance = Integer.parseInt(temp[0]);
                int orderDistance = Integer.parseInt(temp[1]);
                String orientation = temp[2];

                //判断是否符合接单范围
                boolean bool_1 = dist <= rangeDistance;
                //判断订单里程是否符合范围，就是代驾的里程想要代驾多远
                boolean bool_2 = false;

                if(orderDistance == 0){
                    bool_2 = true;
                }else if(orderDistance == 5 && mileage > 0 && mileage <= 5){
                    bool_2 = true;
                }else if(orderDistance == 10 && mileage > 5 && mileage <= 10){
                    bool_2 = true;
                }else if(orderDistance == 15 && mileage > 10 && mileage <= 15){
                    bool_2 = true;
                }else if(orderDistance == 30 && mileage > 15 && mileage <=30){
                    bool_2 = true;
                }
                //判断定向接单是否符合
                boolean bool_3 = false;
                if(!orientation.equals("none")){
                    double orientationLatitude = Double.parseDouble(orientation.split(",")[0]);
                    double orientationLongitude = Double.parseDouble(orientation.split(",")[1]);
                    //将定向点的火星坐标，转换为GPS坐标
                    double[] location = CoordinateTransform.transformGCJ02ToWGS84(orientationLongitude, orientationLatitude);
                    GlobalCoordinates point_1 = new GlobalCoordinates(location[1], location[0]);
                    //把订单终点的火星坐标转换为GPS坐标
                    location = CoordinateTransform.transformGCJ02ToWGS84(endPlaceLongitude, endPlaceLatitude);
                    GlobalCoordinates point_2 = new GlobalCoordinates(location[1], location[0]);
                    //这里不需要Redis的GEO计算，直接用封装函数计算两个GPS坐标之间的距离
                    GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.WGS84, point_1, point_2);

                    //如果定向点距离订单终点距离再3公里以内，说明这个订单和司机定向点是顺路的
                    if(geoCurve.getEllipsoidalDistance() <= 3000){
                        bool_3 = true;
                    }
                }else{
                    bool_3 = true;
                }
                //匹配接单条件，返回适合接单的司机和距离，为什么要返回dist呢？后面会有语音播报提示，距离您。。公里有哪个订单？所以要有dist
                if(bool_1 && bool_2 && bool_3){
                    HashMap map = new HashMap() {{
                        put("driverId", driverId);
                        put("distance", dist);
                    }};
                    list.add(map);//把该司机添加到需要通知的列表里面去
                }
            }
        }

        return list;
    }
}
