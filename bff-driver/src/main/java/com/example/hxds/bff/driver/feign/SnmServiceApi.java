package com.example.hxds.bff.driver.feign;

import com.example.hxds.bff.driver.controller.form.ClearNewOrderQueueForm;
import com.example.hxds.bff.driver.controller.form.ReceiveNewOrderMessageForm;
import com.example.hxds.bff.driver.controller.form.SendPrivateMessageForm;
import com.example.hxds.common.util.R;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "hxds-snm")
public interface SnmServiceApi {

    @PostMapping("/message/order/new/clearNewOrderQueue")
    public R clearNewOrderQueue(ClearNewOrderQueueForm form);

    @PostMapping("/message/order/new/receiveNewOrderMessage")
    public R receiveNewOrderMessage(ReceiveNewOrderMessageForm form);

    @PostMapping("/message/sendPrivateMessage")
    @Operation(summary = "同步发送私有消息")
    public R sendPrivateMessage(SendPrivateMessageForm form);

    @PostMapping("/message/sendPrivateMessageSync")
    @Operation(summary = "异步发送私有消息")
    public R sendPrivateMessageSync(SendPrivateMessageForm form);

}
