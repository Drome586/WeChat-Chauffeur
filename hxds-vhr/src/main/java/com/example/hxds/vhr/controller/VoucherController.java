package com.example.hxds.vhr.controller;

import cn.hutool.core.bean.BeanUtil;
import com.example.hxds.common.util.PageUtils;
import com.example.hxds.common.util.R;
import com.example.hxds.vhr.controller.form.SearchVoucherByPageForm;
import com.example.hxds.vhr.service.VoucherService;
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
@RequestMapping("/voucher")
@Tag(name = "VoucherController",description = "代金券Web接口")
public class VoucherController {

    @Resource
    private VoucherService voucherService;

    @PostMapping("/searchVoucherByPage")
    @Operation(summary = "查询代金券分页记录")
    public R searchVoucherByPage(@RequestBody @Valid SearchVoucherByPageForm form){
        Map map = BeanUtil.beanToMap(form);
        int page = form.getPage();
        int length = form.getLength();
        int start = (page-1)*length;
        map.put("start",start);

        PageUtils pageUtils = voucherService.searchVoucherByPage(map);
        return R.ok().put("result",pageUtils);
    }
}
