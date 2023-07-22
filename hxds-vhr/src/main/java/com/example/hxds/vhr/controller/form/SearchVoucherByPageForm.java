package com.example.hxds.vhr.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "查询代金券分页的表单")
public class SearchVoucherByPageForm {
    @Schema(description = "代金券名称")
    private String name;

    @Schema(description = "代金券标签")
    private String tag;

    @Pattern(regexp = "^无限量$|^有限量$", message = "totalQuota内容不正确")
    @Schema(description = "代金券数量")
    private String totalQuota;

    @Range(min = 1, max = 3, message = "type内容不正确")
    @Schema(description = "赠送类型")
    private Byte type;

    @Pattern(regexp = "^无限制$|^有限制$", message = "limitQuota内容不正确")
    @Schema(description = "每个用户领券限制")
    private String limitQuota;

    @Range(min = 1, max = 3, message = "status内容不正确")
    @Schema(description = "代金券状态")
    private Byte status;

    @Pattern(regexp = "^有效天数$|^有效日期$|^无期限$", message = "timeType内容不正确")
    @Schema(description = "代金券有效期限")
    private String timeType;

    @NotNull(message = "page不能为空")
    @Min(value = 1, message = "page不能小于1")
    @Schema(description = "页数")
    private Integer page;

    @NotNull(message = "length不能为空")
    @Range(min = 10, max = 50, message = "length必须在10~50之间")
    @Schema(description = "每页记录数")
    private Integer length;
}

