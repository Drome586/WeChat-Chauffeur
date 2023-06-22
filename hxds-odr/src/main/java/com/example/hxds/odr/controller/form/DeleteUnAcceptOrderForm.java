package com.example.hxds.odr.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "要跟新订单的状态表")
public class DeleteUnAcceptOrderForm {
    @NotNull(message = "orderId不能为空")
    @Schema(description = "订单ID")
    private Long orderId;

    @Min(value = 0, message = "driverId不能小于0")
    @Schema(description = "司机ID")
    private Long driverId;

    @Min(value = 0, message = "customerId不能小于0")
    @Schema(description = "乘客ID")
    private Long customerId;
}
