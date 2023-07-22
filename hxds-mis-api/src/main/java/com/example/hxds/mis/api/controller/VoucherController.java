package com.example.hxds.mis.api.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.example.hxds.common.util.PageUtils;
import com.example.hxds.common.util.R;
import com.example.hxds.mis.api.controller.form.DeleteVoucherByIdsForm;
import com.example.hxds.mis.api.controller.form.InsertVoucherForm;
import com.example.hxds.mis.api.controller.form.SearchVoucherByPageForm;
import com.example.hxds.mis.api.controller.form.UpdateVoucherStatusForm;
import com.example.hxds.mis.api.service.VoucherService;
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

    @PostMapping("/searchVoucherByPage")
    @SaCheckPermission(value = {"ROOT", "VOUCHER:SELECT"}, mode = SaMode.OR)
    @Operation(summary = "查询代金券分页记录")
    public R searchVoucherByPage(@RequestBody @Valid SearchVoucherByPageForm form) {
        PageUtils pageUtils = voucherService.searchVoucherByPage(form);
        return R.ok().put("result", pageUtils);
    }

    @PostMapping("/insertVoucher")
    @SaCheckPermission(value = {"ROOT", "VOUCHER:INSERT"}, mode = SaMode.OR)
    @Operation(summary = "添加代金券")
    public R insertVoucher(@RequestBody @Valid InsertVoucherForm form) {
        form.setStatus((byte) 3);
        int rows = voucherService.insertVoucher(form);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/updateVoucherStatus")
    @SaCheckPermission(value = {"ROOT", "VOUCHER:UPDATE"}, mode = SaMode.OR)
    @Operation(summary = "更新代金券状态")
    public R updateVoucherStatus(@RequestBody @Valid UpdateVoucherStatusForm form) {
        int rows = voucherService.updateVoucherStatus(form);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/deleteVoucherByIds")
    @SaCheckPermission(value = {"ROOT", "VOUCHER:DELETE"}, mode = SaMode.OR)
    @Operation(summary = "删除代金券")
    public R deleteVoucherByIds(@RequestBody @Valid DeleteVoucherByIdsForm form) {
        int rows = voucherService.deleteVoucherByIds(form);
        return R.ok().put("rows", rows);
    }

}

