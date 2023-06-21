package com.example.hxds.bff.driver.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "司机抢单表单")
public class AcceptNewOrderForm {


    @Schema(description = "driverId")
    private Long driverId;

    @NotNull(message = "orderId")
    @Min(value = 1,message = "orderId不能为空")
    @Schema(description = "orderId")
    private Long orderId;
}
