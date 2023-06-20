package com.example.hxds.bff.customer.feign;

import com.example.hxds.bff.customer.controller.form.InsertOrderForm;
import com.example.hxds.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "hxds-odr")
public interface OdrServiceApi {

    @PostMapping("/order/insertOrder")
    public R insertOrder(InsertOrderForm form);


}

