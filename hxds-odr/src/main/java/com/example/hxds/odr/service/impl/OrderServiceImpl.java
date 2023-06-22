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
            throw new HxdsException("没有查询到数据，请核对查询条件");
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
        redisTemplate.delete("order#"+orderId);
        int rows = orderDao.deleteUnAcceptOrder(param);
        if(rows != 1){
            return "订单取消失败";
        }
        return "订单取消成功";
    }

}
