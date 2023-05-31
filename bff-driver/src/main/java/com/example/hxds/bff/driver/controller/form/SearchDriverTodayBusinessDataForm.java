package com.example.hxds.bff.driver.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查询司机当天营业数据的表单")
public class SearchDriverTodayBusinessDataForm {
    @Schema(description = "司机ID")
    private Long driverId;
}