package com.example.hxds.odr.db.dao;


import com.example.hxds.odr.db.pojo.OrderCommentEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface OrderCommentDao {

    public int insert(OrderCommentEntity entity);

    public HashMap searchCommentByOrderId(Map param);

    public ArrayList<HashMap> searchCommentByPage(Map param);

    public long searchCommentCount(Map param);
}




