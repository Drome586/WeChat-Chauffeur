package com.example.hxds.dr.db.dao;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface DriverDao {
    //判断是否存在司机
    public long hasDriver(Map param);

    //注册司机
    public int registerNewDriver(Map param);

    //查询司机主键值
    public String searchDriverId(String openId);

    //更新司机注册信息，保存填写信息
    public int updateDriverAuth(Map param);

    //查询姓名和性别
    public HashMap searchDriverNameAndSex(long driverId);

    //更新司机表archive字段
    public int updateDriverArchive(long driverId);

    //登陆验证
    public HashMap login(String openId);

    public HashMap searchDriverBaseInfo(long driverId);

    public ArrayList<HashMap> searchDriverByPage(Map param);

    public long searchDriverCount(Map param);

    public HashMap searchDriverAuth(long driverId);

    public HashMap searchDriverRealSummary(long driverId);

    public int updateDriverRealAuth(Map param);

    public HashMap searchDriverBriefInfo(long driverId);
}




