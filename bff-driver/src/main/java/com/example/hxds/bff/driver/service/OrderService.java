package com.example.hxds.bff.driver.service;

import com.example.hxds.bff.driver.controller.form.AcceptNewOrderForm;

public interface OrderService {
    public String acceptNewOrder(AcceptNewOrderForm form);
}
