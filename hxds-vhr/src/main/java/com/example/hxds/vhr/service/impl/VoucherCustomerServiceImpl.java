package com.example.hxds.vhr.service.impl;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.example.hxds.common.exception.HxdsException;
import com.example.hxds.vhr.db.dao.VoucherCustomerDao;
import com.example.hxds.vhr.service.VoucherCustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class VoucherCustomerServiceImpl implements VoucherCustomerService {

    @Resource
    private VoucherCustomerDao voucherCustomerDao;

    @Override
    @Transactional
    @LcnTransaction
    public String useVoucher(Map param) {
        String discount = voucherCustomerDao.validCanUseVoucher(param);
        if(discount != null){
            int rows = voucherCustomerDao.bindVoucher(param);
            if(rows != 1){
                throw new HxdsException("无代金券，不可用");
            }
            return discount;
        }
        throw new HxdsException("代金券不可用");
    }
}
