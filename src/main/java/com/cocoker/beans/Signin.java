package com.cocoker.beans;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019-08-31 21:17
 * @Version: 1.0
 */
@Data
@Entity
@Table(name = "tbl_signin")
public class Signin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String openid;

    private BigDecimal money;

    private Date createTime;
}
