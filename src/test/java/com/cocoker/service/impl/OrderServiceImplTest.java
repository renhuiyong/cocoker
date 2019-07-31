package com.cocoker.service.impl;

import com.cocoker.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2018/12/29 10:11 PM
 * @Version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceImplTest {

    @Autowired
    private OrderService orderService;
    @Test
    public void findAllOrder() throws Exception {
    }

    @Test
    public void addOrder() throws Exception {
       // orderService.addOrder("inc","1111","30","ooooo","");
    }

}