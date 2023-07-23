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

    public int deleteVoucherByIds(Long[] ids);

    public PageUtils searchUnTakeVoucherByPage(Map param);

    public PageUtils searchUnUseVoucherByPage(Map param);

    public PageUtils searchUsedVoucherByPage(Map param);

    public long searchUnUseVoucherCount(Map param);

    public boolean takeVoucher(Map param);

}
