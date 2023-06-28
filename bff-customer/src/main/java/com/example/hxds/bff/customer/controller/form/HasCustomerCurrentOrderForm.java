package com.example.hxds.bff.customer.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查询乘客是否存在当前订单的表单")
public class HasCustomerCurrentOrderForm {

    @Schema(description = "客户ID")
    private Long customerId;
}
