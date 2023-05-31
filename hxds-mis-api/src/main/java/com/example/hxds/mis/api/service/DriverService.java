package com.example.hxds.mis.api.service;


import com.example.hxds.common.util.PageUtils;
import com.example.hxds.mis.api.controller.form.SearchDriverByPageForm;
import com.example.hxds.mis.api.controller.form.SearchDriverRealSummaryForm;
import com.example.hxds.mis.api.controller.form.UpdateDriverRealAuthForm;

import java.util.HashMap;

public interface DriverService {
    public PageUtils searchDriverByPage(SearchDriverByPageForm form);

    public HashMap searchDriverComprehensiveData(byte realAuth, Long driverId);

    public int updateDriverRealAuth(UpdateDriverRealAuthForm form);
}
