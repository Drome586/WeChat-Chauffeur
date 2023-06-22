package com.example.hxds.bff.driver.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.example.hxds.bff.driver.controller.form.AcceptNewOrderForm;
import com.example.hxds.bff.driver.controller.form.SearchDriverExecuteOrderForm;
import com.example.hxds.bff.driver.service.OrderService;
import com.example.hxds.common.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping("/order")
@Tag(name = "OrderController",description = "订单模块Web接口")
public class OrderController {

    @Resource
    private OrderService orderService;


    @PostMapping("/acceptNewOrder")
    @Operation(summary = "司机抢单接单")
    @SaCheckLogin
    public R acceptNewOrder(@RequestBody @Valid AcceptNewOrderForm form){
        long driverId = StpUtil.getLoginIdAsLong();
        form.setDriverId(driverId);
        String result = orderService.acceptNewOrder(form);
        return R.ok().put("result",result);
    }


    @PostMapping("/searchDriverExecuteOrder")
    @Operation(summary = "查询司机正在执行的订单记录")
    @SaCheckLogin
    public R searchDriverExecuteOrder(@RequestBody @Valid SearchDriverExecuteOrderForm form){
        long driverId = StpUtil.getLoginIdAsLong();
        form.setDriverId(driverId);
        HashMap map = orderService.searchDriverExecuteOrder(form);
        return R.ok().put("result",map);
    }
}
