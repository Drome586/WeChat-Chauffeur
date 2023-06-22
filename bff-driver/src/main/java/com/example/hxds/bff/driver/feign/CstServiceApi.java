package com.example.hxds.bff.driver.feign;

import com.example.hxds.bff.driver.controller.form.SearchCustomerInfoInOrderForm;
import com.example.hxds.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "hxds-cst")
public interface CstServiceApi {

    @PostMapping("/customer/searchCustomerInfoInOrder")
    public R searchCustomerInfoInOrder(SearchCustomerInfoInOrderForm form);

}
