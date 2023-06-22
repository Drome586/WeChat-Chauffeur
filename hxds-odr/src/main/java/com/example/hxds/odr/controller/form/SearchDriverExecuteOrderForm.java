package com.example.hxds.odr.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "查询司机正在执行的订单")
public class SearchDriverExecuteOrderForm {

    @NotNull(message = "订单Id不能为空")
    @Min(value = 1,message = "订单id不能小于1")
    @Schema(description = "订单ID")
    private Long orderId;

    @NotNull(message = "司机Id不能为空")
    @Min(value = 1,message = "司机Id最小为1")
    @Schema(description = "司机Id")
    private Long driverId;
}
