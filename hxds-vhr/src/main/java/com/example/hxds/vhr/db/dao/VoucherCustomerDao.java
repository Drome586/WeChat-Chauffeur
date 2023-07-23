package com.example.hxds.vhr.db.dao;


import com.example.hxds.vhr.db.pojo.VoucherCustomerEntity;

import java.util.Map;

public interface VoucherCustomerDao {
    /*
    领取代金券，防止超售
     */
    public int insert(VoucherCustomerEntity entity);

    public String validCanUseVoucher(Map param);

    public int bindVoucher(Map param);

    public long searchTakeVoucherNum(Map param);



}




