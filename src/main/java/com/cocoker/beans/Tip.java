package com.cocoker.beans;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/5/5 8:52 AM
 * @Version: 1.0
 */
@Entity
@Data
@DynamicUpdate
@Table(name = "tbl_tip")
public class Tip implements Serializable{

    private static final long serialVersionUID = -895394583012217251L;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer tipId;

    private String tipMsg;

    private Date createTime;

    private Date updateTime;
}
