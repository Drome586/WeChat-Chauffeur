package com.example.hxds.bff.driver.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "接受新订单消息的表单")
public class ReceiveNewOrderMessageForm {

    @Schema(description = "用户ID")
    private Long userId;
}
