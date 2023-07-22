package com.example.hxds.vhr.db.dao;


import com.example.hxds.vhr.db.pojo.VoucherEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface VoucherDao {
    public int insert(VoucherEntity entity);

    public ArrayList<String> searchIdByUUID(ArrayList<String> list);

    public ArrayList<HashMap> searchVoucherByPage(Map param);

    public long searchVoucherCount(Map param);
}




