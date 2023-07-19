package com.example.hxds.bff.driver.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.example.hxds.bff.driver.controller.form.*;
import com.example.hxds.bff.driver.service.OrderService;
import com.example.hxds.common.util.PageUtils;
import com.example.hxds.common.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
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


    @PostMapping("/searchDriverCurrentOrder")
    @SaCheckLogin
    @Operation(description = "查询司机当前订单信息，包含乘客和司机")
    public R searchDriverCurrentOrder(){
        long driverId = StpUtil.getLoginIdAsLong();
        SearchDriverCurrentOrderForm param = new SearchDriverCurrentOrderForm();
        param.setDriverId(driverId);
        HashMap result = orderService.searchDriverCurrentOrder(param);
        return R.ok().put("result",result);
    }

    @PostMapping("/searchOrderForMoveById")
    @SaCheckLogin
    @Operation(summary = "查询订单信息用于司乘同显功能")
    public R searchOrderForMoveById(@RequestBody @Valid SearchOrderForMoveByIdForm form){
        long driverId = StpUtil.getLoginIdAsLong();
        form.setDriverId(driverId);
        HashMap result = orderService.searchOrderForMoveById(form);
        return R.ok().put("result",result);
    }

    @PostMapping("/arriveStartPlace")
    @SaCheckLogin
    @Operation(summary = "司机到达上车点")
    public R arriveStartPlace(@RequestBody @Valid ArriveStartPlaceForm form){
        long driverId = StpUtil.getLoginIdAsLong();
        form.setDriverId(driverId);
        int rows = orderService.arriveStartPlace(form);
        return R.ok().put("rows",rows);
    }

    @PostMapping("/startDriving")
    @SaCheckLogin
    @Operation(summary = "开始代驾")
    public R startDriving(@RequestBody @Valid StartDrivingForm form){
        long driverId = StpUtil.getLoginIdAsLong();
        form.setDriverId(driverId);
        int rows = orderService.startDriving(form);
        return R.ok().put("rows",rows);
    }

    @PostMapping("/updateOrderStatus")
    @Operation(summary = "更新订单状态")
    @SaCheckLogin
    public R updateOrderStatus(@RequestBody @Valid UpdateOrderStatusForm form){
        int rows = orderService.updateOrderStatus(form);
        return R.ok().put("rows",rows);
    }

    @PostMapping("/updateBillFee")
    @SaCheckLogin
    @Operation(summary = "更新订单账单费用")
    public R updateBillFee(@RequestBody @Valid UpdateBillFeeForm form){
        long driverId = StpUtil.getLoginIdAsLong();
        form.setDriverId(driverId);
        int rows = orderService.updateOrderBill(form);
        return R.ok().put("rows",rows);
    }

    @PostMapping("/searchReviewDriverOrderBill")
    @SaCheckLogin
    @Operation(summary = "查询司机预览订单")
    public R searchReviewDriverOrderBill(@RequestBody @Valid SearchReviewDriverOrderBillForm form){
        long driverId = StpUtil.getLoginIdAsLong();
        form.setDriverId(driverId);
        HashMap map = orderService.searchReviewDriverOrderBill(form);
        return R.ok().put("result",map);
    }

    @PostMapping("/searchDriverOrderByPage")
    @SaCheckLogin
    @Operation(summary = "查询订单分页记录")
    public R searchDriverOrderByPage(@RequestBody @Valid SearchDriverOrderByPageForm form) {
        long driverId = StpUtil.getLoginIdAsLong();
        form.setDriverId(driverId);
        PageUtils pageUtils = orderService.searchDriverOrderByPage(form);
        return R.ok().put("result", pageUtils);
    }

    @PostMapping("/searchOrderById")
    @SaCheckLogin
    @Operation(summary = "根据订单ID查询订单记录")
    public R searchOrderById(@RequestBody @Valid SearchOrderByIdForm form) {
        long driverId = StpUtil.getLoginIdAsLong();
        form.setDriverId(driverId);
        HashMap map = orderService.searchOrderById(form);
        return R.ok().put("result", map);
    }

}
