package com.example.hxds.mis.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "更新代金券状态")
public class UpdateVoucherStatusForm {
    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id不能小于1")
    @Schema(description = "代金券ID")
    private Long id;

    @NotNull(message = "status不能为空")
    @Range(min = 1, max = 3, message = "status内容不正确")
    @Schema(description = "代金券状态")
    private Byte status;

    @NotBlank(message = "uuid不能为空")
    @Schema(description = "uuid")
    private String uuid;
}

