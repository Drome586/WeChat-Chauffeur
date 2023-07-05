package com.example.hxds.odr.service;

import com.example.hxds.odr.db.pojo.OrderBillEntity;
import com.example.hxds.odr.db.pojo.OrderEntity;

import java.util.HashMap;
import java.util.Map;

public interface OrderService {
    public HashMap searchDriverTodayBusinessData(long driverId);

    public String insertOrder(OrderEntity orderEntity, OrderBillEntity orderBillEntity);

    public String acceptNewOrder(long driverId,long orderId);

    public HashMap searchDriverExecuteOrder(Map param);

    public Integer searchOrderStatus(Map param);

    public String deleteUnAcceptOrder(Map param);

    public HashMap searchDriverCurrentOrder(long driverId);

    public HashMap hasCustomerCurrentOrder(long customerId);

    public HashMap searchOrderForMoveById(Map param);

    public int arriveStartPlace(Map param);

    //乘客端确认司机已到达
    public boolean confirmArriveStartPlace(long orderId);

    public int startDriving(Map param);

    //更新代驾过程中的状态，结束代驾
    public int updateOrderStatus(Map param);
}