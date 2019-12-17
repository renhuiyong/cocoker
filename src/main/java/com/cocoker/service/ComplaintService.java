package com.cocoker.service;

import com.cocoker.beans.Complaint;

import java.util.List;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019-09-05 15:54
 * @Version: 1.0
 */
public interface ComplaintService {

    List<Complaint> findAllComplaint();

    Complaint addComplaint(Complaint complaint);


    void deleteAllComplaint();

}
