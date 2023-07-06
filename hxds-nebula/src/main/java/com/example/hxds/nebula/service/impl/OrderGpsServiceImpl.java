package com.example.hxds.nebula.service.impl;

import com.example.hxds.nebula.controller.vo.InsertOrderGpsVo;
import com.example.hxds.nebula.db.dao.OrderGpsDao;
import com.example.hxds.nebula.db.pojo.OrderGpsEntity;
import com.example.hxds.nebula.service.OrderGpsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;

@Service
public class OrderGpsServiceImpl implements OrderGpsService {

    @Resource
    private OrderGpsDao orderGpsDao;

    @Override
    @Transactional
    public int insertOrderGps(ArrayList<InsertOrderGpsVo> list) {
        int rows = 0;

        for(OrderGpsEntity entity:list){
            rows += orderGpsDao.insert(entity);
        }
        return rows;
    }
}
