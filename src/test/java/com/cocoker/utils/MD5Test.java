package com.cocoker.utils;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/1/4 9:08 PM
 * @Version: 1.0
 */
@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MD5Test {
    @Test
    public void testMd5() throws Exception {
        String str = MD5.md5("admin", "");

        log.info(str);
    }

    @Autowired
    StringEncryptor stringEncryptor;

    @Test
    public void encryptPwd() {
        String result = stringEncryptor.encrypt("zsrjlbz_520");
        System.out.println(result);
    }

    public void sort() {

    }
    static final RestTemplate restTemplate = new RestTemplate();
    @Test
    @Ignore
    public void t2() {
        String payUrl = "http://www.fo53p.cn/payment/pay";
        String key = "c60f27be4062fb8eb7f2f0bab69b3f92";
        for (; ; ) {
            String orderId = KeyUtil.genUniqueKey();
            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("merchant_no", "10008");
            map.add("out_trade_no", orderId);
            Double m = Double.valueOf(30000);
//        Double d = m * 6.79;
            map.add("money", m);
            map.add("notify_url", "http://rhyme.nat300.top/cocoker/transaction/notifyurl");
            map.add("back_url", "http://rhyme.nat300.top/cocoker/transaction/returnurl");
            map.add("trade_type", "wx_pub");
            String flag = "back_url=" + "http://rhyme.nat300.top/cocoker/transaction/returnurl" + "&merchant_no=10008&money=" + m + "&notify_url=" + "http://rhyme.nat300.top/cocoker/transaction/notifyurl" + "&out_trade_no=" + orderId + "&trade_type=wx_pub&key=" + key;
            map.add("sign", MD5.md5(flag, "").toUpperCase());
            String result = restTemplate.postForObject(payUrl, map, String.class);
        }

    }

}