package com.example.hxds.mis.api.service;

import com.example.hxds.common.util.PageUtils;
import com.example.hxds.mis.api.controller.form.InsertVoucherForm;
import com.example.hxds.mis.api.controller.form.SearchVoucherByPageForm;
import com.example.hxds.mis.api.controller.form.UpdateVoucherStatusForm;
import org.h2.command.dml.Update;

public interface VoucherService {
    public PageUtils searchVoucherByPage(SearchVoucherByPageForm form);

    public int insertVoucher(InsertVoucherForm form);

    public int updateVoucherStatus(UpdateVoucherStatusForm form);
}

