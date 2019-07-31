package com.cocoker.service;

import com.cocoker.VO.PayReturnParamVO;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.RefundResponse;

import java.util.Map;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2018/12/20 10:12 AM
 * @Version: 1.0
 */
public interface PayService {
    Map<String, Object> create(String openid, String money, String rm);

    PayReturnParamVO create2(String openid, String money);

}
