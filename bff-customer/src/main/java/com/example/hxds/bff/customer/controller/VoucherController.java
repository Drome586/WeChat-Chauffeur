package com.example.hxds.bff.customer.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.example.hxds.bff.customer.controller.form.*;
import com.example.hxds.bff.customer.service.VoucherService;
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

@RestController
@RequestMapping("/voucher")
@Tag(name = "VoucherController", description = "代金券Web接口")
public class VoucherController {

    @Resource
    private VoucherService voucherService;

    @PostMapping("/searchUnTakeVoucherByPage")
    @SaCheckLogin
    @Operation(summary = "查询未领取代金券的分页记录")
    public R searchUnTakeVoucherByPage(@RequestBody @Valid SearchUnTakeVoucherByPageForm form) {
        long customerId = StpUtil.getLoginIdAsLong();
        form.setCustomerId(customerId);
        PageUtils pageUtils = voucherService.searchUnTakeVoucherByPage(form);
        return R.ok().put("result", pageUtils);
    }

    @PostMapping("/searchUnUseVoucherByPage")
    @SaCheckLogin
    @Operation(summary = "查询未使用代金券的分页记录")
    public R searchUnUseVoucherByPage(@RequestBody @Valid SearchUnUseVoucherByPageForm form) {
        long customerId = StpUtil.getLoginIdAsLong();
        form.setCustomerId(customerId);
        PageUtils pageUtils = voucherService.searchUnUseVoucherByPage(form);
        return R.ok().put("result", pageUtils);
    }

    @PostMapping("/searchUsedVoucherByPage")
    @SaCheckLogin
    @Operation(summary = "查询已使用代金券的分页记录")
    public R searchUsedVoucherByPage(@RequestBody @Valid SearchUsedVoucherByPageForm form) {
        long customerId = StpUtil.getLoginIdAsLong();
        form.setCustomerId(customerId);
        PageUtils pageUtils = voucherService.searchUsedVoucherByPage(form);
        return R.ok().put("result", pageUtils);
    }

    @PostMapping("/searchUnUseVoucherCount")
    @SaCheckLogin
    @Operation(summary = "查询未使用代金券数量")
    public R searchUnUseVoucherCount(@RequestBody @Valid SearchUnUseVoucherCountForm form) {
        long customerId = StpUtil.getLoginIdAsLong();
        form.setCustomerId(customerId);
        long result = voucherService.searchUnUseVoucherCount(form);
        return R.ok().put("result", result);
    }

    @PostMapping("/takeVoucher")
    @SaCheckLogin
    @Operation(summary = "领取代金券")
    public R takeVoucher(@RequestBody @Valid TakeVoucherForm form) {
        long customerId = StpUtil.getLoginIdAsLong();
        form.setCustomerId(customerId);
        boolean result = voucherService.takeVoucher(form);
        return R.ok().put("result", result);
    }
}

