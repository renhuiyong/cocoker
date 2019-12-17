package com.cocoker.service;

import com.cocoker.beans.CommissionRedPack;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019-09-23 12:25
 * @Version: 1.0
 */
public interface CommissionRedPackService {
    CommissionRedPack selectRedpackDoesItExist(String openid, String money);

    CommissionRedPack save(CommissionRedPack commissionRedPack);


}
