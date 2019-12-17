package com.cocoker.service;

import com.cocoker.beans.Signin;

import java.util.List;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019-08-31 21:21
 * @Version: 1.0
 */
public interface SigninService {

    Signin insertSignin(Signin signin);

    Signin selectSigninBytoDay(String openid);

    String selectSigninDoesItExist(String openid);

}
