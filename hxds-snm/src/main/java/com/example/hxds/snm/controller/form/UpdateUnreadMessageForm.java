package com.example.hxds.snm.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "把未读消息更新成已读的表单")
public class UpdateUnreadMessageForm {
    @NotBlank(message = "id不能为空")
    @Schema(description = "ref消息ID")
    private String id;
}

