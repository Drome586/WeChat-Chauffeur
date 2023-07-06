package com.example.hxds.nebula.controller.form;

import com.example.hxds.nebula.controller.vo.InsertOrderGpsVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;

@Data
public class InsertOrderGpsForm {

    @NotEmpty(message = "list不能为空")
    @Schema(description = "GPS数据")
    private ArrayList<@Valid InsertOrderGpsVo> list;
}
