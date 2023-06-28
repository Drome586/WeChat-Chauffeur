package com.example.hxds.odr.service.impl;

import cn.hutool.core.map.MapUtil;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.example.hxds.common.exception.HxdsException;
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

}
