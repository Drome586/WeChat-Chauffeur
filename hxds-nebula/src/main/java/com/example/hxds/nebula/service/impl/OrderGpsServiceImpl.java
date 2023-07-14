package com.example.hxds.nebula.service.impl;

import cn.hutool.core.map.MapUtil;
import com.example.hxds.nebula.controller.vo.InsertOrderGpsVo;
import com.example.hxds.nebula.db.dao.OrderGpsDao;
import com.example.hxds.nebula.db.pojo.OrderGpsEntity;
import com.example.hxds.nebula.service.OrderGpsService;
import com.example.hxds.nebula.util.LocationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class OrderGpsServiceImpl implements OrderGpsService {

    @Resource
    private OrderGpsDao orderGpsDao;

    @Override
    @Transactional
    public int insertOrderGps(ArrayList<InsertOrderGpsVo> list) {
        int rows = 0;

        for(OrderGpsEntity entity:list){
            rows += orderGpsDao.insert(entity);
        }
        return rows;
    }

    @Override
    public ArrayList<HashMap> searchOrderGps(long orderId) {
        ArrayList<HashMap> list = orderGpsDao.searchOrderGpd(orderId);
        return list;
    }

    @Override
    public HashMap searchOrderLastGps(long orderId) {
        HashMap map = orderGpsDao.searchOrderLastGps(orderId);
        return map;
    }

    @Override
    public String calculateOrderMileage(long orderId) {
        ArrayList<HashMap> list = orderGpsDao.searchOrderAllGps(orderId);
        double mileage = 0;
        for (int i = 0; i < list.size(); i++) {
            if (i != list.size() - 1) {
                HashMap map_1 = list.get(i);
                HashMap map_2 = list.get(i+1);
                double latitude_1 = MapUtil.getDouble(map_1, "latitude");
                double longitude_1 = MapUtil.getDouble(map_1, "longitude");
                double latitude_2 = MapUtil.getDouble(map_2, "latitude");
                double longitude_2 = MapUtil.getDouble(map_2, "longitude");
                double distance = LocationUtil.getDistance(latitude_1, longitude_1, latitude_2, longitude_2);
                mileage += distance;
            }
        }
        return mileage + "";
    }
}
