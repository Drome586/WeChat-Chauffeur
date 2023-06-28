package com.example.hxds.bff.customer.feign;

import com.example.hxds.bff.customer.controller.form.DeleteUnAcceptOrderForm;
import com.example.hxds.bff.customer.controller.form.HasCustomerCurrentOrderForm;
import com.example.hxds.bff.customer.controller.form.InsertOrderForm;
import com.example.hxds.bff.customer.controller.form.SearchOrderStatusForm;
import com.example.hxds.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "hxds-odr")
public interface OdrServiceApi {

    @PostMapping("/order/insertOrder")
    public R insertOrder(InsertOrderForm form);


    @PostMapping("/order/searchOrderStatus")
    public R searchOrderStatus(SearchOrderStatusForm form);

    @PostMapping("/order/deleteUnAcceptOrder")
    public R deleteUnAcceptOrder(DeleteUnAcceptOrderForm form);

    @PostMapping("/order/hasCustomerCurrentOrder")
    public R hasCustomerCurrentOrder(HasCustomerCurrentOrderForm form);

}

