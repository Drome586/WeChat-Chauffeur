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

    //查询工作台页面正在执行的订单，这个时司机接单之后跳转过来的界面，并不司乘同显页面(司机端）
    public HashMap searchDriverCurrentOrder(long driverId);

    //查询没有司机接单的订单（乘客端）
    public HashMap hasCustomerUnAcceptOrder(long customerId);

    //查询还没有完成的订单号，（乘客端）
    public Long hasCustomerUnFinishedOrder(long customerId);

    //查询司乘同显页面，传过去的路径携带的是orderId
    public HashMap searchOrderForMoveById(Map param);

    //司机确认到达上车点
    public int updateOrderStatus(Map param);

    //查询总数用于MIS管理
    public long searchOrderCount(Map param);
    //MIS
    public ArrayList<HashMap> searchOrderByPage(Map param);

    //MIS下拉列表查询
    public HashMap searchOrderContent(long orderId);

    public ArrayList<String> searchOrderStartLocationIn30Days();

    public int updateOrderMileageAndFee(Map param);

    public long validDriverOwnOrder(Map param);

    public HashMap searchSettlementNeedData(long driverId);


}




