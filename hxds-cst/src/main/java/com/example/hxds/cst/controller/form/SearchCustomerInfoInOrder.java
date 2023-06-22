package com.example.hxds.cst.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "查询订单中的客户信息")
public class SearchCustomerInfoInOrder {

    @NotNull(message = "客户id不能为空")
    @Min(value = 1,message = "客户id最小为1")
    @Schema(description = "客户Id")
    private Long customerId;
}
