package com.example.hxds.nebula.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "添加监控记录摘要的表单")
public class InsertOrderMonitoringForm {

    @NotNull(message = "OrderId不能为空")
    @Min(value = 1,message = "orderID不能小于1")
    @Schema(description = "订单ID")
    private Long orderId;

}
