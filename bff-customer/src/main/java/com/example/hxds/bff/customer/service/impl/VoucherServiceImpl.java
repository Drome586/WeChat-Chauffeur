package com.example.hxds.bff.customer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.example.hxds.bff.customer.controller.form.*;
import com.example.hxds.bff.customer.feign.VhrServiceApi;
import com.example.hxds.bff.customer.service.VoucherService;
import com.example.hxds.common.util.PageUtils;
import com.example.hxds.common.util.R;
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
    public PageUtils searchUnTakeVoucherByPage(SearchUnTakeVoucherByPageForm form) {
        R r = vhrServiceApi.searchUnTakeVoucherByPage(form);
        HashMap map = (HashMap) r.get("result");
        PageUtils pageUtils = BeanUtil.toBean(map, PageUtils.class);
        return pageUtils;
    }

    @Override
    public PageUtils searchUnUseVoucherByPage(SearchUnUseVoucherByPageForm form) {
        R r = vhrServiceApi.searchUnUseVoucherByPage(form);
        HashMap map = (HashMap) r.get("result");
        PageUtils pageUtils = BeanUtil.toBean(map, PageUtils.class);
        return pageUtils;
    }

    @Override
    public PageUtils searchUsedVoucherByPage(SearchUsedVoucherByPageForm form) {
        R r = vhrServiceApi.searchUsedVoucherByPage(form);
        HashMap map = (HashMap) r.get("result");
        PageUtils pageUtils = BeanUtil.toBean(map, PageUtils.class);
        return pageUtils;
    }

    @Override
    public long searchUnUseVoucherCount(SearchUnUseVoucherCountForm form) {
        R r = vhrServiceApi.searchUnUseVoucherCount(form);
        long result = MapUtil.getLong(r, "result");
        return result;
    }

    @Override
    @Transactional
    @LcnTransaction
    public boolean takeVoucher(TakeVoucherForm form) {
        R r = vhrServiceApi.takeVoucher(form);
        boolean result = MapUtil.getBool(r, "result");
        return result;
    }

}

