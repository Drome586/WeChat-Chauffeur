package com.example.hxds.odr.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.example.hxds.common.exception.HxdsException;
import com.example.hxds.common.util.PageUtils;
import com.example.hxds.common.util.R;
import com.example.hxds.common.wxpay.MyWXPayConfig;
import com.example.hxds.common.wxpay.WXPay;
import com.example.hxds.common.wxpay.WXPayUtil;
import com.example.hxds.odr.controller.form.TransferForm;
import com.example.hxds.odr.db.dao.OrderBillDao;
import com.example.hxds.odr.db.dao.OrderDao;
import com.example.hxds.odr.db.pojo.OrderBillEntity;
import com.example.hxds.odr.db.pojo.OrderEntity;
import com.example.hxds.odr.feign.DrServiceApi;
import com.example.hxds.odr.quartz.QuartzUtil;
import com.example.hxds.odr.quartz.job.HandleProfitsharingJob;
import com.example.hxds.odr.service.OrderService;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderDao orderDao;

    @Resource
    private OrderBillDao orderBillDao;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private DrServiceApi drServiceApi;

    @Resource
    private QuartzUtil quartzUtil;

    @Resource
    private MyWXPayConfig myWXPayConfig;

    @Override
    public HashMap searchDriverTodayBusinessData(long driverId) {
        HashMap result = orderDao.searchDriverTodayBusinessData(driverId);
        String duration = MapUtil.getStr(result, "duration");
        if (duration == null) {
            duration = "0";
        }
        result.replace("duration", duration);

        String income = MapUtil.getStr(result, "income");
        if (income == null) {
            income = "0.00";
        }
        result.replace("income", income);

        return result;
    }

    @Override
    @LcnTransaction
    @Transactional
    public String insertOrder(OrderEntity orderEntity, OrderBillEntity orderBillEntity) {
        //插入订单记录,insert语句的返回值是1
        int rows = orderDao.insert(orderEntity);
        if(rows == 1){
            String id = orderDao.searchOrderIdByUUID(orderEntity.getUuid());
            //将uuid保存到orderBill里面,用uuid当作订单金额表的订单号，而不是用主键值
            orderBillEntity.setOrderId(Long.parseLong(id));
            rows = orderBillDao.insert(orderBillEntity);
            if(rows == 1){

                //order 后面加 # 号代表新生成未被抢的订单，“none”将来存放司机的主键值，表示哪个司机抢了这个订单。
                redisTemplate.opsForValue().set("order#"+id,"none");
                redisTemplate.expire("order#"+id,16, TimeUnit.MINUTES);
                return id;
            }else{
                throw new HxdsException("保存新订单失败");
            }
        }else{
            throw new HxdsException("保存新订单失败");
        }

    }

    @Override
    @Transactional
    @LcnTransaction
    public String acceptNewOrder(long driverId, long orderId) {
        //Redis 不存在抢单的订单就代表抢单失败
        if(!redisTemplate.hasKey("order#" + orderId)){
            return "抢单失败";
        }
        //执行Redis事务
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //获取新订单记录的Version
                operations.watch("order#"+orderId);
                //本地缓存redis操作
                operations.multi();
                //把新订单缓存的Value设置成抢单司机的ID
                operations.opsForValue().set("order#"+orderId,driverId);
                //执行redis事务，如果事务提交失败会自动抛出异常
                return operations.exec();
            }
        });
        //抢单成功后，删除Redis中的新订单，避免让其他司机参与抢单
        redisTemplate.delete("order#"+orderId);
        //更新订单记录，添加上接单司机Id和接单时间
        HashMap map = new HashMap(){{
            put("driverId",driverId);
            put("orderId",orderId);
        }};
        int rows = orderDao.acceptNewOrder(map);
        if(rows != 1){
            throw new HxdsException("接单失败，无法更新订单记录");
        }
        return "接单成功";
    }

    @Override
    public HashMap searchDriverExecuteOrder(Map param) {
        HashMap result = orderDao.searchDriverExecuteOrder(param);
        return result;
    }

    @Override
    public Integer searchOrderStatus(Map param) {
        Integer status = orderDao.searchOrderStatus(param);
        if(status == null){
            //throw new HxdsException("没有查询到数据，请核对查询条件");
            /*
            后面的操作中把这个异常更改为status = 0；现在还有一种情况需要我们动脑子认真想想，比如说乘客下单成功之后，等待了5分钟，微信就闪退了。
            过了5分钟之后，他重新登录小程序。因为抢单缓存还没有被销毁，而且订单和账单记录也都在，小程序跳转到create_order.vue页面，
            重新从15分钟开始倒计时，但是倒计时过程中，抢单缓存会超时被销毁，同时订单和账单记录也都删除了。
            这时候乘客端小程序发来轮询请求，业务层发现倒计时还没结束，但是抢单缓存就没有了，说明有司机抢单了，于是就跳转到司乘同显页面，这明显是不对的。
             */
            status = 0;
        }
        return status;
    }

    @Override
    @Transactional
    @LcnTransaction
    public String deleteUnAcceptOrder(Map param) {
        //先拿到orderId删除redis中的缓存信息
        long orderId = MapUtil.getLong(param, "orderId");
        if(!redisTemplate.hasKey("order#"+orderId)){
            return "订单取消失败";
        }
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch("order#"+orderId);
                operations.multi();
                operations.opsForValue().set("order#"+orderId,"none");
                return operations.exec();
            }
        });
        //删除订单记录
        redisTemplate.delete("order#"+orderId);
        int rows = orderDao.deleteUnAcceptOrder(param);
        if(rows != 1){
            return "订单取消失败";
        }
        //删除订单金额表的记录
        rows = orderBillDao.deleteUnAcceptOrderBill(orderId);
        if(rows != 1){
            return "订单取消失败";
        }

        return "订单取消成功";
    }

    @Override
    public HashMap searchDriverCurrentOrder(long driverId) {
        HashMap map = orderDao.searchDriverCurrentOrder(driverId);
        return map;
    }

    @Override
    public HashMap hasCustomerCurrentOrder(long customerId) {
        HashMap result = new HashMap();
        HashMap map = orderDao.hasCustomerUnAcceptOrder(customerId);
        result.put("hasCustomerUnAcceptOrder",map != null);
        result.put("unAcceptOrder",map);

        Long id = orderDao.hasCustomerUnFinishedOrder(customerId);
        result.put("hasCustomerUnFinishedOrder",id != null);
        result.put("unFinishedOrder",id);
        return result;
    }

    @Override
    public HashMap searchOrderForMoveById(Map param) {
        HashMap map = orderDao.searchOrderForMoveById(param);
        return map;
    }

    @Override
    @LcnTransaction
    @Transactional
    public int arriveStartPlace(Map param) {
        //添加到达上车点标志位
        Long orderId = MapUtil.getLong(param,"orderId");
        //"1"  当司机到达上车点后，乘客会点击“司机到达”按钮值变为“2”，当司机检查完车况后，点击开始代驾，后端会确认值是否为“2”是的话开始代驾。
        redisTemplate.opsForValue().set("order_driver_arrivied#" + orderId,"1");
        int rows = orderDao.updateOrderStatus(param);
        if(rows!= 1){
            throw new HxdsException("更新订单状态失败");
        }
        return rows;
    }

    @Override
    public boolean confirmArriveStartPlace(long orderId) {
        String key = "order_driver_arrivied#" + orderId;
        if(redisTemplate.hasKey(key) && redisTemplate.opsForValue().get(key).toString().equals("1")){
            redisTemplate.opsForValue().set(key,"2");
            return true;
        }
        return false;
    }

    @Override
    @LcnTransaction
    @Transactional
    public int startDriving(Map param) {
        long orderId = MapUtil.getLong(param, "orderId");
        String key = "order_driver_arrivied#" + orderId;

        if(redisTemplate.hasKey(key) && redisTemplate.opsForValue().get(key).toString().endsWith("2")){
            redisTemplate.delete(key);

            int rows = orderDao.updateOrderStatus(param);
            if(rows != 1){
                throw new HxdsException("更新订单状态失败");
            }
            return rows;
        }
        return 0;
    }

    @Override
    @LcnTransaction
    @Transactional
    public int updateOrderStatus(Map param) {
        int rows = orderDao.updateOrderStatus(param);
        if(rows != 1){
            throw new HxdsException("更新取消订单记录失败");
        }
        return rows;
    }

    @Override
    public PageUtils searchOrderByPage(Map param) {
        long count = orderDao.searchOrderCount(param);
        ArrayList<HashMap> list = null;
        if(count == 0){
            list = new ArrayList<>();
        }else{
            list = orderDao.searchOrderByPage(param);
        }
        int start = (Integer) param.get("start");
        int length = (Integer) param.get("length");

        PageUtils pageUtils = new PageUtils(list, count, start, length);
        return pageUtils;
    }

    @Override
    public HashMap searchOrderContent(long orderId) {
        HashMap map = orderDao.searchOrderContent(orderId);
        JSON startPlaceLocation = JSONUtil.parse(MapUtil.getStr(map, "startPlaceLocation"));
        JSON endPlaceLocation = JSONUtil.parse(MapUtil.getStr(map, "endPlaceLocation"));

        map.replace("startPlaceLocation",startPlaceLocation);
        map.replace("endPlaceLocation",endPlaceLocation);

        return map;
    }

    @Override
    public ArrayList<HashMap> searchOrderStartLocationIn30Days() {
        ArrayList<String> list = orderDao.searchOrderStartLocationIn30Days();
        ArrayList<HashMap> result = new ArrayList();
        list.forEach(location->{
            JSONObject json = JSONUtil.parseObj(location);
            String latitude = json.getStr("latitude");
            String longitude = json.getStr("longitude");
            latitude = latitude.substring(0, latitude.length() - 4);
            longitude = longitude.substring(0,longitude.length()-4);
            latitude += "0001";
            longitude +="0001";
            HashMap map = new HashMap();
            map.put("latitude",latitude);
            map.put("longitude",longitude);
            result.add(map);
        });
        return result;
    }

    @Override
    public boolean validDriverOwnOrder(Map param) {
        long count = orderDao.validDriverOwnOrder(param);
        return count == 1?true:false;
    }

    @Override
    public HashMap searchSettlementNeedData(long orderId) {
        HashMap map = orderDao.searchSettlementNeedData(orderId);
        return map;
    }

    @Override
    public HashMap searchOrderById(Map param) {
        HashMap map = orderDao.searchOrderById(param);
        String startPlaceLocation=MapUtil.getStr(map,"startPlaceLocation");
        String endPlaceLocation=MapUtil.getStr(map,"endPlaceLocation");
        map.replace("startPlaceLocation",JSONUtil.parseObj(startPlaceLocation));
        map.replace("endPlaceLocation",JSONUtil.parseObj(endPlaceLocation));
        return map;
    }

    @Override
    public HashMap validCanPayOrder(Map param) {
        HashMap map = orderDao.validCanPayOrder(param);
        if(map == null || map.size() == 0){
            throw new HxdsException("订单无法支付");
        }
        return map;
    }

    @Override
    @LcnTransaction
    @Transactional
    public int updateOrderPrepayId(Map param) {
        int rows = orderDao.updateOrderPrepayId(param);
        if(rows != 1){
            throw new HxdsException("更新预支付订单ID失败");
        }
        return rows;
    }

    @Override
    @LcnTransaction
    @Transactional
    public void handlePayment(String uuid, String payId, String driverOpenId, String payTime) {
        /*
         * 更新订单状态之前，先查询订单的状态。
         * 因为乘客端付款成功之后，会主动发起Ajax请求，要求更新订单状态。
         * 所以后端接收到付款通知消息之后，不要着急修改订单状态，先看一下订单是否已经是7状态
         */
        HashMap map = orderDao.searchOrderIdAndStatus(uuid);
        int status = MapUtil.getInt(map, "status");
        if (status == 7) {
            return;
        }

        HashMap param = new HashMap() {{
            put("uuid", uuid);
            put("payId", payId);
            put("payTime", payTime);
        }};
        //更新订单记录的PayId、状态和付款时间
        int rows = orderDao.updateOrderPayIdAndStatus(param);
        if (rows != 1) {
            throw new HxdsException("更新支付订单ID失败");
        }

        //查询系统奖励
        map = orderDao.searchDriverIdAndIncentiveFee(uuid);
        String incentiveFee = MapUtil.getStr(map, "incentiveFee");
        long driverId = MapUtil.getLong(map, "driverId");
        //判断系统奖励费是否大于0
        if (new BigDecimal(incentiveFee).compareTo(new BigDecimal("0.00")) == 1) {
            TransferForm form = new TransferForm();
            form.setUuid(IdUtil.simpleUUID());
            form.setAmount(incentiveFee);
            form.setDriverId(driverId);
            form.setType((byte) 2);
            form.setRemark("系统奖励费");
            //给司机钱包转账奖励费
            drServiceApi.transfer(form);
        }

        //TODO 执行分账
        //先判断是否有分账定时器
        if (quartzUtil.checkExists(uuid, "代驾单分账任务组") || quartzUtil.checkExists(uuid, "查询代驾单分账任务组")) {
            //存在分账定时器就不需要再执行分账
            return;
        }
        //执行分账
        JobDetail jobDetail = JobBuilder.newJob(HandleProfitsharingJob.class).build();
        Map dataMap = jobDetail.getJobDataMap();
        dataMap.put("uuid", uuid);
        dataMap.put("driverOpenId", driverOpenId);
        dataMap.put("payId", payId);

        //2分钟之后执行分账定时器
        Date executeDate = new DateTime().offset(DateField.MINUTE, 2);
        quartzUtil.addJob(jobDetail, uuid, "代驾单分账任务组", executeDate);

        //更新订单状态为已完成状态（8）
        rows = orderDao.finishOrder(uuid);
        if (rows != 1) {
            throw new HxdsException("更新订单结束状态失败");
        }
    }

    @Override
    @Transactional
    @LcnTransaction
    public String updateOrderAboutPayment(Map param) {
        long orderId = MapUtil.getLong(param, "orderId");

        /*
        查询订单状态，因为有可能Web方法先收到了付款结果的通知，把订单状态改成了7，8状态
        所以要先查询订单状态
         */
        HashMap map = orderDao.searchUuidAndStatus(orderId);
        String uuid = MapUtil.getStr(map, "uuid");
        int status = MapUtil.getInt(map, "status");
        //如果订单状态已经是已付款，就退出当前方法
        if (status == 7 || status == 8) {
            return "付款成功";
        }

        //查询支付结果的参数
        map.clear();
        map.put("appid", myWXPayConfig.getAppID());
        map.put("mch_id", myWXPayConfig.getMchID());
        map.put("out_trade_no", uuid);
        map.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            //生成数字签名
            String sign = WXPayUtil.generateSignature(map, myWXPayConfig.getKey());
            map.put("sign", sign);

            WXPay wxPay = new WXPay(myWXPayConfig);
            //查询支付结果
            Map<String, String> result = wxPay.orderQuery(map);

            String returnCode = result.get("return_code");
            String resultCode = result.get("result_code");
            if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
                String tradeState = result.get("trade_state");
                if ("SUCCESS".equals(tradeState)) {
                    String driverOpenId = result.get("attach");
                    String payId = result.get("transaction_id");
                    String payTime = new DateTime(result.get("time_end"), "yyyyMMddHHmmss").toString("yyyy-MM-dd HH:mm:ss");
                    //更新订单相关付款信息和状态
                    param.put("payId", payId);
                    param.put("payTime", payTime);

                    //把订单更新成7状态
                    int rows = orderDao.updateOrderAboutPayment(param);
                    if (rows != 1) {
                        throw new HxdsException("更新订单相关付款信息失败");
                    }

                    //查询系统奖励
                    map = orderDao.searchDriverIdAndIncentiveFee(uuid);
                    String incentiveFee = MapUtil.getStr(map, "incentiveFee");
                    long driverId = MapUtil.getLong(map, "driverId");
                    //判断系统奖励费是否大于0
                    if (new BigDecimal(incentiveFee).compareTo(new BigDecimal("0.00")) == 1) {
                        TransferForm form = new TransferForm();
                        form.setUuid(IdUtil.simpleUUID());
                        form.setAmount(incentiveFee);
                        form.setDriverId(driverId);
                        form.setType((byte) 2);
                        form.setRemark("系统奖励费");
                        //给司机钱包转账奖励费
                        drServiceApi.transfer(form);
                    }

                    //先判断是否有分账定时器
                    if (quartzUtil.checkExists(uuid, "代驾单分账任务组") || quartzUtil.checkExists(uuid, "查询代驾单分账任务组")) {
                        //存在分账定时器就不需要再执行分账
                        return "付款成功";
                    }
                    //执行分账
                    JobDetail jobDetail = JobBuilder.newJob(HandleProfitsharingJob.class).build();
                    Map dataMap = jobDetail.getJobDataMap();
                    dataMap.put("uuid", uuid);
                    dataMap.put("driverOpenId", driverOpenId);
                    dataMap.put("payId", payId);

                    Date executeDate = new DateTime().offset(DateField.MINUTE, 2);
                    quartzUtil.addJob(jobDetail, uuid, "代驾单分账任务组", executeDate);
                    rows = orderDao.finishOrder(uuid);
                    if(rows!=1){
                        throw new HxdsException("更新订单结束状态失败");
                    }
                    return "付款成功";
                } else {
                    return "付款异常";
                }
            } else {
                return "付款异常";
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new HxdsException("更新订单相关付款信息失败");
        }
    }

    @Override
    public PageUtils searchDriverOrderByPage(Map param) {
        long count = orderDao.searchDriverOrderCount(param);
        ArrayList<HashMap> list = null;

        if(count > 0){
            list = orderDao.searchDriverOrderByPage(param);
        }

        int start = (Integer)param.get("start");
        int length = (Integer)param.get("length");
        PageUtils pageUtils = new PageUtils(list, count, start, length);
        return pageUtils;
    }

    @Override
    public PageUtils searchCustomerOrderByPage(Map param) {
        long count = orderDao.searchCustomerOrderCount(param);
        ArrayList<HashMap> list = null;
        if(count > 0){
            list = orderDao.searchCustomerOrderByPage(param);
        }else{
            list = new ArrayList<>();
        }
        int start = MapUtil.getInt(param,"start");
        int length = MapUtil.getInt(param,"length");

        PageUtils pageUtils = new PageUtils(list, count, start, length);

        return pageUtils;
    }

}
