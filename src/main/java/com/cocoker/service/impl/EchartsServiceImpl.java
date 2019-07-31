package com.cocoker.service.impl;

import com.cocoker.beans.Echarts;
import com.cocoker.dao.EchartsDao;
import com.cocoker.service.EchartsService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2018/12/26 9:12 PM
 * @Version: 1.0
 */
@Service
public class EchartsServiceImpl implements EchartsService{
    @Autowired
    private EchartsDao echartsDao;

    @Override
    public void add(Echarts e) {
        echartsDao.save(e);
    }

    @Override
    public List<Echarts> get100() {
        List<Echarts> list = echartsDao.find100();
        return list;
    }

    @Override
    public String getCurrentData() {
        return echartsDao.getCurrentData();
    }

    @Override
    public Echarts find1() {
        return echartsDao.find1();
    }

    @Override
    public Echarts saveEcharts(Echarts echarts) {
        return echartsDao.save(echarts);
    }


    @Override
    public Echarts findByCreateTime(String time) {
        return echartsDao.findByCreateTime(time);
    }

    @Override
    @Transactional
    public Integer updPriceByTime(String price, String createTime) {
        return echartsDao.updPriceByTime(price, createTime);
    }

    @Override
    @Transactional
    public Integer cleanEcharts() {
        return echartsDao.cleanTable();
    }

	@Override
	@Transactional
	public Integer updPriceAndLockByTime(String price, String createTime) {
		 return echartsDao.updateAndLock(price,1, createTime);
	}
}
