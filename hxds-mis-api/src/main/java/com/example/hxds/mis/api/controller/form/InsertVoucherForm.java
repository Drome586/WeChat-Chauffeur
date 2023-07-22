package com.example.hxds.mis.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@Schema(description = "添加代金券的表单")
public class InsertVoucherForm {
    @NotBlank(message = "name不能为空")
    @Schema(description = "代金券标题")
    private String name;

    @Schema(description = "描述文字")
    private String remark;

    @Schema(description = "代金券标签")
    private String tag;

    @NotNull(message = "totalQuota不能为空")
    @Min(value = 0, message = "totalQuota不能小于0")
    @Schema(description = "代金券数量")
    private Integer totalQuota;

    @NotNull(message = "discount不能为空")
    @Min(value = 1, message = "discount不能小于1")
    @Max(value = 20, message = "面额不能大于20")
    @Schema(description = "面额")
    private BigDecimal discount;

    @NotNull(message = "withAmount不能为空")
    @Min(value = 0, message = "withAmount不能小于0")
    @Schema(description = "最低消费限额")
    private BigDecimal withAmount;

    @NotNull(message = "type不能为空")
    @Range(min = 1, max = 3, message = "type内容不正确")
    @Schema(description = "代金券种类")
    private Byte type;

    @NotNull(message = "limitQuota不能为空")
    @Range(min = 0, max = 1, message = "limitQuota内容不正确")
    @Schema(description = "限制领取数量")
    private Short limitQuota;

    @Schema(description = "状态")
    private Byte status;

    @Range(min = 0, max = 2, message = "timeType内容不正确")
    @Schema(description = "有效期类型")
    private Byte timeType;

    @Min(value = 0, message = "days不能小于0")
    @Schema(description = "有效天数")
    private Short days;

    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$",
            message = "startTime内容不正确")
    @Schema(description = "起始日期")
    private String startTime;

    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$",
            message = "endTime内容不正确")
    @Schema(description = "截止日期")
    private String endTime;

}

