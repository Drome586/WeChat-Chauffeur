package com.example.hxds.bff.customer.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.example.hxds.bff.customer.controller.form.*;
import com.example.hxds.bff.customer.feign.*;
import com.example.hxds.bff.customer.service.OrderService;
import com.example.hxds.common.exception.HxdsException;
import com.example.hxds.common.util.R;
import com.example.hxds.common.wxpay.MyWXPayConfig;
import com.example.hxds.common.wxpay.WXPay;
import com.example.hxds.common.wxpay.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private MpsServiceApi mpsServiceApi;

    @Resource
    private RuleServiceApi ruleServiceApi;

    @Resource
    private OdrServiceApi odrServiceApi;

    @Resource
    private SnmServiceApi snmServiceApi;

    @Resource
    private DrServiceApi drServiceApi;

    @Resource
    private VhrServiceApi vhrServiceApi;

    @Resource
    private MyWXPayConfig myWXPayConfig;

    @Resource
    private CstServiceApi cstServiceApi;

    @Override
    @LcnTransaction
    @Transactional
    public HashMap createNewOrder(CreateNewOrderForm form) {
        Long customerId = form.getCustomerId();
        String startPlace = form.getStartPlace();
        String startPlaceLatitude = form.getStartPlaceLatitude();
        String startPlaceLongitude = form.getStartPlaceLongitude();
        String endPlace = form.getEndPlace();
        String endPlaceLatitude = form.getEndPlaceLatitude();
        String endPlaceLongitude = form.getEndPlaceLongitude();
        //好处费
        String favourFee = form.getFavourFee();
        //重新计算里程和时间，虽然下单前，系统会预估里程和时间，但是有可能顾客在下单页面停留太久，然后在点击下单键，
        // 这时候路线和时长都可能会发生变化，所以需要重新预估里程和时间
        EstimateOrderMileageAndMinuteForm form_1 = new EstimateOrderMileageAndMinuteForm();
        form_1.setMode("driving");
        form_1.setStartPlaceLatitude(startPlaceLatitude);
        form_1.setStartPlaceLongitude(startPlaceLongitude);
        form_1.setEndPlaceLatitude(endPlaceLatitude);
        form_1.setEndPlaceLongitude(endPlaceLongitude);
        R r = mpsServiceApi.estimateOrderMileageAndMinute(form_1);

        HashMap map = (HashMap) r.get("result");
        String mileage = MapUtil.getStr(map, "mileage");
        int minute = MapUtil.getInt(map, "minute");

        /*
        重新估算订单金额
         */
        EstimateOrderChargeForm form_2 = new EstimateOrderChargeForm();
        form_2.setMileage(mileage);
        form_2.setTime(new DateTime().toTimeStr());
        //这样是对 r 做一个重新的赋值
        r = ruleServiceApi.estimateOrderCharge(form_2);
        map = (HashMap)r.get("result");
        String expectsFee = MapUtil.getStr(map, "amount");
        String chargeRuleId = MapUtil.getStr(map, "chargeRuleId");
        short baseMileage = MapUtil.getShort(map, "baseMileage");
        String baseMileagePrice = MapUtil.getStr(map, "baseMileagePrice");
        String exceedMileagePrice = MapUtil.getStr(map, "exceedMileagePrice");
        short baseMinute = MapUtil.getShort(map, "baseMinute");
        String exceedMinutePrice = MapUtil.getStr(map, "exceedMinutePrice");
        short baseReturnMileage = MapUtil.getShort(map, "baseReturnMileage");
        String exceedReturnPrice = MapUtil.getStr(map, "exceedReturnPrice");

        // 搜索适合接单的司机，有的话就创建订单，否则就不创建订单

        SearchBefittingDriverAboutOrderForm form_3 = new SearchBefittingDriverAboutOrderForm();
        form_3.setStartPlaceLatitude(startPlaceLatitude);
        form_3.setStartPlaceLongitude(startPlaceLongitude);
        form_3.setEndPlaceLatitude(endPlaceLatitude);
        form_3.setEndPlaceLongitude(endPlaceLongitude);
        form_3.setMileage(mileage);

        r = mpsServiceApi.searchBefittingDriverAboutOrder(form_3);
        ArrayList<HashMap> list = (ArrayList<HashMap>) r.get("result");
        //用来记录多少个符合的司机用来左返回
        HashMap result = new HashMap() {{
            put("count", 0);
        }};

        if(list.size() > 0) {
        /*
        生成订单记录
         */
            InsertOrderForm form_4 = new InsertOrderForm();
            //uuid微信支付时充当订单号
            form_4.setUuid(IdUtil.simpleUUID());
            form_4.setCustomerId(customerId);
            form_4.setStartPlace(startPlace);
            form_4.setStartPlaceLatitude(startPlaceLatitude);
            form_4.setStartPlaceLongitude(startPlaceLongitude);
            form_4.setEndPlace(endPlace);
            form_4.setEndPlaceLatitude(endPlaceLatitude);
            form_4.setEndPlaceLongitude(endPlaceLongitude);
            form_4.setExpectsMileage(mileage);
            form_4.setExpectsFee(expectsFee);
            form_4.setFavourFee(favourFee);
            form_4.setDate(new DateTime().toDateStr());
            form_4.setChargeRuleId(Long.parseLong(chargeRuleId));
            form_4.setCarPlate(form.getCarPlate());
            form_4.setCarType(form.getCarType());
            form_4.setBaseMileage(baseMileage);
            form_4.setBaseMileagePrice(baseMileagePrice);
            form_4.setExceedMileagePrice(exceedMileagePrice);
            form_4.setBaseMinute(baseMinute);
            form_4.setExceedMinutePrice(exceedMinutePrice);
            form_4.setBaseReturnMileage(baseReturnMileage);
            form_4.setExceedReturnPrice(exceedReturnPrice);

            r = odrServiceApi.insertOrder(form_4);
            String orderId = MapUtil.getStr(r, "result");

            //TODO 发送通知符合条件的司机抢单

            SendNewOrderMessageForm form_5 = new SendNewOrderMessageForm();
            String[] driverContent = new String[list.size()];
            for(int i = 0;i < list.size();i++){
                HashMap one = list.get(i);
                String driverId = MapUtil.getStr(one, "driverId");
                String distance = MapUtil.getStr(one, "distance");

                //将距离精确到小数点后一位，为什么要返回距离信息呢？因为后面会用到语音播报，例如
                //”距离您2.3公里处有一个订单“
                distance = new BigDecimal(distance).setScale(1, RoundingMode.CEILING).toString();
                driverContent[i] = driverId + "#" + distance;
            }

            form_5.setDriversContent(driverContent);
            form_5.setOrderId(Long.parseLong(orderId));
            form_5.setFrom(startPlace);
            form_5.setTo(endPlace);
            form_5.setExpectsFee(expectsFee);
            //里程转化为小数保留最后一位
            mileage = new BigDecimal(mileage).setScale(1, RoundingMode.CEILING).toString();
            form_5.setMileage(mileage);
            form_5.setMinute(minute);
            form_5.setFavourFee(favourFee);
            //异步发送消息
            snmServiceApi.sendNewOrderMessageAsync(form_5);

            //放入订单号即uuid
            result.put("orderId",orderId);
            result.put("count",list.size());
        }
        return result;
    }

    @Override
    public Integer searchOrderStatus(SearchOrderStatusForm form) {
        R r = odrServiceApi.searchOrderStatus(form);
        Integer status = MapUtil.getInt(r, "result");
        return status;
    }

    @Override
    @Transactional
    @LcnTransaction
    public String deleteUnAcceptOrder(DeleteUnAcceptOrderForm form) {
        R r = odrServiceApi.deleteUnAcceptOrder(form);
        String result = MapUtil.getStr(r, "result");
        return result;
    }

    @Override
    public HashMap hasCustomerCurrentOrder(HasCustomerCurrentOrderForm form) {
        R r = odrServiceApi.hasCustomerCurrentOrder(form);
        HashMap map = (HashMap) r.get("result");
        return map;
    }

    @Override
    public HashMap searchOrderForMoveById(SearchOrderForMoveByIdForm form) {
        R r = odrServiceApi.searchOrderForMoveById(form);
        HashMap result = (HashMap) r.get("result");
        return result;
    }

    @Override
    public boolean confirmArriveStartPlace(ConfirmArriveStartPlaceForm form) {
        R r = odrServiceApi.confirmArriveStartPlace(form);
        Boolean result = MapUtil.getBool(r, "result");
        return result;
    }

    @Override
    public HashMap searchOrderById(SearchOrderByIdForm form) {
        R r = odrServiceApi.searchOrderById(form);
        HashMap map = (HashMap) r.get("result");
        Long driverId = MapUtil.getLong(map, "driverId");
        if(driverId != null){
            SearchDriverBriefInfoForm form_1 = new SearchDriverBriefInfoForm();
            form_1.setDriverId(driverId);
            r = drServiceApi.searchDriverBriefInfo(form_1);
            HashMap temp = (HashMap) r.get("result");
            map.putAll(temp);
            return map;
        }
        return null;
    }

    @Override
    @Transactional
    @LcnTransaction
    public HashMap createWxPayment(long orderId, long customerId, Long voucherId) {
        /*
        先查询订单是否为6状态，其他状态都不可以生成订单
         */
        ValidCanPayOrderForm form_1 = new ValidCanPayOrderForm();
        form_1.setOrderId(orderId);
        form_1.setCustomerId(customerId);
        R r = odrServiceApi.validCanPayOrder(form_1);
        HashMap map = (HashMap) r.get("result");
        //总金额
        String amount = MapUtil.getStr(map,"realFee");
        //uuid是支付订单编号，并不想用雪花算法生成的id，因为雪花算法生成的id是有顺序性的，有据可循的不安全
        String uuid = MapUtil.getStr(map,"uuid");
        long driverId = MapUtil.getLong(map,"driverId");
        //代金券的面额大小
        String discount = "0.00";

        if(voucherId != null){
            /*
            查询代金券是否可以使用
             */
            UseVoucherForm form_2 = new UseVoucherForm();
            form_2.setCustomerId(customerId);
            form_2.setOrderId(orderId);
            form_2.setVoucherId(voucherId);
            form_2.setAmount(amount);
            r = vhrServiceApi.useVoucher(form_2);
            discount = MapUtil.getStr(r,"result");
        }

        if (new BigDecimal(amount).compareTo(new BigDecimal(discount)) == -1) {
            throw new HxdsException("总金额不能小于优惠劵面额");
        }
        /*
         * 3.修改实付金额
         */
        amount = NumberUtil.sub(amount, discount).toString();
        UpdateBillPaymentForm form_3 = new UpdateBillPaymentForm();
        form_3.setOrderId(orderId);
        form_3.setRealPay(amount);
        form_3.setVoucherFee(discount);
        odrServiceApi.updateBillPayment(form_3);

        /*
         * 4.查询用户的OpenID字符串
         */
        SearchCustomerOpenIdForm form_4 = new SearchCustomerOpenIdForm();
        form_4.setCustomerId(customerId);
        r = cstServiceApi.searchCustomerOpenId(form_4);
        String customerOpenId = MapUtil.getStr(r, "result");

        /*
         * 5.查询司机的OpenId字符串
         */
        SearchDriverOpenIdForm form_5 = new SearchDriverOpenIdForm();
        form_5.setDriverId(driverId);
        r = drServiceApi.searchDriverOpenId(form_5);
        String driverOpenId = MapUtil.getStr(r, "result");

        /*
         * 6.TODO 创建支付订单,appid 啊还是商户号等都放在 myWXPayConfig 里面了
         */

        try{
            WXPay wxPay = new WXPay(myWXPayConfig);
            //提交支付账单的时候需要提交各种参数

            HashMap param = new HashMap();
            param.put("nonce_str", WXPayUtil.generateNonceStr());   //随机字符串
            param.put("body","代驾费");
            //代价订单uuid
            param.put("out_trade_no",uuid);
            //充值金额转换成分为单位，并且让BigDecimal取整数；
            //amount == 1.00;
            param.put("total_fee", NumberUtil.mul(amount, "100").setScale(0, RoundingMode.FLOOR).toString());
            param.put("spbill_create_ip", "127.0.0.1");
            //TODO 这里要修改成内网穿透的公网URL
            param.put("notify_url", "http://demo.com");
            param.put("trade_type", "JSAPI");
            param.put("openid", customerOpenId);
            param.put("attach", driverOpenId);
            param.put("profit_sharing", "Y"); //支付需要分账

            //创建支付订单
            Map<String, String> result = wxPay.unifiedOrder(param);

            //预支付交易会话标识ID
            String prepayId = result.get("prepay_id");
            if (prepayId != null) {
                /*
                 * 7.更新订单记录中的prepay_id字段值
                 */
                UpdateOrderPrepayIdForm form_6 = new UpdateOrderPrepayIdForm();
                form_6.setOrderId(orderId);
                form_6.setPrepayId(prepayId);
                odrServiceApi.updateOrderPrepayId(form_6);

                //准备生成数字签名用的数据
                map.clear();
                map.put("appId", myWXPayConfig.getAppID());
                String timeStamp = new Date().getTime() + "";
                map.put("timeStamp", timeStamp);
                String nonceStr = WXPayUtil.generateNonceStr();
                map.put("nonceStr", nonceStr);
                map.put("package", "prepay_id=" + prepayId);
                map.put("signType", "MD5");

                //生成数据签名
                String paySign = WXPayUtil.generateSignature(map, myWXPayConfig.getKey()); //生成数字签名

                map.clear(); //清理HashMap，放入结果
                map.put("package", "prepay_id=" + prepayId);
                map.put("timeStamp", timeStamp);
                map.put("nonceStr", nonceStr);
                map.put("paySign", paySign);
                //uuid用于付款成功后，移动端主动请求更新充值状态
                map.put("uuid", uuid);
                return map;
            } else {
                log.error("创建支付订单失败");
                throw new HxdsException("创建支付订单失败");
            }
        } catch (Exception e) {
            log.error("创建支付订单失败", e);
            throw new HxdsException("创建支付订单失败");
        }

    }
}
