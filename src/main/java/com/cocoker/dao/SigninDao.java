package com.cocoker.dao;

import com.cocoker.beans.Signin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019-08-31 21:19
 * @Version: 1.0
 */
public interface SigninDao extends JpaRepository<Signin, Integer> {

    @Query(value = "SELECT * FROM tbl_signin WHERE  to_days(create_time) = to_days(now()) and openid = :openid", nativeQuery = true)
    Signin selectSigninBytoDay(@Param("openid") String openid);


    @Query(value = "SELECT COUNT(*) as count FROM tbl_signin WHERE openid = :openid", nativeQuery = true)
    String selectSigninDoesItExist(@Param("openid") String openid);

}
