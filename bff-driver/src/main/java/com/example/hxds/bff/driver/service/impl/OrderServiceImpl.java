package com.example.hxds.bff.driver.service.impl;

import cn.hutool.core.map.MapUtil;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.example.hxds.bff.driver.controller.form.*;
import com.example.hxds.bff.driver.feign.CstServiceApi;
import com.example.hxds.bff.driver.feign.NebulaServiceApi;
import com.example.hxds.bff.driver.feign.OdrServiceApi;
import com.example.hxds.bff.driver.service.OrderService;
import com.example.hxds.common.util.R;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OdrServiceApi odrServiceApi;

    @Resource
    private CstServiceApi cstServiceApi;

    @Resource
    private NebulaServiceApi nebulaServiceApi;

    @Override
    @Transactional
    @LcnTransaction
    public String acceptNewOrder(AcceptNewOrderForm form) {
        R r = odrServiceApi.acceptNewOrder(form);
        String result = MapUtil.getStr(r, "result");
        return result;
    }

    @Override
    public HashMap searchDriverExecuteOrder(SearchDriverExecuteOrderForm form) {
        //查询订单信息
        R r = odrServiceApi.searchDriverExecuteOrder(form);
        HashMap orderMap = (HashMap) r.get("result");

        //查询代驾客户信息
        Long customerId = MapUtil.getLong(orderMap, "customerId");
        SearchCustomerInfoInOrderForm infoInOrderForm = new SearchCustomerInfoInOrderForm();
        infoInOrderForm.setCustomerId(customerId);

        r = cstServiceApi.searchCustomerInfoInOrder(infoInOrderForm);
        HashMap cstMap = (HashMap) r.get("result");

        HashMap map = new HashMap();
        map.putAll(orderMap);
        map.putAll(cstMap);
        return map;
    }

    @Override
    public HashMap searchDriverCurrentOrder(SearchDriverCurrentOrderForm form) {
        R r = odrServiceApi.searchDriverCurrentOrder(form);
        HashMap orderMap = (HashMap) r.get("result");

        if(MapUtil.isNotEmpty(orderMap)){
            HashMap map = new HashMap();
            //查询代驾客户信息
            Long customerId = MapUtil.getLong(orderMap, "customerId");
            SearchCustomerInfoInOrderForm infoInOrderForm = new SearchCustomerInfoInOrderForm();
            infoInOrderForm.setCustomerId(customerId);

            //包括电话照片等。。。
            r = cstServiceApi.searchCustomerInfoInOrder(infoInOrderForm);
            HashMap cstMap = (HashMap) r.get("result");
            map.putAll(orderMap);
            map.putAll(cstMap);
            return map;
        }else{
            return null;
        }
    }

    @Override
    public HashMap searchOrderForMoveById(SearchOrderForMoveByIdForm form) {
        R r = odrServiceApi.searchOrderForMoveById(form);
        HashMap result = (HashMap) r.get("result");
        return result;
    }

    @Override
    public int arriveStartPlace(ArriveStartPlaceForm form) {
        R r = odrServiceApi.arriveStartPlace(form);
        int rows = MapUtil.getInt(r, "rows");
        if(rows == 1){
            //TODO 发送通知消息
        }
        return rows;
    }

    @Override
    @LcnTransaction
    @Transactional
    public int startDriving(StartDrivingForm form) {
        R r = odrServiceApi.startDriving(form);
        int rows = MapUtil.getInt(r, "rows");
        if(rows == 1){
            InsertOrderMonitoringForm form1 = new InsertOrderMonitoringForm();
            form1.setOrderId(form.getOrderId());
            nebulaServiceApi.insertOrderMonitoring(form1);
        }
        //TODO 发送消息
        return rows;
    }

    @Override
    @LcnTransaction
    @Transactional
    public int updateOrderStatus(UpdateOrderStatusForm form) {
        R r = odrServiceApi.updateOrderStatus(form);
        int rows = MapUtil.getInt(r, "rows");
        //TODO 判断订单状态，用于后续业务
        return rows;
    }


}
