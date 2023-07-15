package com.example.hxds.odr.service.impl;

import cn.hutool.core.map.MapUtil;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.example.hxds.common.exception.HxdsException;
import com.example.hxds.odr.db.dao.OrderBillDao;
import com.example.hxds.odr.db.dao.OrderDao;
import com.example.hxds.odr.db.dao.OrderProfitsharingDao;
import com.example.hxds.odr.db.pojo.OrderProfitsharingEntity;
import com.example.hxds.odr.service.OrderBillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@Service
public class OrderBillServiceImpl implements OrderBillService {

    @Resource
    private OrderBillDao orderBillDao;

    @Resource
    private OrderDao orderDao;

    @Resource
    private OrderProfitsharingDao orderProfitsharingDao;

    @Override
    @Transactional
    @LcnTransaction
    public int updateBillFee(Map param) {
        //更新账单数据
        int rows = orderBillDao.updateBillFee(param);
        if(rows != 1){
            throw new HxdsException("更新账单费用详情失败");
        }

        //更新订单数据
        rows = orderDao.updateOrderMileageAndFee(param);
        if(rows != 1){
            throw new HxdsException("更新订单费用详情失败");
        }
        //添加分账单数据
        OrderProfitsharingEntity entity = new OrderProfitsharingEntity();
        entity.setOrderId(MapUtil.getLong(param, "orderId"));
        entity.setRuleId(MapUtil.getLong(param, "ruleId"));
        entity.setAmountFee(new BigDecimal((String) param.get("total")));
        entity.setPaymentRate(new BigDecimal((String) param.get("paymentRate")));
        entity.setPaymentFee(new BigDecimal((String) param.get("paymentFee")));
        entity.setTaxRate(new BigDecimal((String) param.get("taxRate")));
        entity.setTaxFee(new BigDecimal((String) param.get("taxFee")));
        entity.setSystemIncome(new BigDecimal((String) param.get("systemIncome")));
        entity.setDriverIncome(new BigDecimal((String) param.get("driverIncome")));
        rows = orderProfitsharingDao.insert(entity);
        if (rows != 1) {
            throw new HxdsException("添加分账记录失败");
        }

        return 0;
    }

    @Override
    public HashMap searchReviewDriverOrderBill(Map param) {
        HashMap map = orderBillDao.searchReviewDriverOrderBill(param);
        return map;
    }
}
