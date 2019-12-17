package com.cocoker.dao;

import com.cocoker.beans.CommissionRedPack;
import com.cocoker.beans.Signin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019-09-23 12:14
 * @Version: 1.0
 */
public interface CommissionRedPackDao extends JpaRepository<CommissionRedPack, Integer> {
    @Query(value = "SELECT * FROM tbl_commissionredpack WHERE  to_days(create_time) = to_days(now()) and money = :money and openid = :openid", nativeQuery = true)
    CommissionRedPack selectRedpackDoesItExist(@Param("openid") String openid, @Param("money") String money);
}
