package com.cocoker.beans;

import lombok.Data;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/5/21 5:37 PM
 * @Version: 1.0
 */
@Data
public class PayReturnParamVOBean {
    private String amount;
    private String order_num;
    private String pay_url;
}
