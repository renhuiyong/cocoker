package com.cocoker.dao;

import com.alibaba.fastjson.JSONObject;
import com.cocoker.beans.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/1/8 2:01 PM
 * @Version: 1.0
 */
public interface CommissionDao extends JpaRepository<Commission, Integer> {

    @Query(value = "SELECT SUM(c_money) AS c_money FROM tbl_commission WHERE c_openid = :openid", nativeQuery = true)
    String findByOpenid(@Param("openid") String openid);

    @Query(value = "SELECT SUM(c_money) AS money FROM tbl_commission WHERE c_openid = :openid AND to_days(create_time) = to_days(now());", nativeQuery = true)
    String findByCOpenidAndTime(@Param("openid") String openid);

    @Query(value = "SELECT  t1.c_openid,SUM(t1.c_money)AS money,t2.y_nickname,t2.y_upic FROM tbl_commission  t1 LEFT JOIN tbl_userinfo  t2  ON t1.c_openid = t2.y_openid WHERE to_days(t1.create_time) = to_days(now()) GROUP BY t1.c_openid,t2.y_nickname,t2.y_upic    ORDER BY money  DESC  LIMIT 50", nativeQuery = true)
    List<JSONObject> findCommissionTop50();

    @Query(value = "SELECT * FROM tbl_commission WHERE  to_days(create_time) = to_days(now()) and c_openid = :openid ORDER BY c_id DESC ", nativeQuery = true)
    List<Commission> findToDayCommissionsByCOpenidEqualsOOrderByCIdDesc(@Param("openid") String openid);

    @Query(value = "SELECT * FROM tbl_commission WHERE  c_openid = :openid ORDER BY c_id DESC ", nativeQuery = true)
    List<Commission> findAllDayCommissionsByCOpenidEqualsOOrderByCIdDesc(@Param("openid") String openid);


}
