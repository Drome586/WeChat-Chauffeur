package com.example.hxds.mis.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.example.hxds.common.util.PageUtils;
import com.example.hxds.common.util.R;
import com.example.hxds.mis.api.controller.form.SearchVoucherByPageForm;
import com.example.hxds.mis.api.feign.VhrServiceApi;
import com.example.hxds.mis.api.service.VoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
