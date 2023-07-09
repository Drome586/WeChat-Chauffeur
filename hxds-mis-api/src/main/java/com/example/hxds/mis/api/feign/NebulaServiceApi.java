package com.example.hxds.mis.api.feign;

import com.example.hxds.common.util.R;
import com.example.hxds.mis.api.controller.form.SearchOrderGpsForm;
import com.example.hxds.mis.api.controller.form.SearchOrderLastGpsForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "hxds-nebula")
public interface NebulaServiceApi {
    @PostMapping("/order/gps/searchOrderGps")
    public R searchOrderGps(SearchOrderGpsForm form);

    @PostMapping("/order/gps/searchOrderLastGps")
    public R searchOrderLastGps(SearchOrderLastGpsForm form);
}

