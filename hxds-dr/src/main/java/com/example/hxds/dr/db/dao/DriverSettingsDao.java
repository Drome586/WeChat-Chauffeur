package com.example.hxds.dr.db.dao;

import com.example.hxds.dr.db.pojo.DriverSettingsEntity;

import java.util.Map;

/**
 * @Entity com.example.hxdsdr.db.pojo.DriverSettingsEntity
 */
public interface DriverSettingsDao {

    //插入司机设定记录，默认值
    public int insertDriverSettings(DriverSettingsEntity entity);

    public String searchDriverSettings(long driverId);
}




