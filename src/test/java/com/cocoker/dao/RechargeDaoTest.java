package com.cocoker.dao;

import com.cocoker.beans.Recharge;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/1/4 12:21 PM
 * @Version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RechargeDaoTest {
    @Autowired
    private RechargeDao rechargeDao;

    @Test
    public void testSave(){
//        Recharge recharge = new Recharge();
//        recharge.setTmoney(new BigDecimal(7));
//        recharge.setTnickname("zhangsan");
//        recharge.setTyue(new BigDecimal(10));
//        Recharge r = rechargeDao.save(recharge);
//        Assert.assertNotNull(r);
    }
}