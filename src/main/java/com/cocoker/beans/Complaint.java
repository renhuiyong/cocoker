package com.cocoker.beans;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019-09-05 15:51
 * @Version: 1.0
 */
@Data
@Entity
@Table(name = "tbl_complaint")
@Accessors(chain = true)
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String wxNickname;

    private String wxOpenid;

    private BigDecimal balance;

    private Date createTime;

}
