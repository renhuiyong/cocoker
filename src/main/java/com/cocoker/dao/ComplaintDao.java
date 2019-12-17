package com.cocoker.dao;

import com.cocoker.beans.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019-09-05 15:53
 * @Version: 1.0
 */
public interface ComplaintDao extends JpaRepository<Complaint, Integer> {

}
