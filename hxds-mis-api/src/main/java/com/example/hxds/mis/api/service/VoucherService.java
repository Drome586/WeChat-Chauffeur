package com.example.hxds.mis.api.service;

import com.example.hxds.common.util.PageUtils;
import com.example.hxds.mis.api.controller.form.SearchVoucherByPageForm;

public interface VoucherService {
    public PageUtils searchVoucherByPage(SearchVoucherByPageForm form);
}

