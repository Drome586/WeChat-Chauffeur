package com.example.hxds.bff.customer.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查询客户OpenId的表单")
public class SearchCustomerOpenIdForm {
    @Schema(description = "客户ID")
    private Long customerId;
}

