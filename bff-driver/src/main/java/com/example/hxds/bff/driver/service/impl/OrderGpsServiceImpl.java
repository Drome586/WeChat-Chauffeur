package com.example.hxds.bff.driver.service.impl;

import cn.hutool.core.map.MapUtil;
import com.example.hxds.bff.driver.controller.form.InsertOrderGpsForm;
import com.example.hxds.bff.driver.feign.NebulaServiceApi;
import com.example.hxds.bff.driver.service.OrderGpsService;
import com.example.hxds.common.util.R;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class OrderGpsServiceImpl implements OrderGpsService {

    @Resource
    private NebulaServiceApi nebulaServiceApi;

    @Override
    public int insertOrderGps(InsertOrderGpsForm form) {
        R r = nebulaServiceApi.insertOrderGps(form);
        int rows = MapUtil.getInt(r, "rows");
        return rows;
    }
}
