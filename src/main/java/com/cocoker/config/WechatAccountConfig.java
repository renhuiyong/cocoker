package com.cocoker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2018/12/23 4:41 PM
 * @Version: 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat")
public class WechatAccountConfig {

    //公众平台id
    private String mpAppId;

    //公众平台密匙
    private String mpAppSecret;

    //开放平台id
    private String openAppId;

    //开放平台密匙
    private String openAppSecret;

    //商户号
    private String mchId;

    //商户密匙
    private String mchKey;

    //商户证书路径
    private String keyPath;

    //异步通知
    private String notifyUrl;

//    -----
    private String customeridY1;

    private String customerKey1;

    private String notifyurlY1;

    private String returnurlY1;

    private String payUrl1;

    private String customeridY2;

    private String customerKey2;

    private String notifyurlY2;

    private String returnurlY2;

    private String payUrl2;

}

