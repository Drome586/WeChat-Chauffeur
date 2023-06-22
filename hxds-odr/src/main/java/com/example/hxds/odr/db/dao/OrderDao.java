package com.example.hxds.odr.db.dao;


import com.example.hxds.odr.db.pojo.OrderEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface OrderDao {

    public HashMap searchDriverTodayBusinessData(long driverId);

    public int insert(OrderEntity entity);

    public String searchOrderIdByUUID(String uuid);

    public int acceptNewOrder(Map param);

    public HashMap searchDriverExecuteOrder(Map param);

    //查询订单号等返回数字类型的值时要注意返回值时Integer，，因为可能时null，如果时int的话是会报错的。
    public Integer searchOrderStatus(Map param);

    public int deleteUnAcceptOrder(Map param);
}




