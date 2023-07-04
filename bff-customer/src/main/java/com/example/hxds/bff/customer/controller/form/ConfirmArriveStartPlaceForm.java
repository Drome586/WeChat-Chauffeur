package com.example.hxds.bff.customer.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "乘客确认司机到达表单")
public class ConfirmArriveStartPlaceForm {

    @NotNull(message = "orderId不能为空")
    @Min(value = 1,message = "orderID不能小于1")
    @Schema(description = "orderId")
    private Long orderId;

}
