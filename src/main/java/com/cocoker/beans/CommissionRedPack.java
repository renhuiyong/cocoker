package com.cocoker.beans;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019-09-23 12:13
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "tbl_commissionredpack")
public class CommissionRedPack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String openid;

    private BigDecimal money;

    private Date createTime;
}
