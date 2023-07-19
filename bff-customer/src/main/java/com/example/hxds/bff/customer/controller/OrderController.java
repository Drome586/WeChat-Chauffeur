package com.example.hxds.bff.customer.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.example.hxds.bff.customer.controller.form.*;
import com.example.hxds.bff.customer.service.OrderService;
import com.example.hxds.common.util.PageUtils;
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

    @PostMapping("/createNewOrder")
    @Operation(summary = "创建新订单")
    @SaCheckLogin
    public R createNewOrder(@RequestBody @Valid CreateNewOrderForm form){
        long customerId = StpUtil.getLoginIdAsLong();
        form.setCustomerId(customerId);
        HashMap result = orderService.createNewOrder(form);
        return R.ok().put("result",result);
    }

    @PostMapping("/searchOrderStatus")
    @Operation(summary = "查询订单状态")
    @SaCheckLogin
    public R searchOrderStatus(@RequestBody @Valid SearchOrderStatusForm form) {
        long customerId = StpUtil.getLoginIdAsLong();
        form.setCustomerId(customerId);
        Integer status = orderService.searchOrderStatus(form);
        return R.ok().put("result", status);
    }

    @PostMapping("/deleteUnAcceptOrder")
    @Operation(summary = "关闭没有司机接单的订单")
    @SaCheckLogin
    public R deleteUnAcceptOrder(@RequestBody @Valid DeleteUnAcceptOrderForm form) {
        long customerId = StpUtil.getLoginIdAsLong();
        form.setCustomerId(customerId);

        String result = orderService.deleteUnAcceptOrder(form);
        return R.ok().put("result", result);
    }

    @PostMapping("/hasCustomerCurrentOrder")
    @SaCheckLogin
    @Operation(summary = "查询乘客是否存在当前的订单")
    public R hasCustomerCurrentOrder(){
        long customerId = StpUtil.getLoginIdAsLong();
        HasCustomerCurrentOrderForm form = new HasCustomerCurrentOrderForm();
        form.setCustomerId(customerId);
        HashMap result = orderService.hasCustomerCurrentOrder(form);
        return R.ok().put("result",result);
    }

    @PostMapping("/searchOrderForMoveById")
    @SaCheckLogin
    @Operation(summary = "查询订单信息用于司乘同显功能")
    public R searchOrderForMoveById(@RequestBody @Valid SearchOrderForMoveByIdForm form) {
        long customerId = StpUtil.getLoginIdAsLong();
        form.setCustomerId(customerId);
        HashMap map = orderService.searchOrderForMoveById(form);
        return R.ok().put("result", map);
    }

    @PostMapping("/confirmArriveStartPlace")
    @Operation(summary = "确认司机已经到达")
    public R confirmArriveStartPlace(@RequestBody @Valid ConfirmArriveStartPlaceForm form){
        boolean result = orderService.confirmArriveStartPlace(form);
        return R.ok().put("result",result);
    }

    @PostMapping("/searchOrderById")
    @Operation(summary = "根据Id查询订单信息")
    @SaCheckLogin
    public R searchOrderById(@RequestBody @Valid SearchOrderByIdForm form){
        long customerId = StpUtil.getLoginIdAsLong();
        form.setCustomerId(customerId);
        HashMap map = orderService.searchOrderById(form);
        return R.ok().put("result",map);
    }

    @PostMapping("/createWxPayment")
    @Operation(summary = "创建支付订单")
    @SaCheckLogin
    public R createWxPayment(@RequestBody @Valid CreateWxPaymentForm form) {
        Long customerId = StpUtil.getLoginIdAsLong();
        form.setCustomerId(customerId);
        HashMap map = orderService.createWxPayment(form.getOrderId(), form.getCustomerId(), form.getVoucherId());
        return R.ok().put("result", map);
    }

    @PostMapping("/updateOrderAboutPayment")
    @Operation(summary = "查询司机是否关联某订单")
    @SaCheckLogin
    public R updateOrderAboutPayment(@RequestBody @Valid UpdateOrderAboutPaymentForm form) {
        String result = orderService.updateOrderAboutPayment(form);
        return R.ok().put("result", result);
    }

    @PostMapping("/searchCustomerOrderByPage")
    @SaCheckLogin
    @Operation(summary = "查询订单分页记录")
    public R searchCustomerOrderByPage(@RequestBody @Valid SearchCustomerOrderByPageForm form){
        long customerId = StpUtil.getLoginIdAsLong();
        form.setCustomerId(customerId);
        PageUtils pageUtils = orderService.searchCustomerOrderByPage(form);
        return R.ok().put("result", pageUtils);
    }
}
