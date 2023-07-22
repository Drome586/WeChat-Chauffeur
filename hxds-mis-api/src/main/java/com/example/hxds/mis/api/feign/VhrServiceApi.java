package com.example.hxds.mis.api.feign;

import com.example.hxds.common.util.R;
import com.example.hxds.mis.api.controller.form.InsertVoucherForm;
import com.example.hxds.mis.api.controller.form.SearchVoucherByPageForm;
import com.example.hxds.mis.api.controller.form.UpdateVoucherStatusForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "hxds-vhr")
public interface VhrServiceApi {
    @PostMapping("/voucher/searchVoucherByPage")
    public R searchVoucherByPage(SearchVoucherByPageForm form);

    @PostMapping("/voucher/insertVoucher")
    public R insertVoucher(InsertVoucherForm form);

    @PostMapping("/voucher/updateVoucherStatus")
    public R updateVoucherStatus(UpdateVoucherStatusForm form);
}

