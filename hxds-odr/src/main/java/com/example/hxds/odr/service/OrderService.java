package com.example.hxds.odr.service;

import cn.hutool.core.util.PageUtil;
import com.example.hxds.common.util.PageUtils;
import com.example.hxds.odr.db.pojo.OrderBillEntity;
import com.example.hxds.odr.db.pojo.OrderEntity;

import java.util.ArrayList;
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

    public PageUtils searchOrderByPage(Map param);

    public HashMap searchOrderContent(long orderId);

    //返回热点图
    public ArrayList<HashMap> searchOrderStartLocationIn30Days();

    public boolean validDriverOwnOrder(Map param);

    public HashMap searchSettlementNeedData(long orderId);

    public HashMap searchOrderById(Map param);

    public HashMap validCanPayOrder(Map param);

    public int updateOrderPrepayId(Map param);

    public void handlePayment(String uuid, String payId, String driverOpenId, String payTime);

    public String updateOrderAboutPayment(Map param);


}