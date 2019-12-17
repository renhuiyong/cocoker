package com.cocoker.dao;

import com.cocoker.beans.Order;
import org.hibernate.annotations.SQLInsert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2018/12/29 7:23 PM
 * @Version: 1.0
 */
public interface OrderDao extends JpaRepository<Order, Integer> {
    @Query(value = "SELECT * from tbl_order where openid = :openid and create_time < CONCAT((SELECT DATE_SUB(NOW(), interval 30 SECOND))) ORDER BY create_time DESC", nativeQuery = true)
    List<Order> findByOpenid(@Param("openid") String openid);

    @Query(value = "SELECT * FROM tbl_order WHERE result = :str and create_time < CONCAT((SELECT DATE_SUB(NOW(), interval 30 SECOND))) ORDER BY oid desc LIMIT 25", nativeQuery = true)
    List<Order> getByResult(@Param("str") String str);


    @Query(value = "select * from tbl_order WHERE  create_time < CONCAT((SELECT DATE_SUB(NOW(), interval 30 SECOND)))  ORDER BY oid desc  LIMIT 20", nativeQuery = true)
    List<Order> findByLatest20();

    @Modifying
    @Query(value = "INSERT INTO tbl_order(direction,oindex,ofinal,money,result,create_time,handle,openid,onickname,opic) VALUES(:direction,:oindex,:ofinal,:money,:result,:createTime,:handle,:openid,:onickname,:opic)", nativeQuery = true)
    Integer saveByAll(@Param("openid") String openid,
                      @Param("direction") String direction,
                      @Param("oindex") String oindex,
                      @Param("ofinal") String ofinal,
                      @Param("money") BigDecimal money,
                      @Param("result") String result,
                      @Param("createTime") String createTime,
                      @Param("handle") String handle,
                      @Param("onickname") String onickname,
                      @Param("opic") String opic
    );

    @Query(value = "SELECT * from tbl_order where openid = :openid and create_time <= CONCAT((SELECT DATE_SUB(NOW(), interval 30 SECOND))) ORDER BY create_time DESC LIMIT 1", nativeQuery = true)
    Order findLastOrderByOpenid(@Param("openid") String openid);

    Order findByOid(Integer oid);

    @Modifying
    @Query(value = "UPDATE tbl_order SET ofinal = :ofinal, result = :result, handle= :handle WHERE oid = :oid", nativeQuery = true)
    Integer saveResult(@Param("ofinal") String ofinal, @Param("result") String result, @Param("handle") String handle, @Param("oid") String oid);

    Page<Order> findAll(Specification<Order> handle, Pageable pageable);

    @Query(value = "SELECT * from tbl_order where  create_time = :createTime", nativeQuery = true)
    List<Order> findByCreateTime(@Param("createTime") Date createTime);


    @Query(value = "SELECT * from tbl_order where  create_time in (:dates)", nativeQuery = true)
    List<Order> findInCreateTime(@Param("dates") List<Date> dates);

    @Query(value = "SELECT * from tbl_order where openid = :openid and  create_time in (:date)", nativeQuery = true)
    Order findLastOrderByOpenidAndCreateTime(@Param("openid") String openid, @Param("date") Date date);

    @Query(value = "SELECT * from tbl_order where oid = :oid", nativeQuery = true)
    Order findOrderByOid(@Param("oid") int oid);

    @Query(value = "SELECT * from tbl_order where openid = :openid ORDER BY oid DESC LIMIT 1", nativeQuery = true)
    Order findLastOrderByOpenidAndCreateDate(@Param("openid") String openid);

    @Query(value = "SELECT * FROM tbl_order WHERE result = '盈'  AND handle ='0' and TO_DAYS(create_time) = to_days(now())", nativeQuery = true)
    List<Order> findToBeRevised();


    /**
     * private String openid;

     private String direction;

     private String oindex;

     private String ofinal;

     private BigDecimal money;

     private String result;

     private Date createTime;

     private String handle;

     private String onickname;
     */

}
