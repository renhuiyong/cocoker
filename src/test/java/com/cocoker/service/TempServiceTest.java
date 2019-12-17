package com.cocoker.service;

import com.cocoker.dao.TempDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/1/9 11:36 PM
 * @Version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TempServiceTest {
    @Autowired
    private TempDao tempDao;
    @Test
    @Transactional
    public void delTemp() throws Exception {
//        int i = tempDao.deleteByTIdIn(Arrays.asList(35, 36));
//        Assert.assertNotEquals(0,i);
    }

}