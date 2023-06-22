package com.example.hxds.bff.driver.service;

import com.example.hxds.bff.driver.controller.form.AcceptNewOrderForm;
import com.example.hxds.bff.driver.controller.form.SearchDriverExecuteOrderForm;

import java.util.HashMap;

public interface OrderService {
    public String acceptNewOrder(AcceptNewOrderForm form);

    public HashMap searchDriverExecuteOrder(SearchDriverExecuteOrderForm form);
}
