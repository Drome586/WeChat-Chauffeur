package com.example.hxds.bff.customer.service;

import com.example.hxds.bff.customer.controller.form.SearchUnTakeVoucherByPageForm;
import com.example.hxds.bff.customer.controller.form.SearchUnUseVoucherByPageForm;
import com.example.hxds.bff.customer.controller.form.SearchUsedVoucherByPageForm;
import com.example.hxds.common.util.PageUtils;

public interface VoucherService {

    public PageUtils searchUnTakeVoucherByPage(SearchUnTakeVoucherByPageForm form);

    public PageUtils searchUnUseVoucherByPage(SearchUnUseVoucherByPageForm form);

    public PageUtils searchUsedVoucherByPage(SearchUsedVoucherByPageForm form);

}

