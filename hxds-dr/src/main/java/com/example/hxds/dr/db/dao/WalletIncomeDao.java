package com.example.hxds.dr.db.dao;

import com.example.hxds.dr.db.pojo.WalletIncomeEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface WalletIncomeDao {

    //  平台发放奖励就是往司机账单里充钱
    public int insert(WalletIncomeEntity entity);

}




