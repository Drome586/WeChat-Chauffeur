package com.example.hxds.nebula.db.dao;

import com.example.hxds.nebula.db.pojo.OrderGpsEntity;

import java.util.ArrayList;
import java.util.HashMap;

public interface OrderGpsDao {

    public int insert(OrderGpsEntity entity);

    //将gps定位点信息查找出来，连接成线
    public ArrayList<HashMap> searchOrderGpd(long orderId);

    public HashMap searchOrderLastGps(long orderId);
}
