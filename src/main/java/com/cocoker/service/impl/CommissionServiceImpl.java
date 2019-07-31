package com.cocoker.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cocoker.beans.Commission;
import com.cocoker.dao.CommissionDao;
import com.cocoker.service.CommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/1/8 2:03 PM
 * @Version: 1.0
 */
@Service
public class CommissionServiceImpl implements CommissionService{
    @Autowired
    private CommissionDao commissionDao;

    @Override
    public String findByCOpenid(String openid) {
        return commissionDao.findByOpenid(openid);
    }

    @Override
    public Commission save(Commission commission) {
        return commissionDao.save(commission);
    }

    @Override
    public List<JSONObject> findCommissionTop50() {
        return commissionDao.findCommissionTop50();
    }

    @Override
    public List<Commission> findCommissionsByCOpenidEqualsOOrderByCIdDesc(String openid) {
        return commissionDao.findToDayCommissionsByCOpenidEqualsOOrderByCIdDesc(openid);
    }

    @Override
    public List<Commission> findAllDayCommissionsByCOpenidEqualsOOrderByCIdDesc(String openid) {
        return commissionDao.findAllDayCommissionsByCOpenidEqualsOOrderByCIdDesc(openid);
    }

    @Override
    public String findByCOpenidAndTime(String openid) {
        return commissionDao.findByCOpenidAndTime(openid);
    }
}
