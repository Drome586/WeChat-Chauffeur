package com.example.hxds.dr.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.example.hxds.dr.db.dao.DriverSettingsDao;
import com.example.hxds.dr.service.DriverSettingsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class DriverSettingsServiceImpl implements DriverSettingsService {
    @Resource
    private DriverSettingsDao settingsDao;

    @Override
    public HashMap searchDriverSettings(long driverId) {
        String settings = settingsDao.searchDriverSettings(driverId);
        HashMap map = JSONUtil.parseObj(settings).toBean(HashMap.class);
        boolean bool = MapUtil.getInt(map, "listenService") == 1 ? true : false;
        map.replace("listenService", bool);
        bool = MapUtil.getInt(map, "autoAccept") == 1 ? true : false;
        map.replace("autoAccept", bool);
        return map;
    }
}
