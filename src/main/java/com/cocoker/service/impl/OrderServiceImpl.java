package com.cocoker.service.impl;

import com.cocoker.VO.ResultVO;
import com.cocoker.beans.Echarts;
import com.cocoker.beans.Order;
import com.cocoker.beans.Temp;
import com.cocoker.beans.UserInfo;
import com.cocoker.dao.OrderDao;
import com.cocoker.enums.ResultEnum;
import com.cocoker.exception.CocokerException;
import com.cocoker.service.EchartsService;
import com.cocoker.service.OrderService;
import com.cocoker.service.TempService;
import com.cocoker.service.UserInfoService;
import com.cocoker.utils.RandomUtil;
import com.cocoker.utils.ResultVOUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.cocoker.utils.MathUtil.*;
import static com.cocoker.utils.DateUtil.*;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2018/12/29 7:25 PM
 * @Version: 1.0
 */
@Service
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private TempService tempService;
    @Autowired
    private EchartsService echartsService;

    @Override
    public List<Order> findAllOrder(String openid) {
        return orderDao.findByOpenid(openid);
    }

    private static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat yMd = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DecimalFormat df = new DecimalFormat("#.0000");



    private String drawLine(String price, String ofinal, Date d,double money,String index) throws ParseException{
        double s = subtract(ofinal,price);//终点到起点距离（终点在上方为正）
        double runTime = Math.floor(divide(Double.valueOf(abs(s)),0.0003));//能够执行几次
        double tmp = Double.valueOf(price);
        double vg = s>0?0.0005:-0.0005;//每次的增减量
        double[] pricesArray = new double[24];
        String[] timeArrayOrder = new String[8];//订单创建时间
        String[] timeArrayEcharts = new String[24];//曲线时间
        List<Date> dates = new ArrayList<>();
        int arrayIndex = 0;
        String date = yMd.format(new Date())+" ";
        if(runTime<3){
            for (int i = 1; i <= 3; i++) {
                tmp = i<3?(subtract(tmp,vg)):(add(tmp,vg));
                pricesArray[arrayIndex]=tmp;
                timeArrayOrder[arrayIndex]=date+getTime(d, 26+i-30);//订单创建时间要减30秒
                timeArrayEcharts[arrayIndex] = getTime(d, 26+i);
				dates.add(sdf.parse(timeArrayOrder[arrayIndex]));
                arrayIndex++;
            }
            //System.out.println("模式一：s="+s+"runtime="+runTime+",price="+price+",ofinal="+ofinal);
        }else if(runTime<4){
            for (int i = 1; i <= 3; i++) {
                if(i==1){
                    tmp = add(tmp,vg);
                }else {
                    if(i==2){
                        vg = divide(add(s,vg),3);//获取接下来的平均值
                    }
                    tmp = add(tmp,vg);
                }
                pricesArray[arrayIndex]=tmp;
                timeArrayOrder[arrayIndex]=date+getTime(d,26 + i-30);
                timeArrayEcharts[arrayIndex] = getTime(d, 26+i);
				dates.add(sdf.parse(timeArrayOrder[arrayIndex]));
                arrayIndex++;
            }
            //System.out.println("模式2：s="+s+"runtime="+runTime+",price="+price+",ofinal="+ofinal);
        }else {
        	//ofinal 需要重新计算值   要在ofinl到index之间
        	if(runTime>5){
        		if(Double.valueOf(ofinal)>Double.valueOf(index)){
            		ofinal = add(index, "0.0003")+"";
            	}else {
            		ofinal = subtract(index, "0.0003")+"";
    			}
        		s = subtract(ofinal,price);
        		//System.out.println("模式3：s="+s+"runtime="+runTime+",price="+price+",ofinal="+ofinal);
        	}
            vg = divide(s,4);
            for (int i = 1; i <= 3; i++) {
                tmp = add(tmp,vg);
                pricesArray[arrayIndex]=tmp;
                timeArrayOrder[arrayIndex]=date+getTime(d,26 + i-30);
                timeArrayEcharts[arrayIndex] = getTime(d, 26+i);
				dates.add(sdf.parse(timeArrayOrder[arrayIndex]));
                arrayIndex++;
            }
            //System.out.println("模式3：s="+s+"runtime="+runTime+",price="+price+",ofinal="+ofinal);
        }
        tmp = Double.valueOf(ofinal);
        pricesArray[arrayIndex]=tmp;
        timeArrayOrder[arrayIndex]=date+getTime(d,0);
        timeArrayEcharts[arrayIndex] = getTime(d, 30);
        arrayIndex++;
		dates.add(sdf.parse(date+getTime(d, 0)));
        Echarts end = echartsService.findByCreateTime(getTime(d, 35));
        int j=1;
        boolean idLow = false;//2个点以后是否  小于0.0009
		boolean find = false;
        for (int i = 1; i <= 4; i++) {
        	if(i==1){//对过度点过大做一个调整
        		if(vg<-0.0005){
        			vg = -0.0004;
        		}else if(vg>0.0005){
        			vg = 0.0004;
        		}
        	}
        	if(i==3){//过度2个点
				s = subtract( end.getPrice(),tmp+"");
				if(abs(s)>0.0015){//大于15重新找点
					//System.out.println("大于15重新找点");
					for(;j<=15;j++){
						Echarts echarts = echartsService.findByCreateTime(getTime(d, 35+j));
						vg = divide(abs(subtract(echarts.getPrice(),tmp+"")), (3+j));
						//System.out.println("vg1："+vg);
						if(vg>0.0005){//平均值大于4  再找
							continue;
						}else {//否则就取这个点
							s = subtract(echarts.getPrice(),tmp+"");
							vg = divide(s, (3+j));
							//System.out.println("vg2："+vg);
							find = true;
							break;
						}
					}
				}else if(abs(s)<0.0009){
					idLow = true;
				}
				if(!find){
					vg = divide(s,3);
				}
			}
        	if(idLow){
        		if(i==3){
        			vg = vg>0?0.0004:-0.0004; 
        		}else {
        			vg = divide(add(s, vg),2);
				}
        	}
        	tmp = add(tmp,vg);
            pricesArray[arrayIndex]=tmp;
            timeArrayOrder[arrayIndex]=date+getTime(d,i);
            timeArrayEcharts[arrayIndex] = getTime(d, 30+i);
            arrayIndex++;
			dates.add(sdf.parse(date+getTime(d, i)));
        }
        if(find){//需要扩充点  35以后的点（包括35）
        	int xb = 0;
        	for(;j>0;j--){
        		tmp = add(tmp,vg);
           	 	pricesArray[arrayIndex]=tmp;
           	 	timeArrayEcharts[arrayIndex] = getTime(d, 35+xb++);
           	 	arrayIndex++;
        	}
        }
        //查询所有该时间订单
        List<Order> list = orderDao.findInCreateTime(dates);
        double winMoney = 0,lostMoney = 0;
        for (Order o:list) {
            //判断如果按照现在的点去做   输还是赢
            for(int i=0;i<dates.size();i++){
                if(sdf.format(o.getCreateTime()).equals(sdf.format(dates.get(i)))){
                    if(Double.valueOf(o.getOindex())>pricesArray[i]){//修改值后的结果为   跌
                        if(o.getDirection().equals("看跌")){
                            lostMoney = add(lostMoney,o.getMoney().doubleValue());//老板赔钱
                        }else {
                            winMoney = add(winMoney,o.getMoney().doubleValue());//老板赚钱
                        }
                    }else {//修改值后的结果为   涨
                        if(o.getDirection().equals("看跌")){
                            winMoney = add(winMoney,o.getMoney().doubleValue());//老板赚钱
                        }else {
                            lostMoney = add(lostMoney,o.getMoney().doubleValue());//老板赔钱
                        }
                    }
                }
            }
        }
        if(winMoney+money>=lostMoney){//老板赚钱才做修改
            Echarts echarts = null;
            for(int i=0;i<pricesArray.length;i++){
            	if(pricesArray[i]!=0){//去掉未赋值的
            		echarts = echartsService.findByCreateTime(timeArrayEcharts[i].toString());
                    if(echarts.getIsLock()==0){
                        echartsService.updPriceByTime(df.format(pricesArray[i]), timeArrayEcharts[i].toString());
                        //订单信息
                        changeOrders(timeArrayEcharts[i].toString(), df.format(pricesArray[i]));
                    }
                    if(i==3&&echarts.getIsLock()==1){
                        ofinal = echarts.getPrice();
                    }
            	}
            }
            return ofinal;
        }else {
            return echartsService.findByCreateTime(format.format((d.getTime() + (30 * 1000))).toString()).getPrice();
        }
    }
    
    public void changeOrders(String time,String price){
    	List<Order> orders = orderDao.findByCreateTime(getDateTimeBedore30s2(time.toString()));
        for (Order od : orders) {
			if(od.getDirection().equals("看跌")){
				if(Double.valueOf(od.getOindex())>Double.valueOf(price)//现在是跌
						){
					od.setOfinal(price);
					orderDao.saveResult(price,"盈",od.getHandle(),od.getOid()+"");
					if(od.getResult().equals("亏")){
						od.setResult("盈");
						saveTemp(od);
					}
				}else if(Double.valueOf(od.getOindex())<Double.valueOf(price)//现在是涨
						){
					od.setOfinal(price);
					orderDao.saveResult(price,"亏",od.getHandle(),od.getOid()+"");
					if(od.getResult().equals("盈")){
						od.setResult("亏");
						delTemp(od);
					}
					
				}
			}else {
				if(Double.valueOf(od.getOindex())>Double.valueOf(price)//现在是跌
						){//之前的结果是盈  修改结果
					od.setOfinal(price);
					orderDao.saveResult(price,"亏",od.getHandle(),od.getOid()+"");
					if(od.getResult().equals("盈")){
						od.setResult("亏");
						delTemp(od);
					}
				}else if(Double.valueOf(od.getOindex())<Double.valueOf(price)//现在是涨
						){//之前的结果是亏  修改结果
					od.setOfinal(price);
					orderDao.saveResult(price,"盈",od.getHandle(),od.getOid()+"");
					if(od.getResult().equals("亏")){
						od.setResult("盈");
						saveTemp(od);
					}
					
				}
			}
		}
    }
    
    
    private ResultVO saveTemp(Order order){//盈
        Temp temp = new Temp();
        temp.setTMoney(order.getMoney().multiply(new BigDecimal(1.85)));
        temp.setTOpenid(order.getOpenid());
        temp.setCreateTime(order.getCreateTime());
        temp.setOrderid(order.getOid());
        Temp r = tempService.save(temp);
        if (r == null) {
            log.error("【用户订单修改 加余额】增加失败,temp: {}", temp);
            return ResultVOUtil.error(ResultEnum.UPDATE_ORDER_ERROR.getCode(), ResultEnum.UPDATE_ORDER_ERROR.getMsg());
        }
        return null;
    }
    private ResultVO delTemp(Order order){//亏
        //减掉余额
        List<Temp> temps = tempService.findByTOpenid(order.getOpenid());
        if (null == temps || temps.size() == 0) {
            return ResultVOUtil.error(ResultEnum.UPDATE_ORDER_ERROR.getCode(), ResultEnum.UPDATE_ORDER_ERROR.getMsg());
        }
        List<Integer> a = temps.stream().map(e -> e.getTId()).collect(Collectors.toList());
        int num = tempService.delTemp(temps.stream().map(e -> e.getTId()).collect(Collectors.toList()));
        if (num < 0) {
            log.error("[更新订单删除余额] 删除失败，temps：{} ", temps);
            return ResultVOUtil.error(ResultEnum.UPDATE_ORDER_ERROR.getCode(), ResultEnum.UPDATE_ORDER_ERROR.getMsg());
        }
        return null;
    }
    @Override
    @Transactional
    public int addOrder(String flag, String index, String money, String openid, String currentDate) {
        UserInfo userInfo = userInfoService.findByOpenId(openid);
        if (userInfo == null) {
            log.error("[创建订单] openid用户未找到，openid = {}", openid);
            throw new CocokerException(ResultEnum.CREATE_ORDER_ERROR);
        }
        Order o = new Order();
        String t1 = RandomUtil.getZD(Integer.valueOf(money));//获取盈亏
        Date d = null;
        //这里会有一个异常，所以要用try catch捕获异常
        try {
            d = sdf.parse(yMd.format(new Date()) + " " + currentDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (flag.equals("inc")) {
            o.setDirection("看涨");
        } else {
            o.setDirection("看跌");
        }
        o.setOindex(index);
        o.setMoney(new BigDecimal(money));
        o.setHandle("0");
        o.setCreateTime(d);
        o.setResult(t1);
        o.setOpic(userInfo.getYUpic());
        String ofinal = "";
        //判断当前的点是否已经满足boss赢得需求  满足的话就不做修改
        Echarts echarts30 = echartsService.findByCreateTime(getTime(d, 30));//30秒结算点
        boolean isMZ = false;//是否要修改点  true 不改
        //线往上
        int rOfinal = RandomUtil.getRandomBet(8, 15);
        if ((flag.equals("dec") && t1.equals("亏") || (flag.equals("inc") && t1.equals("盈")))) {//涨
        	if(Double.valueOf(index)<Double.valueOf(echarts30.getPrice())){//结果本来就为涨
        		 isMZ = true;
        		 ofinal = echarts30.getPrice();
        		 //System.out.println("不改点");
        	}else {
                ofinal = String.valueOf(add(index, rOfinal>=10?("0.00"+rOfinal):("0.000"+rOfinal)));
                //System.out.println("改点");
			}
        } else { //跌
        	if(Double.valueOf(index)>Double.valueOf(echarts30.getPrice())){//结果本来就为跌
        		ofinal = echarts30.getPrice();
       		 	isMZ = true;
       		 	//System.out.println("不改点");
        	}else {
        		ofinal = String.valueOf(subtract(index, rOfinal>=10?("0.00"+rOfinal):("0.000"+rOfinal)));
        		//System.out.println("改点");
			}
        }
        if(!isMZ){
       	 try {
            	Echarts echarts = echartsService.findByCreateTime(getTime(d, 26));//从26秒开始
    			//System.out.println("26-30点间距："+subtract(ofinal, echarts.getPrice()));
    			if(abs(subtract(ofinal, echarts.getPrice()))>0.005){
    				ofinal = echarts30.getPrice();
    				o.setResult("盈");
    			}else {
    				ofinal = drawLine(echarts.getPrice(), ofinal, d,o.getMoney().doubleValue(),index);
				}
    		} catch (ParseException e) {
    			e.printStackTrace();
    		}
       }
        o.setOfinal(ofinal);
        o.setOpenid(openid);
        o.setOnickname(userInfo.getYNickname());
        Integer save = orderDao.saveByAll(o.getOpenid(), o.getDirection(), o.getOindex(), df.format(Double.valueOf(o.getOfinal())), o.getMoney(), o.getResult(), sdf.format(o.getCreateTime()).toString(), o.getHandle(), o.getOnickname(), o.getOpic());
        Order lastOrder = findLastOrderByOpenidAndCreateDate(openid);
        //判断下
        //if ((flag.equals("inc") && t1.equals("盈")) || (flag.equals("dec") && t1.equals("盈"))||!isMZ) {
        if (lastOrder.getResult().equals("盈")/*||!isMZ*/) {
            //加余额
            Temp temp = new Temp();
            temp.setTMoney(new BigDecimal(Integer.valueOf(money) * 1.85));
            temp.setTOpenid(openid);
            temp.setCreateTime(new Date());
            temp.setOrderid(lastOrder.getOid());
            Temp r = tempService.save(temp);
            if (r == null) {
                log.error("【加余额】增加失败,temp: {}", temp);
                throw new CocokerException(ResultEnum.CREATE_ORDER_ERROR);
            }
        }
        
        if (save == null) {
            log.error("[创建订单] 创建订单失败，order = {}", o);
            throw new CocokerException(ResultEnum.CREATE_ORDER_ERROR);
        }else {
			return lastOrder.getOid();
		}
    }

    @Override
    public List<Order> getByResult(String str) {
        return orderDao.getByResult(str);
    }

    @Override
    public Page<Order> findListPage(Pageable pageable) {
        return orderDao.findAll(new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path<String> dataTypePath = root.get("handle");
                /**
                 * 连接查询条件, 不定参数，可以连接0..N个查询条件
                 */
                query.where(cb.notEqual(dataTypePath, "2")); //这里可以设置任意条查询条件
                return null;
            }
        }, pageable);
    }

    @Override
    @Transactional
    public Integer saveByAll(String openid, String direction, String oindex, String ofinal, BigDecimal money, String result, String createTime, String handle, String onickname, String opic) {
        return orderDao.saveByAll(openid, direction, oindex, ofinal, money, result, createTime, handle, onickname, opic);
    }

    @Override
    public Order findLastOrderByOpenid(String openid) {
        return orderDao.findLastOrderByOpenid(openid);
    }

    @Override
    public Order findByOid(Integer oid) {
        return orderDao.findByOid(oid);
    }

    @Override
    public List<Order> findOrder20() {
        return orderDao.findByLatest20();
    }

    @Override
    @Transactional
    public Integer saveResult(String ofinal, String result, String handle, String oid) {
        return orderDao.saveResult(ofinal, result, handle, oid);
    }
	@Override
	public List<Order> findOrderByTimes() {
		List<Date>dates = new ArrayList<>();
		try {
			dates.add(sdf.parse("2019-07-06 08:22:12"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List<Order> orders=orderDao.findInCreateTime(dates);
		return orders;
	}

	@Override
	public Order findLastOrderByOpenidAndCreateDate(String opid) {
		return orderDao.findLastOrderByOpenidAndCreateDate(opid);
	}

}

