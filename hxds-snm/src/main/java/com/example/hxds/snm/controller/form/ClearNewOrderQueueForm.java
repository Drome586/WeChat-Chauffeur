package com.example.hxds.snm.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class ClearNewOrderQueueForm {
    @NotNull(message = "userId不能为空")
    @Min(value = 1,message = "userId最小不能小于1")
    @Schema(description = "用户ID")
    private Long userId;
}
