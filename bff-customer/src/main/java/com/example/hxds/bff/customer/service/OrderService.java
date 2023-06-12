package com.example.hxds.bff.customer.service;

import com.example.hxds.bff.customer.controller.form.CreateNewOrderForm;

public interface OrderService {
    public int createNewOrder(CreateNewOrderForm form);
}
