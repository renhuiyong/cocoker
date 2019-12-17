package com.cocoker.dao;

import com.cocoker.beans.Tip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/5/5 8:57 AM
 * @Version: 1.0
 */
public interface TipDao extends JpaRepository<Tip, Integer> {

    @Query(value = "SELECT * FROM tbl_tip LIMIT 1", nativeQuery = true)
    Tip findFirst();

    /*
    *
    * @Modifying
        @Query(value = "UPDATE tbl_order SET ofinal = :ofinal, result = :result, handle= :handle WHERE oid = :oid",nativeQuery = true)
        Integer saveResult(@Param("ofinal")String ofinal,@Param("result")String result,@Param("handle")String handle,@Param("oid")String oid);

    *
    * */
    @Modifying
    @Transactional
    @Query(value = "UPDATE tbl_tip SET tip_msg = :msg WHERE tip_id = 1", nativeQuery = true)
    Integer updTip(@Param("msg") String msg);

    @Query(value = "SELECT tip_msg as Turnover FROM tbl_tip WHERE tip_id = 2", nativeQuery = true)
    Integer getTurnover();

    @Modifying
    @Transactional
    @Query(value = "UPDATE tbl_tip SET tip_msg = tip_msg + CONCAT(SUBSTRING(RAND(),3,1),0) WHERE tip_id = 2", nativeQuery = true)
    void setTurnover();


    @Query(value = "SELECT tip_msg FROM tbl_tip WHERE tip_id =3", nativeQuery = true)
    String getReturnUrl();

    @Modifying
    @Transactional
    @Query(value = "UPDATE tbl_tip SET tip_msg = :url  WHERE tip_id = 3", nativeQuery = true)
    Integer updReturnUrl(@Param("url") String url);


    //是否维护
    @Query(value = "SELECT tip_msg FROM tbl_tip WHERE tip_id =4", nativeQuery = true)
    String getIfMaintain();

    @Modifying
    @Transactional
    @Query(value = "UPDATE tbl_tip SET tip_msg = :b WHERE tip_id = 4", nativeQuery = true)
    void setIfMaintain(@Param("b") boolean b);


    Tip findByTipIdEquals(int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tbl_tip SET tip_msg = :str WHERE tip_id = 5", nativeQuery = true)
    int setRechargeMoney(@Param("str") String str);


    @Modifying
    @Transactional
    @Query(value = "UPDATE tbl_tip SET tip_msg = :str WHERE tip_id = 6", nativeQuery = true)
    int setOrderMoney(@Param("str") String str);


    //跳到其他地方
    @Query(value = "SELECT tip_msg FROM tbl_tip WHERE tip_id = 8", nativeQuery = true)
    String getOtherUrlOpenid();

}
