package com.example.hxds.bff.customer.service.impl;

import cn.hutool.core.map.MapUtil;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.example.hxds.bff.customer.controller.form.InsertCommentForm;
import com.example.hxds.bff.customer.feign.OdrServiceApi;
import com.example.hxds.bff.customer.service.OrderCommentService;
import com.example.hxds.common.exception.HxdsException;
import com.example.hxds.common.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Slf4j
public class OrderCommentServiceImpl implements OrderCommentService {

    @Resource
    private OdrServiceApi odrServiceApi;

    @Override
    @Transactional
    @LcnTransaction
    public int insertComment(InsertCommentForm form) {
        R r = odrServiceApi.insertComment(form);
        int rows = MapUtil.getInt(r, "rows");
        if(rows != 1){
            throw new HxdsException("保存订单评价失败");
        }
        return rows;
    }
}
