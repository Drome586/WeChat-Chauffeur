package com.example.hxds.bff.driver.controller.form;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "删除腾讯云COS文件表单")
public class DeleteCosFileForm {
    @NotBlank(message = "Paths不能为空")
    @Schema(description = "云文件路径数组")
    private String[] pathes;
}
