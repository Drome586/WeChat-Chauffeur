package com.example.hxds.odr.service;

import com.example.hxds.common.util.PageUtils;
import com.example.hxds.odr.db.pojo.OrderCommentEntity;

import java.util.HashMap;
import java.util.Map;

public interface OrderCommentService {

    public int insert(OrderCommentEntity entity);

    public HashMap searchCommentByOrderId(Map param);

    public PageUtils searchCommentByPage(Map param);
}
