package com.example.hxds.cst.service;

import com.example.hxds.cst.db.pojo.CustomerCarEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface CustomerService {
    public String registerNewCustomer(Map param);

    public String login(String openId);

    public HashMap searchCustomerInfoInOrder(long customerId);

    public HashMap searchCustomerBriefInfo(long customerId);

    public String searchCustomerOpenId(long customerId);
}
