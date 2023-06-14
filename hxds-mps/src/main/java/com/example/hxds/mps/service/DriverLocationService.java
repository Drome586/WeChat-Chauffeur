package com.example.hxds.mps.service;

import java.util.ArrayList;
import java.util.Map;

public interface DriverLocationService {
    //缓存司机定位信息，保存到redis中
    public void updateLocationCache(Map param);
    //从redis中删除司机实时定位缓存信息
    public void removeLocationCache(long driverId);

    //查找附近适合接单的司机
    public ArrayList searchBefittingDriverAboutOrder(double startPlaceLatitude,
                                                     double startPlaceLongitude,
                                                     double endPlaceLatitude,
                                                     double endPlaceLongitude,
                                                     double mileage);
}
