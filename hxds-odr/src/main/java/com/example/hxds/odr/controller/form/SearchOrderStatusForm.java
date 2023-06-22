package com.example.hxds.odr.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "查询订单状态表单")
public class SearchOrderStatusForm {

    @NotNull(message = "orderId不能为空")
    @Schema(description = "订单Id")
    private Long orderId;

    @Min(value = 1,message = "driverId不能小于1")
    @Schema(description = "司机Id")
    private Long driverId;

    @Min(value = 1,message = "customerId不能小于1")
    @Schema(description = "顾客Id")
    private Long customerId;
}
