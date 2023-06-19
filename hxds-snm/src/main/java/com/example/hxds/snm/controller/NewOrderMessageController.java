package com.example.hxds.snm.controller;

import com.example.hxds.common.util.R;
import com.example.hxds.snm.controller.form.SendNewOrderMessageForm;
import com.example.hxds.snm.entity.NewOrderMessage;
import com.example.hxds.snm.task.NewOrderMessageTask;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;

@RestController
@RequestMapping("/message/order/new")
public class NewOrderMessageController {

    @Resource
    private NewOrderMessageTask task;


    @PostMapping("/sendNewOrderMessage")
    @Operation(summary = "向司机发送同步消息")
    public R sendNewOrderMessage(@RequestBody @Valid SendNewOrderMessageForm form){
        ArrayList<NewOrderMessage> list = new ArrayList<>();
        String[] driversContent = form.getDriversContent();
        for(String one:driversContent){
            String[] temp = one.split("#");
            String userId = temp[0];
            String distance = temp[1];

            NewOrderMessage message = new NewOrderMessage();
            message.setUserId(userId);
            message.setFrom(form.getFrom());
            message.setTo(form.getTo());
            message.setMileage(form.getMileage());
            message.setOrderId(form.getOrderId().toString());
            message.setMinute(form.getMinute().toString());
            message.setDistance(distance);
            message.setExpectsFee(form.getExpectsFee());
            message.setFavourFee(form.getFavourFee());
            list.add(message);
        }
        task.sendNewOrderMessage(list);
        return R.ok();
    }

    @PostMapping("/sendNewOrderMessageAsync")
    @Operation(summary = "向司机发送异步消息")
    public R sendNewOrderMessageAsync(@RequestBody @Valid SendNewOrderMessageForm form){
        ArrayList<NewOrderMessage> list = new ArrayList<>();
        String[] driversContent = form.getDriversContent();
        for(String one:driversContent){
            String[] temp = one.split("#");
            String userId = temp[0];
            String distance = temp[1];

            NewOrderMessage message = new NewOrderMessage();
            message.setUserId(userId);
            message.setFrom(form.getFrom());
            message.setTo(form.getTo());
            message.setMileage(form.getMileage());
            message.setOrderId(form.getOrderId().toString());
            message.setMinute(form.getMinute().toString());
            message.setDistance(distance);
            message.setExpectsFee(form.getExpectsFee());
            message.setFavourFee(form.getFavourFee());
            list.add(message);
        }
        task.sendNewOrderMessageAsync(list);
        return R.ok();
    }
}
