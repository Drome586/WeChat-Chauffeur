package com.example.hxds.bff.customer.feign;

import com.example.hxds.bff.customer.controller.form.ReceiveBillMessageForm;
import com.example.hxds.bff.customer.controller.form.SendNewOrderMessageForm;
import com.example.hxds.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "hxds-snm")
public interface SnmServiceApi {

    @PostMapping("/message/order/new/sendNewOrderMessageAsync")
    public R sendNewOrderMessageAsync(SendNewOrderMessageForm form);

    @PostMapping("/message/receiveBillMessage")
    public R receiveBillMessage(ReceiveBillMessageForm form);
}
