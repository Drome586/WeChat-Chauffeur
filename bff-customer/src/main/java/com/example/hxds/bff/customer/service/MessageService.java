package com.example.hxds.bff.customer.service;

import com.example.hxds.bff.customer.controller.form.ReceiveBillMessageForm;

public interface MessageService {

    public String receiveBillMessage(ReceiveBillMessageForm form);
}
