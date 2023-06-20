package com.example.hxds.bff.driver.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "开始接单之前清空消息队列")
public class ClearNewOrderQueueForm {

    @Schema(description = "用户ID")
    private Long userId;
}
