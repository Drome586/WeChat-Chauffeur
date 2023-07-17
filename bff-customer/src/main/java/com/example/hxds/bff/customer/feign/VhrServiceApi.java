package com.example.hxds.bff.customer.feign;

import com.example.hxds.bff.customer.controller.form.UseVoucherForm;
import com.example.hxds.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "hxds-vhr")
public interface VhrServiceApi {

    @PostMapping("/voucher/customer/useVoucher")
    public R useVoucher(UseVoucherForm form);
}

