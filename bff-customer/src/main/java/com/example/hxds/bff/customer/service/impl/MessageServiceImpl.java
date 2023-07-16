package com.example.hxds.bff.customer.service.impl;

import cn.hutool.core.map.MapUtil;
import com.example.hxds.bff.customer.controller.form.ReceiveBillMessageForm;
import com.example.hxds.bff.customer.feign.SnmServiceApi;
import com.example.hxds.bff.customer.service.MessageService;
import com.example.hxds.common.util.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private SnmServiceApi snmServiceApi;


    @Override
    public String receiveBillMessage(ReceiveBillMessageForm form) {
        R r = snmServiceApi.receiveBillMessage(form);
        String msg = MapUtil.getStr(r, "result");
        return msg;
    }
}
