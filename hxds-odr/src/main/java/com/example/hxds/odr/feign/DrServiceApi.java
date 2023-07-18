package com.example.hxds.odr.feign;

import com.example.hxds.common.util.R;
import com.example.hxds.odr.controller.form.TransferForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "hxds-dr")
public interface DrServiceApi {
    @PostMapping("/wallet/income/transfer")
    public R transfer(TransferForm form);
}

