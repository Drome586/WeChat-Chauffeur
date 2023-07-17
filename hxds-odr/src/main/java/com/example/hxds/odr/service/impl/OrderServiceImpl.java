package com.example.hxds.odr.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.example.hxds.common.exception.HxdsException;
import com.example.hxds.common.util.PageUtils;
import com.example.hxds.common.util.R;
import com.example.hxds.odr.db.dao.OrderBillDao;
import com.example.hxds.odr.db.dao.OrderDao;
import com.example.hxds.odr.db.pojo.OrderBillEntity;
import com.example.hxds.odr.db.pojo.OrderEntity;
import com.example.hxds.odr.service.OrderService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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

}
