package com.example.hxds.cst.db.dao;

import java.util.HashMap;
import java.util.Map;

public interface CustomerDao {
    public int registerNewCustomer(Map param);

    public long hasCustomer(Map param);

    public String searchCustomerId(String openId);

    public String login(String code);

    public HashMap searchCustomerOrderInfoInOrder(long customerId);

    public HashMap searchCustomerBriefInfo(long customerId);

}
