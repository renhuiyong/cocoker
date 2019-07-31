package com.cocoker;//package com.cocoker;
//
//import java.util.List;
//
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.cocoker.beans.Echarts;
//import com.cocoker.beans.Order;
//import com.cocoker.dao.EchartsDao;
//import com.cocoker.service.EchartsService;
//import com.cocoker.service.OrderService;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes={CocokerApplication.class})
//public class JunitTest {
//	@Autowired

//	private OrderService orderService;
//	@Autowired
//    private EchartsDao echartsDao;
//	@Autowired
//	private EchartsService echartsService;
//	@Test
//	public void testd(){
//		Echarts echartsTmp = new Echarts();
//		echartsTmp.setIsLock(1);
//		echartsTmp.setCreateTime("13:59:10");
//		echartsTmp.setPrice("5111");
//		echartsService.updPriceAndLockByTime("1","13:59:10");
//		//echartsService.saveEcharts(echartsTmp);
//		System.out.println("1");
//	}
//}
