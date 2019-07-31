package com.cocoker.service.impl;

import com.cocoker.beans.UserInfo;
import com.cocoker.service.UserInfoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2018/12/24 8:43 PM
 * @Version: 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserInfoServiceImplTest {
    @Autowired
    private UserInfoService userInfoService;

    @Test
    public void findOne() throws Exception {
//        UserInfo one = userInfoService.findOne(757);
//        Assert.assertNotNull(one);
    }

    @Test
    public void save() throws Exception {
    }

    @Test
    public void findList() throws Exception {
    }

    @Test
    public void delOne() throws Exception {
    }

    @Test
    public void findByOpenId() throws Exception {

    }

    //    private static Integer num = 3000;
    @Test
    public void testTime() {

        Random r = new Random();
//        Integer i = r.nextInt(max - min + 1) + min;
////        return i;
//        if(r.nextInt(10) % 2 == 0){
//            num += r.nextInt(50);
//        }else {
//            num -= r.nextInt(50);
//        }
//        if(num < 1500){
//            num += 50;
//        }
//        if(num > 4500){
//            num -= 50;
//        }
    }
}