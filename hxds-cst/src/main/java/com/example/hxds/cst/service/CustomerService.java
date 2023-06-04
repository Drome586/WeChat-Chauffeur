package com.example.hxds.cst.service;

import java.util.Map;

public interface CustomerService {
    public String registerNewCustomer(Map param);

    public String login(String openId);
}
