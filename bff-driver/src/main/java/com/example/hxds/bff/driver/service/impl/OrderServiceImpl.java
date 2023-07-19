package com.example.hxds.bff.driver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.example.hxds.bff.driver.controller.form.*;
import com.example.hxds.bff.driver.feign.*;
import com.example.hxds.bff.driver.service.OrderService;
import com.example.hxds.common.exception.HxdsException;
import com.example.hxds.common.util.PageUtils;
import com.example.hxds.common.util.R;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.util.HashMap;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OdrServiceApi odrServiceApi;

    @Resource
    private CstServiceApi cstServiceApi;

    @Resource
    private NebulaServiceApi nebulaServiceApi;

    @Resource
    private RuleServiceApi ruleServiceApi;

    @Resource
    private SnmServiceApi snmServiceApi;

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
        if(rows != 1){
            throw new HxdsException("订单状态修改失败");
        }
        if(form.getStatus() == 6){
            SendPrivateMessageForm messageForm = new SendPrivateMessageForm();
            messageForm.setReceiverIdentity("customer_bill");
            messageForm.setReceiverId(form.getCustomerId());
            messageForm.setTtl(3 * 24 * 3600 * 1000);
            messageForm.setSenderId(0L);
            messageForm.setSenderIdentity("system");
            messageForm.setSenderName("华夏代驾");
            messageForm.setMsg("您有代驾订单待支付");
            snmServiceApi.sendPrivateMessageSync(messageForm);
        }
        return rows;
    }

    @Override
    @Transactional
    @LcnTransaction
    public int updateOrderBill(UpdateBillFeeForm form) {
        /*
        判断司机是否关联该订单
         */
        ValidDriverOwnOrderForm form_1 = new ValidDriverOwnOrderForm();
        form_1.setOrderId(form.getOrderId());
        form_1.setDriverId(form.getDriverId());

        R r = odrServiceApi.validDriverOwnOrder(form_1);
        Boolean bool = MapUtil.getBool(r, "result");
        if(!bool){
            throw new HxdsException("司机未关联该订单");
        }

        /*
        计算订单里程数据
         */

        CalculateOrderMileageForm form_2 = new CalculateOrderMileageForm();
        form_2.setOrderId(form.getOrderId());
        r = nebulaServiceApi.calculateOrderMileage(form_2);
        String mileage = (String) r.get("result");
        mileage= NumberUtil.div(mileage,"1000",1, RoundingMode.CEILING).toString();

        /*
         * 3.查询订单消息
         */
        SearchSettlementNeedDataForm form_3 = new SearchSettlementNeedDataForm();
        form_3.setOrderId(form.getOrderId());
        r = odrServiceApi.searchSettlementNeedData(form_3);
        HashMap map = (HashMap) r.get("result");
        String acceptTime = MapUtil.getStr(map, "acceptTime");
        String startTime = MapUtil.getStr(map, "startTime");
        int waitingMinute = MapUtil.getInt(map, "waitingMinute");
        String favourFee = MapUtil.getStr(map, "favourFee");

        /*
         * 4.计算代驾费
         */
        CalculateOrderChargeForm form_4 = new CalculateOrderChargeForm();
        form_4.setMileage(mileage);
        form_4.setTime(startTime.split(" ")[1]);
        form_4.setMinute(waitingMinute);
        r = ruleServiceApi.calculateOrderCharge(form_4);
        map = (HashMap) r.get("result");
        String mileageFee = MapUtil.getStr(map, "mileageFee");
        String returnFee = MapUtil.getStr(map, "returnFee");
        String waitingFee = MapUtil.getStr(map, "waitingFee");
        String amount = MapUtil.getStr(map, "amount");
        String returnMileage = MapUtil.getStr(map, "returnMileage");

        /*
         * 5.计算系统奖励费用
         */
        CalculateIncentiveFeeForm form_5 = new CalculateIncentiveFeeForm();
        form_5.setDriverId(form.getDriverId());
        form_5.setAcceptTime(acceptTime);
        r = ruleServiceApi.calculateIncentiveFee(form_5);
        String incentiveFee = (String) r.get("result");


        form.setMileageFee(mileageFee);
        form.setReturnFee(returnFee);
        form.setWaitingFee(waitingFee);
        form.setIncentiveFee(incentiveFee);
        form.setRealMileage(mileage);
        form.setReturnMileage(returnMileage);
        //计算总费用
        String total = NumberUtil.add(amount, form.getTollFee(), form.getParkingFee(), form.getOtherFee(), favourFee).toString();
        form.setTotal(total);

        /*
         * 6.计算分账数据
         */
        CalculateProfitsharingForm form_6 = new CalculateProfitsharingForm();
        form_6.setOrderId(form.getOrderId());
        form_6.setAmount(total);
        r = ruleServiceApi.calculateProfitsharing(form_6);
        map = (HashMap) r.get("result");
        long ruleId = MapUtil.getLong(map, "ruleId");
        String systemIncome = MapUtil.getStr(map, "systemIncome");
        String driverIncome = MapUtil.getStr(map, "driverIncome");
        String paymentRate = MapUtil.getStr(map, "paymentRate");
        String paymentFee = MapUtil.getStr(map, "paymentFee");
        String taxRate = MapUtil.getStr(map, "taxRate");
        String taxFee = MapUtil.getStr(map, "taxFee");
        form.setRuleId(ruleId);
        form.setPaymentRate(paymentRate);
        form.setPaymentFee(paymentFee);
        form.setTaxRate(taxRate);
        form.setTaxFee(taxFee);
        form.setSystemIncome(systemIncome);
        form.setDriverIncome(driverIncome);

        /*
         * 7.更新代驾费账单数据
         */
        r = odrServiceApi.updateBillFee(form);
        int rows = MapUtil.getInt(r, "rows");
        return rows;
    }

    @Override
    public HashMap searchReviewDriverOrderBill(SearchReviewDriverOrderBillForm form) {
        R r = odrServiceApi.searchReviewDriverOrderBill(form);
        HashMap map = (HashMap) r.get("result");
        return map;
    }

    @Override
    public PageUtils searchDriverOrderByPage(SearchDriverOrderByPageForm form) {
        R r = odrServiceApi.searchDriverOrderByPage(form);
        HashMap map = (HashMap) r.get("result");
        PageUtils pageUtils = BeanUtil.toBean(map, PageUtils.class);
        return pageUtils;

    }

    @Override
    public HashMap searchOrderById(SearchOrderByIdForm form) {
        //查询订单信息

        R r = odrServiceApi.searchOrderById(form);
        HashMap orderMap = (HashMap) r.get("result");

        //查询代驾客户信息
        long customerId = MapUtil.getLong(orderMap, "customerId");
        SearchCustomerInfoInOrderForm form_1 = new SearchCustomerInfoInOrderForm();
        form_1.setCustomerId(customerId);
        r = cstServiceApi.searchCustomerInfoInOrder(form_1);
        HashMap cstMap = (HashMap) r.get("result");

        //查询评价信息
        int status = MapUtil.getInt(orderMap, "status");
        HashMap cmtMap = new HashMap();
        if (status >= 7) {
            SearchCommentByOrderIdForm form_2 = new SearchCommentByOrderIdForm();
            form_2.setOrderId(form.getOrderId());
            form_2.setDriverId(form.getDriverId());
            r = odrServiceApi.searchCommentByOrderId(form_2);

            if (r.containsKey("result")) {
                cmtMap = (HashMap) r.get("result");
            } else {
                cmtMap.put("rate", 5);
            }
        }

        HashMap map = new HashMap();
        map.putAll(orderMap);
        map.putAll(cstMap);
        map.put("comment",cmtMap);
        return map;
    }


}
