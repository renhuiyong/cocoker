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

    private String memberid;

    private String orderid;

    private String amount;

    private String datetime;

    private String returncode;

    private String transaction_id;

    private String attach;

    private String sign;

}
