package com.example.hxds.mps.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.hxds.common.exception.HxdsException;
import com.example.hxds.mps.service.MapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

@Service
@Slf4j
public class MapServiceImpl implements MapService {

    //预估里程API的地址，链接地址自动计算
    private String distanceUrl = "https://apis.map.qq.com/ws/distance/v1/matrix/";

    private String directionUrl = "https://apis.map.qq.com/ws/direction/v1/driving/";

    @Value("${tencent.map.key}")
    private String key;

    @Override
    public HashMap estimateOrderMileageAndMinute(String mode,
                                                 String startPlaceLatitude,
                                                 String startPlaceLongitude,
                                                 String endPlaceLatitude,
                                                 String endPlaceLongitude) {
        HttpRequest req = new HttpRequest(distanceUrl);
        req.form("mode",mode);
        req.form("from",startPlaceLatitude + "," + startPlaceLongitude);
        req.form("to",endPlaceLatitude+","+endPlaceLongitude);
        req.form("key",key);
        HttpResponse resp = req.execute();
        JSONObject json = JSONUtil.parseObj(resp.body());
        int status = json.getInt("status");
        String message = json.getStr("message");
        log.info(message);
        if(status != 0){
            log.error(message);
            throw new HxdsException("预估里程异常" + message);
        }
        JSONArray rows = json.getJSONObject("result").getJSONArray("rows");
        JSONObject element = rows.get(0, JSONObject.class).getJSONArray("elements").get(0, JSONObject.class);
        int distance = element.getInt("distance");
        String mileage = new BigDecimal(distance).divide(new BigDecimal(1000)).toString();
        int duration = element.getInt("duration");

        String temp = new BigDecimal(duration).divide(new BigDecimal(60), 0, RoundingMode.CEILING).toString();
        int minute = Integer.parseInt(temp);
        HashMap map = new HashMap(){{
            put("mileage",mileage);
            put("minute",minute);
        }};

        return map;
    }

    @Override
    public HashMap calculateDriveLine(String startPlaceLatitude, String startPlaceLongitude,
                                      String endPlaceLatitude, String endPlaceLongitude) {

        HttpRequest req = new HttpRequest(directionUrl);
        req.form("from",startPlaceLatitude+","+startPlaceLongitude);
        req.form("to",endPlaceLatitude+","+endPlaceLongitude);
        req.form("key",key);

        HttpResponse resp = req.execute();
        JSONObject json = JSONUtil.parseObj(resp.body());

        Integer status = json.getInt("status");
        if(status != 0){
            log.error("规划行进路线异常");
            throw new HxdsException("执行异常");
        }
        //result  中有多个最佳线路组合，并不是像估算时间和距离那样提取最小的那个，而是提取所有的，返回。
        JSONObject result = json.getJSONObject("result");
        HashMap map = result.toBean(HashMap.class);

        return map;
    }
}
