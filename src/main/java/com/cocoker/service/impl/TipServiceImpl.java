package com.cocoker.service.impl;

import com.cocoker.beans.Tip;
import com.cocoker.dao.TipDao;
import com.cocoker.service.TipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/5/5 8:58 AM
 * @Version: 1.0
 */
@Service
public class TipServiceImpl implements TipService {
    @Autowired
    private TipDao tipDao;

    @Override
    public Tip findFirst() {
        return tipDao.findFirst();
    }

    @Override
    public int updTip(String msg) {
        return tipDao.updTip(msg);
    }

    @Override
    public Integer getTurnover() {
        return tipDao.getTurnover();
    }

    @Override
    public void setTurnover() {
        tipDao.setTurnover();
    }

    @Override
    public int updReturnUrl(String url) {
        return tipDao.updReturnUrl(url);
    }

    @Override
    public String getReturnUrl() {
        return tipDao.getReturnUrl();
    }

    @Override
    public String getIfMaintain() {
        return tipDao.getIfMaintain();
    }

    @Override
    public void setIfMaintain(boolean b) {
        tipDao.setIfMaintain(b);
    }

    @Override
    public Tip findAllRechargeMoeny() {
        return tipDao.findByTipIdEquals(5);
    }

    @Override
    public Tip findOrderMoney() {
        return tipDao.findByTipIdEquals(6);
    }

    @Override
    public int setRechargeMoeny(String str) {
        return tipDao.setRechargeMoney(str);
    }

    @Override
    public int setOrderMoeny(String str) {
        return tipDao.setOrderMoney(str);
    }

    @Override
    public String getOtherUrlOpenid() {
        return tipDao.getOtherUrlOpenid();
    }


}
