package com.example.hxds.vhr.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.PageUtil;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.ctc.wstx.util.DataUtil;
import com.example.hxds.common.util.PageUtils;
import com.example.hxds.vhr.db.dao.VoucherCustomerDao;
import com.example.hxds.vhr.db.dao.VoucherDao;
import com.example.hxds.vhr.db.pojo.VoucherCustomerEntity;
import com.example.hxds.vhr.db.pojo.VoucherEntity;
import com.example.hxds.vhr.service.VoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class VoucherServiceImpl implements VoucherService {

    @Resource
    private VoucherDao voucherDao;

    @Resource
    private VoucherCustomerDao voucherCustomerDao;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public PageUtils searchVoucherByPage(Map param) {
        ArrayList<HashMap> list = null;
        long count = voucherDao.searchVoucherCount(param);
        if(count > 0){
            list = voucherDao.searchVoucherByPage(param);
        }else{
            list = new ArrayList();
        }
        int start = MapUtil.getInt(param,"start");
        int length = MapUtil.getInt(param,"length");

        PageUtils pageUtils = new PageUtils(list, count, start, length);
        return pageUtils;
    }

    @Override
    @Transactional
    @LcnTransaction
    public int insert(VoucherEntity entity) {
        int rows = voucherDao.insert(entity);
        return rows;
    }

    @Override
    @LcnTransaction
    @Transactional
    public int updateVoucherStatus(Map param) {
        int rows = voucherDao.updateVoucherStatus(param);
        if(rows == 1){
            Long id = (Long) param.get("id");
            Byte status = (Byte) param.get("status");
            String uuid = (String) param.get("uuid");
            if(status == 1){
                HashMap result = voucherDao.searchVoucherById(id);
                VoucherEntity entity = BeanUtil.toBean(result, VoucherEntity.class);
                //将代金券信息缓存到redis里面去
                this.saveVoucherCache(entity);
            }else if(status == 3){
                //这是已下架的状态，将缓存中的代金券信息删除掉
                redisTemplate.delete("voucher_info_"+uuid);
                redisTemplate.delete("voucher_"+uuid);
            }
        }
        return rows;
    }

    /*
    做批量删除，多选框，所以传入的是个数组
     */
    @Override
    @Transactional
    @LcnTransaction
    public int deleteVoucherByIds(Long[] ids) {
        ArrayList<HashMap> list = voucherDao.searchVoucherTakeCount(ids);
        ArrayList<Long> temp = new ArrayList<>();

        list.forEach(one->{
            long id = MapUtil.getLong(one,"id");
            String uuid = MapUtil.getStr(one, "uuid");
            Long totalQuota = MapUtil.getLong(one, "totalQuota");
            Long takeCount = MapUtil.getLong(one, "takeCount");
            if(takeCount == 0){
                //查询redis中的缓存记录
                if(redisTemplate.hasKey("voucher_"+uuid)){
                    long num = Long.parseLong(redisTemplate.opsForValue().get("voucher_"+uuid).toString());
                    //没有人领取代金券
                    if(num == totalQuota){
                        temp.add(id);
                        //删除redis缓存
                        redisTemplate.delete("voucher_"+uuid);
                        redisTemplate.delete("voucher_info_"+uuid);

                    }else{
                        log.debug("主键是"+id + "的代金券不能被删除");
                    }

                }else{
                    temp.add(id);
                }
            }else{
                //该记录不能被删除
                log.debug("主键是"+id + "的代金券不能被删除");
            }
        });
        if(temp.size() > 0){
            ids = temp.toArray(new Long[temp.size()]);
            int rows = voucherDao.deleteVoucherByIds(ids);
            return rows;
        }
        return 0;
    }

    @Override
    public PageUtils searchUnTakeVoucherByPage(Map param) {
        long count = voucherDao.searchUnTakeVoucherCount(param);
        ArrayList<HashMap> list = null;
        if(count > 0){
            list = voucherDao.searchUnTakeVoucherByPage(param);
        }else{
            list = new ArrayList<>();
        }
        int start = MapUtil.getInt(param,"start");
        int length = MapUtil.getInt(param,"length");
        PageUtils pageUtils = new PageUtils(list, count, start, length);
        return pageUtils;
    }

    @Override
    public PageUtils searchUnUseVoucherByPage(Map param) {
        long count = voucherDao.searchUnUseVoucherCount(param);
        ArrayList<HashMap> list = null;
        if(count > 0){
            list = voucherDao.searchUnUseVoucherByPage(param);
        }else{
            list = new ArrayList<>();
        }
        int start = MapUtil.getInt(param,"start");
        int length = MapUtil.getInt(param,"length");
        PageUtils pageUtils = new PageUtils(list, count, start, length);
        return pageUtils;
    }

    @Override
    public PageUtils searchUsedVoucherByPage(Map param) {
        long count = voucherDao.searchUsedVoucherCount(param);
        ArrayList<HashMap> list = null;
        if (count > 0) {
            list = voucherDao.searchUsedVoucherByPage(param);
        } else {
            list = new ArrayList<>();
        }
        int start = MapUtil.getInt(param, "start");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, start, length);
        return pageUtils;
    }

    @Override
    public long searchUnUseVoucherCount(Map param) {
        long count = voucherDao.searchUnUseVoucherCount(param);
        return count;
    }

    @Override
    @Transactional
    @LcnTransaction
    public boolean takeVoucher(Map param) {
        String uuid = MapUtil.getStr(param, "uuid");
        long id = MapUtil.getLong(param, "id");
        long customerId = MapUtil.getLong(param, "customerId");
        if (!(redisTemplate.hasKey("voucher_" + uuid) && redisTemplate.hasKey("voucher_info_" + uuid))) {
            return false;
        }
        //开启redis事务，领取代金券
        /**
         * 1.先判断缓存
         * 2.判断代金券是否仅限一人一张，是否已经领取过了
         * 3.判断有效期限
         * 4.判断代金券是否为无限制（==0）
         * 5.有总数量的限制，不能超售
         */
        boolean result=(Boolean)redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch("voucher_" + uuid);
                Map entries = operations.opsForHash().entries("voucher_info_" + uuid); //代金券信息
                int totalQuota = MapUtil.getInt(entries, "totalQuota"); //代金券总数
                short limitQuota = Short.parseShort(entries.get("limitQuota").toString()); //限制领取

                //代金券有限制，判断是否领取过了
                if(limitQuota>0){
                    HashMap condition = new HashMap() {{
                        put("customerId", customerId);
                        put("voucherId", id);
                    }};
                    //查询该乘客已经领取的代金券的数量
                    long count = voucherCustomerDao.searchTakeVoucherNum(condition);
                    if(count>=limitQuota){
                        return false;
                    }
                }
                //领取代金券后的有效期
                String startTime = null;
                String endTime = null;
                if(entries.get("timeType")!=null){
                    byte timeType = Byte.parseByte(entries.get("timeType").toString()); //有效期类型
                    if(timeType==1){
                        int days = MapUtil.getInt(entries, "days");
                        startTime=DateUtil.today();
                        endTime=new DateTime().offset(DateField.DAY_OF_MONTH,days).toDateStr();
                    }
                    else if(timeType==2){
                        startTime = MapUtil.getStr(entries, "startTime");
                        endTime = MapUtil.getStr(entries, "endTime");
                    }
                }
                VoucherCustomerEntity entity=new VoucherCustomerEntity();
                entity.setVoucherId(id);
                entity.setCustomerId(customerId);
                entity.setStartTime(startTime);
                entity.setEndTime(endTime);


                //代金券没有限量
                if(totalQuota==0){
                    int rows = voucherDao.takeVoucher(id);  //更新代金券数量
                    if(rows==1){
                        rows=voucherCustomerDao.insert(entity); //记录领取的代金券
                        return rows==1?true:false;
                    }else {
                        return false;
                    }
                }
                //代金券有总数量上的限制
                else {
                    String temp = operations.opsForValue().get("voucher_" + uuid).toString();
                    int num = Integer.parseInt(temp);
                    //扣减redis缓存
                    if(num>0){
                        num--;
                        operations.multi();
                        operations.opsForValue().set("voucher_" + uuid, num);   //更新代金券数量
                        operations.exec();
                        int rows = voucherDao.takeVoucher(id);
                        if(rows==1){
                            rows=voucherCustomerDao.insert(entity);     //记录领取的代金券
                            return rows==1?true:false;
                        }else {
                            return false;
                        }
                    }
                    else{
                        operations.unwatch();
                        //删除缓存
                        operations.delete("voucher_" + uuid);
                        operations.delete("voucher_info_" + uuid);
                        return false;
                    }
                }
            }
        });
        return result;
    }

    /**
     * 该私有方法的作用是将将上线的 “代金券信息” 和 ”代金券数量“ 缓存到redis中，并且设置过期时间
     * @param entity
     */
    private void saveVoucherCache(VoucherEntity entity){
        String uuid = entity.getUuid();
        HashMap map = new HashMap(){{
            put("totalQuota", entity.getTotalQuota());
            put("discount", entity.getDiscount());
            put("limitQuota", entity.getLimitQuota());
            put("type", entity.getType());
            put("withAmount", entity.getWithAmount());
            put("timeType", entity.getTimeType());
            put("startTime", entity.getStartTime());
            put("endTime", entity.getEndTime());
            put("days", entity.getDays());
        }};
        redisTemplate.opsForHash().putAll("voucher_info_"+uuid,map);
        redisTemplate.opsForValue().set("voucher_"+uuid,entity.getTotalQuota());

        //如果代金券有时间限制，就设置过期时间(只能设置总的过期时间）
        if(entity.getTimeType() != null && entity.getTimeType() == 2){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(entity.getStartTime() + " 00:00:00", formatter);
            LocalDateTime endTime = LocalDateTime.parse(entity.getEndTime() + " 00:00:00", formatter);
            Duration duration = Duration.between(startTime,endTime);

            redisTemplate.expire("voucher_info_"+uuid,duration);
            redisTemplate.expire("voucher_"+uuid,duration);
        }
    }
}
