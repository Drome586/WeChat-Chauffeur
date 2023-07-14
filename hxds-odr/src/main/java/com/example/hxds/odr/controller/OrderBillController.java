package com.example.hxds.odr.controller;

import cn.hutool.core.bean.BeanUtil;
import com.example.hxds.common.util.R;
import com.example.hxds.odr.controller.form.UpdateBillFeeForm;
import com.example.hxds.odr.service.OrderBillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/bill")
@Tag(name = "OrderBillController", description = "订单费用账单Web接口")
public class OrderBillController {
    @Resource
    private OrderBillService orderBillService;

    @PostMapping("/updateBillFee")
    @Operation(summary = "更新订单账单费用")
    public R updateBillFee(@RequestBody @Valid UpdateBillFeeForm form) {
        Map param = BeanUtil.beanToMap(form);
        int rows = orderBillService.updateBillFee(param);
        return R.ok().put("rows", rows);
    }
}
