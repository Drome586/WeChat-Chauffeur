package com.example.hxds.bff.driver.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "查询司机正在执行的订单")
public class SearchDriverExecuteOrderForm {
    @NotNull(message = "orderId不能为空")
    @Min(value = 1,message = "orderId不能小于1")
    @Schema(description = "orderId")
    private Long orderId;

    @Schema(description = "司机Id")
    private Long driverId;

}
