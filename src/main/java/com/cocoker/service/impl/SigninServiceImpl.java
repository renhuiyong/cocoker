package com.cocoker.service.impl;

import com.cocoker.beans.Signin;
import com.cocoker.dao.SigninDao;
import com.cocoker.service.SigninService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019-08-31 21:24
 * @Version: 1.0
 */
@Service
public class SigninServiceImpl implements SigninService {

    @Autowired
    private SigninDao signinDao;

    @Override
    public Signin insertSignin(Signin signin) {
        return signinDao.save(signin);
    }

    @Override
    public Signin selectSigninBytoDay(String openid) {
        return signinDao.selectSigninBytoDay(openid);
    }

    @Override
    public String selectSigninDoesItExist(String openid) {
        return signinDao.selectSigninDoesItExist(openid);
    }
}
