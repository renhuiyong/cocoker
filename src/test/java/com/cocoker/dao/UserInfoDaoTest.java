package com.cocoker.dao;

import com.cocoker.beans.UserInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2018/12/23 4:12 PM
 * @Version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserInfoDaoTest {
    @Autowired
    private UserInfoDao userInfoDao;


    @Test
    public void hello(){
//        UserInfo userInfo = new UserInfo();
//        userInfo.setOtype(1);
//        userInfo.setUstatus(0);
//        userInfo.setUsername("admin");
//        userInfo.setUpwd("123456");
//        UserInfo save = userInfoDao.save(userInfo);
//        Assert.assertNotNull(save);
    }

    @Test
    public void findByOpenId(){
//        UserInfo byOpenid = userInfoDao.findByOpenid("9527");
//        Assert.assertNotNull(byOpenid);
    }
}