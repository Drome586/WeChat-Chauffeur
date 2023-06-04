package com.example.hxds.bff.customer.service;

import com.example.hxds.bff.customer.controller.form.LoginForm;
import com.example.hxds.bff.customer.controller.form.RegisterNewCustomerForm;

public interface CustomerService {
    public long registerNewCustomer(RegisterNewCustomerForm form);

    public Long login(LoginForm form);
}
