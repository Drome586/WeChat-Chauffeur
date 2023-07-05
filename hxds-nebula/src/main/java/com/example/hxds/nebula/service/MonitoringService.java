package com.example.hxds.nebula.service;

import org.springframework.web.multipart.MultipartFile;

public interface MonitoringService {

    public void monitoring(MultipartFile file,String name,String text);
}
