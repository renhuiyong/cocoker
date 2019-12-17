package com.cocoker.dao;

import com.cocoker.beans.Exchange;
import com.cocoker.beans.Recharge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/1/11 4:17 PM
 * @Version: 1.0
 */
public interface ExchangeDao extends JpaRepository<Exchange, Integer> {

    @Query(value = "select * from tbl_exchange where t_status = '1' AND  t_openid = :openid ORDER by t_id desc", nativeQuery = true)
    List<Exchange> findByTopenid(@Param("openid") String openid);

    Page<Exchange> findByTStatus(Integer status, Pageable pageable);

    //历史提现
    @Query(value = "SELECT SUM(t_money) as money FROM tbl_exchange WHERE t_status = '1'  and t_openid = :openid", nativeQuery = true)
    String findAllExchangeMoneySum(@Param("openid") String openid);

    Exchange findByTId(Integer orderId);

    //今日提现
    @Query(value = "SELECT SUM(t_money) as money FROM tbl_exchange WHERE t_status = '1' AND TO_DAYS(create_time) =TO_DAYS(NOW())", nativeQuery = true)
    String findDayExchangeAllMoney();

    //一天是否提过5次
    @Query(value = "SELECT COUNT(*) as count FROM tbl_exchange WHERE t_openid = :openid  AND to_days(create_time) = to_days(now()) AND t_status='1'", nativeQuery = true)
    String findtoDay5(@Param("openid") String openid);

    //一分钟内是否提过
    @Query(value = "SELECT COUNT(*) as count FROM tbl_exchange WHERE t_openid = :openid  AND create_time BETWEEN CONCAT((SELECT DATE_SUB(NOW(), interval 60 SECOND)))  AND NOW()", nativeQuery = true)
    String find60sIsNull(@Param("openid") String openid);
}
