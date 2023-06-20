package com.example.hxds.bff.driver.service.impl;

import com.example.hxds.bff.driver.controller.form.ClearNewOrderQueueForm;
import com.example.hxds.bff.driver.controller.form.RemoveLocationCacheForm;
import com.example.hxds.bff.driver.controller.form.UpdateLocationCacheForm;
import com.example.hxds.bff.driver.feign.MpsServiceApi;
import com.example.hxds.bff.driver.feign.SnmServiceApi;
import com.example.hxds.bff.driver.service.DriverLocationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class DriverLocationServiceImpl implements DriverLocationService {
    @Resource
    private MpsServiceApi mpsServiceApi;

    @Resource
    private SnmServiceApi snmServiceApi;

    @Override
    public void updateLocationCache(UpdateLocationCacheForm form) {
        mpsServiceApi.updateLocationCache(form);
    }

    @Override
    public void removeLocationCache(RemoveLocationCacheForm form) {
        mpsServiceApi.removeLocationCache(form);
    }

}
