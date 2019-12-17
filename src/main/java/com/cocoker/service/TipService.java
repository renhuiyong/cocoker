package com.cocoker.service;

import com.cocoker.beans.Tip;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/5/5 8:58 AM
 * @Version: 1.0
 */
public interface TipService {
    Tip findFirst();

    int updTip(String msg);

    Integer getTurnover();

    void setTurnover();

    int updReturnUrl(String url);

    //返回地址
    String getReturnUrl();

    //获取维护
    String getIfMaintain();

    //设置维护
    void setIfMaintain(boolean b);

    //获取充值金额
    Tip findAllRechargeMoeny();

    //获取下单金额
    Tip findOrderMoney();

    int setRechargeMoeny(String str);


    int setOrderMoeny(String str);

    //跳到其他地方
    String getOtherUrlOpenid();
}
