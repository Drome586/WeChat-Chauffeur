package com.example.hxds.nebula.service;

import com.example.hxds.nebula.controller.vo.InsertOrderGpsVo;

import java.util.ArrayList;

public interface OrderGpsService {
    //用List的原因是，当坐标位置收集到满一定的数量之后，再交给后端，减少后端的访问压力
    /*
    这边用InsertOrderGpsVo的目的是因为传过来的是一个数组，web端用的是form类，去验证的ArrayList，Vo继承了Entity，然后要验证Entity里面的每一个数据。
     */
    public int insertOrderGps(ArrayList<InsertOrderGpsVo> list);
}
