package com.example.hxds.mis.api.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.hxds.common.exception.HxdsException;
import com.example.hxds.common.util.PageUtils;
import com.example.hxds.common.util.R;
import com.example.hxds.mis.api.controller.form.SearchOrderByPageForm;
import com.example.hxds.mis.api.controller.form.SearchOrderComprehensiveInfoForm;
import com.example.hxds.mis.api.controller.form.SearchOrderLastGpsForm;
import com.example.hxds.mis.api.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/order")
@Tag(name = "OrderController",description = "Mis中的订单管理Web接口")
public class OrderController {

    @Resource
    private OrderService orderService;


    @PostMapping("/searchOrderByPage")
    @Operation(summary = "查询订单分页记录")
    @SaCheckPermission(value = {"ROOT","ORDER:SELECT"},mode = SaMode.OR)
    public R searchOrderByPage(@RequestBody @Valid SearchOrderByPageForm form){
        PageUtils pageUtils = orderService.searchOrderByPage(form);
        return R.ok().put("result",pageUtils);
    }

    @PostMapping("/searchOrderComprehensiveInfo")
    @Operation(summary = "查询汇总的订单")
    @SaCheckPermission(value = {"ROOT","ORDER:SELECT"},mode = SaMode.OR)
    public R searchOrderComprehensiveInfo(@RequestBody @Valid SearchOrderComprehensiveInfoForm form){
        HashMap result = orderService.searchOrderComprehensiveInfo(form.getOrderId());
        return R.ok().put("result",result);
    }

    @PostMapping("/searchOrderLastGps")
    @Operation(summary = "获取某个订单的最后的Gps定位")
    @SaCheckPermission(value = {"ROOT","ORDER:SELECT"},mode = SaMode.OR)
    public R searchOrderLastGps(@RequestBody @Valid SearchOrderLastGpsForm form){
        HashMap result = orderService.searchOrderLastGps(form);
        return R.ok().put("result",result);
    }

    @GetMapping("/downloadOrderStartLocationIn30Days")
    @SaCheckPermission(value = {"ROOT", "ORDER:SELECT"}, mode = SaMode.OR)
    @Operation(summary = "查询30天内订单上车点定位记录")
    public void downloadOrderStartLocationIn30Days(HttpServletResponse response) {
        ArrayList<HashMap> result = orderService.searchOrderStartLocationIn30Days();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment;filename=heat_data.xls");
        try (ServletOutputStream out = response.getOutputStream();
             BufferedOutputStream bff = new BufferedOutputStream(out);) {
            ExcelWriter writer = ExcelUtil.getWriter();
            writer.write(result, true);
            writer.flush(bff);
            writer.close();
        } catch (Exception e) {
            throw new HxdsException("Excel文件下载失败");
        }
    }

}
