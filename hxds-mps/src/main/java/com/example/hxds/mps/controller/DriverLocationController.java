package com.example.hxds.mps.controller;

import cn.hutool.core.bean.BeanUtil;
import com.example.hxds.common.util.R;
import com.example.hxds.mps.controller.form.RemoveLocationCacheForm;
import com.example.hxds.mps.controller.form.UpdateLocationCacheForm;
import com.example.hxds.mps.service.DriverLocationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/driver/location")
public class DriverLocationController {
    @Resource
    private DriverLocationService driverLocationService;

    @PostMapping("/updateLocationCache")
    @Operation(summary = "更新司机GPS缓存定位")
    public R updateLocationCache(@RequestBody @Valid UpdateLocationCacheForm form){
        Map param = BeanUtil.beanToMap(form);
        driverLocationService.updateLocationCache(param);
        return R.ok();
    }

    @PostMapping("/removeLocationCache")
    @Operation(summary = "删除司机定位缓存")
    public R removeLocationCache(@RequestBody @Valid RemoveLocationCacheForm form){
        Long driverId = form.getDriverId();
        driverLocationService.removeLocationCache(driverId);
        return R.ok();
    }

}
