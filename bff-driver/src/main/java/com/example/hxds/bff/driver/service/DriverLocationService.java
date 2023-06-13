package com.example.hxds.bff.driver.service;

import com.example.hxds.bff.driver.controller.form.RemoveLocationCacheForm;
import com.example.hxds.bff.driver.controller.form.UpdateLocationCacheForm;

public interface DriverLocationService {

    public void updateLocationCache(UpdateLocationCacheForm form);

    public void removeLocationCache(RemoveLocationCacheForm form);
}
