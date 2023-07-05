package com.example.hxds.nebula.controller;

import com.example.hxds.common.exception.HxdsException;
import com.example.hxds.common.util.R;
import com.example.hxds.nebula.service.MonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/monitoring")
@Tag(name = "monitoringController",description = "监控服务Web接口")
@Slf4j
public class MonitoringController {

    @Resource
    private MonitoringService monitoringService;

    /*
    @RequestPart 用来接受文件
     */

    @PostMapping("/uploadRecordFile")
    @Operation(summary = "上传代驾录音文件")
    public R uploadRecordFile(@RequestPart("file")MultipartFile file,@RequestPart("name") String name,
                              @RequestPart(value = "text",required = false) String text){

        if(file.isEmpty()){
            throw new HxdsException("录音文件不能为空");
        }
        monitoringService.monitoring(file,name,text);
        return R.ok();
    }
}
