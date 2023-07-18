package com.example.hxds.dr.db.dao;


import com.example.hxds.dr.db.pojo.WalletEntity;

import java.util.HashMap;
import java.util.Map;

public interface WalletDao {

    //添加默认的钱包记录
    public int insert(WalletEntity entity);

    //修改钱包余额
    public int updateWalletBalance(Map param);
}




