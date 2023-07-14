package com.example.hxds.bff.driver.feign;

import com.example.hxds.bff.driver.config.MultipartSupportConfig;
import com.example.hxds.bff.driver.controller.form.CalculateOrderMileageForm;
import com.example.hxds.bff.driver.controller.form.InsertOrderGpsForm;
import com.example.hxds.bff.driver.controller.form.InsertOrderMonitoringForm;
import com.example.hxds.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "hxds-nebula", configuration = MultipartSupportConfig.class)
public interface NebulaServiceApi {

    //consumes  规定还可以传输普通的数据

    @PostMapping(value = "/monitoring/uploadRecordFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R uploadRecordFile(@RequestPart(value = "file") MultipartFile file,
                              @RequestPart("name") String name,
                              @RequestPart(value = "text", required = false) String text);

    @PostMapping(value = "/monitoring/insertOrderMonitoring")
    public R insertOrderMonitoring(InsertOrderMonitoringForm form);

    @PostMapping("/order/gps/insertOrderGps")
    public R insertOrderGps(InsertOrderGpsForm form);

    @PostMapping("/order/gps/calculateOrderMileage")
    public R calculateOrderMileage(CalculateOrderMileageForm form);
}

