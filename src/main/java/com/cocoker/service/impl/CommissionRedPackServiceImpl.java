package com.cocoker.service.impl;

import com.cocoker.beans.CommissionRedPack;
import com.cocoker.dao.CommissionRedPackDao;
import com.cocoker.service.CommissionRedPackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019-09-23 12:26
 * @Version: 1.0
 */
@Service
public class CommissionRedPackServiceImpl implements CommissionRedPackService {
    @Autowired
    private CommissionRedPackDao commissionRedPackDao;

    @Override
    public CommissionRedPack selectRedpackDoesItExist(String openid, String money) {
        return commissionRedPackDao.selectRedpackDoesItExist(openid, money);
    }

    @Override
    public CommissionRedPack save(CommissionRedPack commissionRedPack) {
        return commissionRedPackDao.save(commissionRedPack);
    }
}
