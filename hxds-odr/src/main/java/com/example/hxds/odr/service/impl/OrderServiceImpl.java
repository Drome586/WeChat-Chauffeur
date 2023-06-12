package com.example.hxds.odr.service.impl;

import cn.hutool.core.map.MapUtil;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.example.hxds.common.exception.HxdsException;
import com.example.hxds.odr.db.dao.OrderBillDao;
import com.example.hxds.odr.db.dao.OrderDao;
import com.example.hxds.odr.db.pojo.OrderBillEntity;
import com.example.hxds.odr.db.pojo.OrderEntity;
import com.example.hxds.odr.service.OrderService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
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
                redisTemplate.expire("order#"+id,15, TimeUnit.MINUTES);
                return id;
            }else{
                throw new HxdsException("保存新订单失败");
            }
        }else{
            throw new HxdsException("保存新订单失败");
        }

    }
}
