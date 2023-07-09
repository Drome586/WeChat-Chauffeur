package com.example.hxds.mis.api.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.example.hxds.common.util.PageUtils;
import com.example.hxds.common.util.R;
import com.example.hxds.mis.api.controller.form.SearchOrderByPageForm;
import com.example.hxds.mis.api.controller.form.SearchOrderComprehensiveInfoForm;
import com.example.hxds.mis.api.service.OrderService;
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
@Tag(name = "OrderController",description = "Mis中的订单管理Web接口")
public class OrderController {

    @Resource
    private OrderService orderService;


    @PostMapping("/searchOrderByPage")
    @Operation(summary = "查询订单分页记录")
    @SaCheckPermission(value = {"ROOT","ORDER:SELECT"},mode = SaMode.OR)
    public R searchOrderByPage(@RequestBody @Valid SearchOrderByPageForm form){
        PageUtils pageUtils = orderService.searchOrderByPage(form);
        return R.ok().put("result",pageUtils);
    }

    @PostMapping("/searchOrderComprehensiveInfo")
    @Operation(summary = "查询汇总的订单")
    @SaCheckPermission(value = {"ROOT","ORDER:SELECT"},mode = SaMode.OR)
    public R searchOrderComprehensiveInfo(@RequestBody @Valid SearchOrderComprehensiveInfoForm form){
        HashMap result = orderService.searchOrderComprehensiveInfo(form.getOrderId());
        return R.ok().put("result",result);
    }

}
