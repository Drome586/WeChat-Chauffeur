package com.example.hxds.odr.db.dao;

import com.example.hxds.odr.db.pojo.OrderBillEntity;

import java.util.HashMap;
import java.util.Map;

public interface OrderBillDao {

    public int insert(OrderBillEntity entity);

    public int deleteUnAcceptOrderBill(long orderId);

    public int updateBillFee(Map param);

    //查询汇总出来的金额，显示到小程序
    public HashMap searchReviewDriverOrderBill(Map param);

    //支付时查看是否使用过代金券等
    public int updateOrderBillPayment(Map param);

}




