package com.example.hxds.mis.api.feign;

import com.example.hxds.common.util.R;
import com.example.hxds.mis.api.controller.form.SearchCustomerBriefInfoForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "hxds-cst")
public interface CstServiceApi {
    @PostMapping("/customer/searchCustomerBriefInfo")
    public R searchCustomerBriefInfo(SearchCustomerBriefInfoForm form);
}

