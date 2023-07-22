package com.example.hxds.mis.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(description = "删除代金券的表单")
public class DeleteVoucherByIdsForm {
    @NotEmpty(message = "ids不能为空")
    @Schema(description = "主键数组")
    private Long[] ids;
}

