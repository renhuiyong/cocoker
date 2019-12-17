package com.cocoker.beans;

import lombok.Data;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/1/5 2:29 PM
 * @Version: 1.0
 */
@Data
public class PayNotifyBean {

    //平台分配商户号
    private String appid;

    //商户提交的订单号
    private String order_no;

    //免签支付平台的订单号
    private String order_id;

    //单位元
    private String amount;

    private String time;

    private String sign;

//    private String memberid;
//
//    private String orderid;
//
//    private String amount;
//
//    private String datetime;
//
//    private String returncode;
//
//    private String transaction_id;
//
//    private String attach;
//
//    private String sign;

}
