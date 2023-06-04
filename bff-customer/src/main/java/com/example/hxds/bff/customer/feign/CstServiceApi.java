package com.example.hxds.bff.customer.feign;

import com.example.hxds.bff.customer.controller.form.LoginForm;
import com.example.hxds.bff.customer.controller.form.RegisterNewCustomerForm;
import com.example.hxds.common.util.R;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "hxds-cst")
public interface CstServiceApi {
    @PostMapping("/customer/registerNewCustomer")
    public R registerNewCustomer(RegisterNewCustomerForm form);

    @PostMapping("/customer/login")
    public R login(LoginForm form);
}
