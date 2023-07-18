package com.example.hxds.bff.customer.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.example.hxds.bff.customer.controller.form.InsertCommentForm;
import com.example.hxds.bff.customer.service.OrderCommentService;
import com.example.hxds.common.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/comment")
@Tag(name = "OrderCommentController", description = "订单评价模块Web接口")
public class OrderCommentController {

    @Resource
    private OrderCommentService orderCommentService;

    @PostMapping("/insertComment")
    @Operation(summary = "保存订单评价")
    @SaCheckLogin
    public R insertComment(@RequestBody @Valid InsertCommentForm form) {
        long customerId = StpUtil.getLoginIdAsLong();
        form.setCustomerId(customerId);
        int rows = orderCommentService.insertComment(form);
        return R.ok().put("rows", rows);
    }
}

