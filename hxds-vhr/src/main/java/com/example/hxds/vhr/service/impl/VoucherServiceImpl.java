package com.example.hxds.vhr.service.impl;

import cn.hutool.core.map.MapUtil;
import com.example.hxds.common.util.PageUtils;
import com.example.hxds.vhr.db.dao.VoucherCustomerDao;
import com.example.hxds.vhr.db.dao.VoucherDao;
import com.example.hxds.vhr.service.VoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class VoucherServiceImpl implements VoucherService {

    @Resource
    private VoucherDao voucherDao;

    @Resource
    private VoucherCustomerDao voucherCustomerDao;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public PageUtils searchVoucherByPage(Map param) {
        ArrayList<HashMap> list = null;
        long count = voucherDao.searchVoucherCount(param);
        if(count > 0){
            list = voucherDao.searchVoucherByPage(param);
        }else{
            list = new ArrayList();
        }
        int start = MapUtil.getInt(param,"start");
        int length = MapUtil.getInt(param,"length");

        PageUtils pageUtils = new PageUtils(list, count, start, length);
        return pageUtils;
    }
}
