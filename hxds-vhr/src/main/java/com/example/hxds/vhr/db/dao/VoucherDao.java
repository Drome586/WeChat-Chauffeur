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

    public HashMap searchVoucherById(long id);

    public int updateVoucherStatus(Map param);

    public ArrayList<HashMap> searchVoucherTakeCount(Long[] ids);

    public int deleteVoucherByIds(Long[] ids);

    /*
    查询未领取的代金券
     */
    public ArrayList<HashMap> searchUnTakeVoucherByPage(Map param);
    public long searchUnTakeVoucherCount(Map param);

    /*
    查询未使用代金券总数量
     */
    public ArrayList<HashMap> searchUnUseVoucherByPage(Map param);
    public long searchUnUseVoucherCount(Map param);

    /*
    查询已使用代金券总数量
     */
    public ArrayList<HashMap> searchUsedVoucherByPage(Map param);
    public long searchUsedVoucherCount(Map param);

    public int takeVoucher(long id);

    public HashMap searchBestUnUseVoucher(Map param);

    public int updateUsedCount(long id);


}




