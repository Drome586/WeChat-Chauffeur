package com.example.hxds.mis.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.nacos.common.utils.MapUtils;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.example.hxds.common.util.PageUtils;
import com.example.hxds.common.util.R;
import com.example.hxds.mis.api.controller.form.InsertVoucherForm;
import com.example.hxds.mis.api.controller.form.SearchVoucherByPageForm;
import com.example.hxds.mis.api.controller.form.UpdateVoucherStatusForm;
import com.example.hxds.mis.api.feign.VhrServiceApi;
import com.example.hxds.mis.api.service.VoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
@Slf4j
public class VoucherServiceImpl implements VoucherService {

    @Resource
    private VhrServiceApi vhrServiceApi;

    @Override
    public PageUtils searchVoucherByPage(SearchVoucherByPageForm form) {
        R r = vhrServiceApi.searchVoucherByPage(form);
        HashMap result = (HashMap) r.get("result");
        PageUtils pageUtils = BeanUtil.toBean(result, PageUtils.class);
        return pageUtils;
    }

    @Override
    @LcnTransaction
    @Transactional
    public int insertVoucher(InsertVoucherForm form) {
        R r = vhrServiceApi.insertVoucher(form);
        Integer rows = MapUtil.getInt(r, "rows");
        return rows;
    }

    @Override
    @LcnTransaction
    @Transactional
    public int updateVoucherStatus(UpdateVoucherStatusForm form) {
        R r = vhrServiceApi.updateVoucherStatus(form);
        int rows = MapUtil.getInt(r, "rows");
        return rows;
    }
}
