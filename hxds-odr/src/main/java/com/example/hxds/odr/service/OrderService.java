package com.example.hxds.odr.service;

import com.example.hxds.odr.db.pojo.OrderBillEntity;
import com.example.hxds.odr.db.pojo.OrderEntity;

import java.util.HashMap;
import java.util.Map;

public interface OrderService {
    public HashMap searchDriverTodayBusinessData(long driverId);

    public String insertOrder(OrderEntity orderEntity, OrderBillEntity orderBillEntity);

    public String acceptNewOrder(long driverId,long orderId);

    public HashMap searchDriverExecuteOrder(HashMap param);
}