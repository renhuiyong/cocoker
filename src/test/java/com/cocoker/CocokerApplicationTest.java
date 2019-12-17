package com.cocoker;

import com.cocoker.utils.DateUtil;
import com.cocoker.utils.MD5;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/1/4 12:20 PM
 * @Version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CocokerApplicationTest {

    @Test
    public void contextLoads() {


        System.out.println(MD5.md5("123456", "hi"));

//        System.out.println(MD5.md5("1234567890abcdefghijklmnopqrstuvwxyz", ""));
//        DecimalFormat df = new DecimalFormat("#.00");
//        String str = df.format(Double.valueOf("1"));
//        System.out.println(str);
    }


    @Test
    public void testDate() {
        Date time = DateUtil.getTime(01, 00, 00);
        System.out.println(time);
    }

    @Test
    public void other() {
        for (int i = 0; i < 50; i++) {
            System.out.println(String.format("%.2f", (Math.random() * 0.1 + 0.1)));
        }
    }

}