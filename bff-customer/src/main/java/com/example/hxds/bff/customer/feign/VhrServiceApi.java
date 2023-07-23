package com.example.hxds.bff.customer.feign;

import com.example.hxds.bff.customer.controller.form.SearchUnTakeVoucherByPageForm;
import com.example.hxds.bff.customer.controller.form.SearchUnUseVoucherByPageForm;
import com.example.hxds.bff.customer.controller.form.SearchUsedVoucherByPageForm;
import com.example.hxds.bff.customer.controller.form.UseVoucherForm;
import com.example.hxds.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "hxds-vhr")
public interface VhrServiceApi {

    @PostMapping("/voucher/customer/useVoucher")
    public R useVoucher(UseVoucherForm form);

    @PostMapping("/voucher/searchUnTakeVoucherByPage")
    public R searchUnTakeVoucherByPage(SearchUnTakeVoucherByPageForm form);

    @PostMapping("/voucher/searchUnUseVoucherByPage")
    public R searchUnUseVoucherByPage(SearchUnUseVoucherByPageForm form);

    @PostMapping("/voucher/searchUsedVoucherByPage")
    public R searchUsedVoucherByPage(SearchUsedVoucherByPageForm form);
}

