package com.example.hxds.bff.customer.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.hxds.bff.customer.controller.form.DeleteCustomerCarByIdForm;
import com.example.hxds.bff.customer.controller.form.InsertCustomerCarForm;
import com.example.hxds.bff.customer.controller.form.SearchCustomerCarListForm;
import com.example.hxds.bff.customer.service.CustomerCarService;
import com.example.hxds.bff.customer.service.CustomerService;
import com.example.hxds.common.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/customer/car")
@Tag(name = "CustomerCarController",description = "客户车辆Web接口")
public class CustomerCarController {

    @Resource
    private CustomerCarService customerCarService;

    @PostMapping("/insertCustomerCar")
    @Operation(summary = "添加客户车辆")
    public R insertCustomerCar(@RequestBody @Valid InsertCustomerCarForm form){
        long customerId = StpUtil.getLoginIdAsLong();
        form.setCustomerId(customerId);
        customerCarService.insertCustomerCar(form);
        return R.ok();
    }

    @PostMapping("/searchCustomerCarList")
    @Operation(summary = "查询车辆信息")
    public R searchCustomerCarList(@RequestBody @Valid SearchCustomerCarListForm form){
        long customerId = StpUtil.getLoginIdAsLong();
        form.setCustomerId(customerId);
        ArrayList<HashMap> list = customerCarService.searchCustomerCarList(form);
        return R.ok().put("result",list);
    }

    @PostMapping("/deleteCustomerCarById")
    @Operation(summary = "删除客户车辆")
    public R deleteCustomerCarById(@RequestBody @Valid DeleteCustomerCarByIdForm form){
        int rows = customerCarService.deleteCustomerCarById(form);
        return R.ok().put("rows",rows);
    }
}
