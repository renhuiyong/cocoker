package com.cocoker.service.impl;

import com.cocoker.beans.Complaint;
import com.cocoker.dao.ComplaintDao;
import com.cocoker.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019-09-05 15:54
 * @Version: 1.0
 */
@Service
public class ComplaintServiceImpl implements ComplaintService {

    @Autowired
    private ComplaintDao complaintDao;

    @Override
    public List<Complaint> findAllComplaint() {
        return complaintDao.findAll();
    }

    @Override
    public Complaint addComplaint(Complaint complaint) {
        return complaintDao.save(complaint);
    }

    @Override
    public void deleteAllComplaint() {
        complaintDao.deleteAll();
    }
}
