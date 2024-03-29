package com.example.hxds.cst.db.dao;


import com.example.hxds.cst.db.pojo.CustomerCarEntity;

import java.util.ArrayList;
import java.util.HashMap;

public interface CustomerCarDao {
    //添加车辆信息
    public int insert(CustomerCarEntity entity);

    //查询车辆信息
    public ArrayList<HashMap> searchCustomerCarList(long customerId);

    //删除车辆信息
    public int deleteCustomerCarById(long id);

}




