package com.example.hxds.vhr.service;

import com.example.hxds.common.util.PageUtils;
import com.example.hxds.vhr.db.pojo.VoucherEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface VoucherService {

    public PageUtils searchVoucherByPage(Map param);

    public int insert(VoucherEntity entity);

    public int updateVoucherStatus(Map param);

}
