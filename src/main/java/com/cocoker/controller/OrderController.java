package com.cocoker.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.cocoker.utils.DateUtil.*;

import com.cocoker.beans.*;
import com.cocoker.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.cocoker.VO.ResultVO;
import com.cocoker.dao.OrderDao;
import com.cocoker.dto.UserDetailDTO;
import com.cocoker.enums.ResultEnum;
import com.cocoker.exception.CocokerException;
import com.cocoker.utils.ResultVOUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2018/12/25 7:25 PM
 * @Version: 1.0
 */
@Controller
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private TempService tempService;
    @Autowired
    private CommissionService commissionService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ExchangeService exchangeService;
    @Autowired
    private EchartsService echartsService;

    @Autowired
    private TipService tipService;

    private String[][] imagelocations;

    public static final String[][] IMAGE_LOCATIONS = new String[][]{
//            {"http://img1.imgtn.bdimg.com/it/u=3682886824,1477893997&fm=26&gp=0.jpg", "a.黑帝（关注朋友圈）"},
//            {"http://img5.imgtn.bdimg.com/it/u=1541889946,1553776213&fm=26&gp=0.jpg", "啊洋暂退、群发勿带"},
//            {"http://img5.imgtn.bdimg.com/it/u=2908920732,1386994533&fm=26&gp=0.jpg", "啊一.已开双封"},
//            {"http://img4.imgtn.bdimg.com/it/u=2231231470,4098557134&fm=26&gp=0.jpg", "Az.安子【趣步招募队友】"},
//            {"http://img1.imgtn.bdimg.com/it/u=2119793040,3394872197&fm=26&gp=0.jpg", "别离(招募推广代理)"},
//            {"http://img3.imgtn.bdimg.com/it/u=1068797574,4125604363&fm=26&gp=0.jpg", "废钢小姚15227272694"},
//            {"http://img0.imgtn.bdimg.com/it/u=1365686385,2181521853&fm=26&gp=0.jpg", "行动（双封）借钱勿扰"},
//            {"http://img5.imgtn.bdimg.com/it/u=421851750,2464727291&fm=26&gp=0.jpg", "萘鸟咨询师（不收钱）"},
//            {"http://img4.imgtn.bdimg.com/it/u=3759944720,3326020170&fm=26&gp=0.jpg", "没有未来。"},
//            {"http://img1.imgtn.bdimg.com/it/u=2695530757,400750380&fm=26&gp=0.jpg", "期待、与你的邂逅"},
//            {"http://img3.imgtn.bdimg.com/it/u=3966697508,1001216579&fm=26&gp=0.jpg", "晓琳：活动关注朋友圈"},
//            {"http://img3.imgtn.bdimg.com/it/u=421337878,3074376539&fm=26&gp=0.jpg", "记忆、凋零在花季"},
//            {"http://img3.imgtn.bdimg.com/it/u=1372403824,1066196387&fm=26&gp=0.jpg", "胡同里有只猫"},
//            {"http://img0.imgtn.bdimg.com/it/u=1202415939,3892212811&fm=26&gp=0.jpg", "炸金花微信不回复请看朋友圈"},
//            {"http://img3.imgtn.bdimg.com/it/u=2975309424,1332266689&fm=26&gp=0.jpg", "余生（换内排群）"},
//            {"http://img0.imgtn.bdimg.com/it/u=1941739257,2931686383&fm=26&gp=0.jpg", "小HAN广交好友"},
//            {"http://img2.imgtn.bdimg.com/it/u=3734214308,3602111578&fm=26&gp=0.jpg", "梦晓：活动关注朋友圈"},
//            {"http://img2.imgtn.bdimg.com/it/u=974898855,3876304923&fm=26&gp=0.jpg", "招代课养号中。小陌"},
//            {"http://img4.imgtn.bdimg.com/it/u=2112776449,2996688223&fm=26&gp=0.jpg", "猛虎（关注朋友圈）"},
//            {"http://img2.imgtn.bdimg.com/it/u=4092485548,2414335682&fm=26&gp=0.jpg", "小刘 微科技工作室"},
//            {"http://img5.imgtn.bdimg.com/it/u=2519605715,3665627260&fm=26&gp=0.jpg", "苍总趣投（步）收实力代理"},
    };

    public OrderController() {
        this.imagelocations = IMAGE_LOCATIONS;
    }

    @ResponseBody
    @RequestMapping("/order")
    public synchronized ResultVO order(@RequestParam("openid") String openid,
                                       @RequestParam("num") String num,
                                       @RequestParam("flag") String flag,
                                       @RequestParam("index") String index,
                                       @RequestParam("currentDate") String currentDate) {
        //TODO
        log.info("openid,{} 金额,{} 方向,{} 指数,{} ", openid, num, flag, index);
        UserInfo userInfo = new UserInfo();
        try {
            userInfo = userInfoService.save(openid, num, flag, index, currentDate);
        } catch (CocokerException e) {
            log.error("[创建订单] 订单错误 e={}", e.getMessage());
            return ResultVOUtil.error(-1, e.getMessage());
        }
        return ResultVOUtil.success(new UserDetailDTO(userInfo.getYOrderid(), userInfo.getYUsername(), userInfo.getYNickname(), userInfo.getYUpic(), userInfo.getYUsermoney(), "请别黑我，谢谢大哥们！"));
    }

    @ResponseBody
    @GetMapping("/order/info")
    @Transactional
    public ResultVO orderinfo(@RequestParam("openid") String openid,
                              @RequestParam("first") boolean first,
                              @RequestParam("currentMoney") String currentMoney,
                              @RequestParam("oid") Integer oid
    ) {
        List<Temp> temps = null;
        if (first) {
            temps = tempService.findByTOpenidAndTime(openid);
        } else {
            temps = tempService.findByTOpenidAndOrderid(openid, oid + "");
        }
        UserInfo userInfo = new UserInfo();
        if (null != temps || temps.size() != 0) {//设置余额
            UserInfo user = userInfoService.findByOpenId(openid);
            for (Temp t : temps) {
                user.setYUsermoney(user.getYUsermoney().add(t.getTMoney()));
            }
            int num = tempService.delTemp(temps.stream().map(e -> e.getTId()).collect(Collectors.toList()));
            if (num < 0) {
                log.error("[更新余额删除] 删除失败，temps：{} ", temps);
                return ResultVOUtil.error(-1, ResultEnum.UPD_USER_BLANCE_ERR.getMsg());
            }
            userInfo = userInfoService.save(user);
            if (userInfo == null) {
                log.error("[更新余额] 更新失败，user： {}", user);
                return ResultVOUtil.error(-1, ResultEnum.UPD_USER_BLANCE_ERR.getMsg());
            }
        }
        return ResultVOUtil.success(new UserDetailDTO(oid + "", userInfo.getYUsername(), userInfo.getYNickname(), userInfo.getYUpic(), userInfo.getYUsermoney(), "请不要攻击我，谢谢！"));

    }

    @GetMapping("/order/orderDetail")
    @ResponseBody
    public ResultVO orderDetail(@RequestParam("oid") Integer oid) {
        Order order = orderDao.findOrderByOid(oid);
        //Order order = orderService.findLastOrderByOpenid(openid);
        order.setHandle("别黑我,谢谢大佬");
        return ResultVOUtil.success(order);
    }


    @GetMapping("/history")
    public ModelAndView history(@RequestParam("openid") String openid, Map<String, Object> map) {
        List<Order> orders = orderService.findAllOrder(openid);
        map.put("orders", orders);
        return new ModelAndView("order", map);
    }

    @GetMapping("/entry")
    public ModelAndView entry() {
        return new ModelAndView("entry");
    }

    @GetMapping("/modelinfo")
    public ModelAndView modelinfo() {
        return new ModelAndView("modelinfo");
    }

    @GetMapping("/novicetips")
    public ModelAndView novicetips() {
        return new ModelAndView("novicetips");
    }

    @GetMapping("/profitmodel")
    public ModelAndView profitmodel() {
        return new ModelAndView("profitmodel");
    }

    @GetMapping("/recharge")
    public ModelAndView recharge(Map<String, Object> map) {
        Tip allRechargeMoeny = tipService.findAllRechargeMoeny();
        List<String> strings = Arrays.asList(allRechargeMoeny.getTipMsg().split("-"));

        map.put("rechargeMoeny", strings);
        return new ModelAndView("recharge", map);
    }

    @GetMapping("/rechargehistory")
    public ModelAndView rechargehistory(@RequestParam("openid") String openid, Map<String, Object> map) {
        List<Recharge> l = rechargeService.findByOpenid(openid);
        map.put("recharges", l);
        return new ModelAndView("rechargehistory", map);
    }

    @GetMapping("/exchargehistory")
    public ModelAndView exchargehistory(@RequestParam("openid") String openid, Map<String, Object> map) {
        List<Exchange> l = exchangeService.findByTopenid(openid);
        map.put("excharge", l);
        return new ModelAndView("exchargehistory", map);
    }

    @GetMapping("/exchange")
    public ModelAndView exchange() {
        return new ModelAndView("exchange");
    }

//    @GetMapping("/proxy")
//    public ModelAndView proxy(@RequestParam("openid") String openid, Map<String, Object> map) {
//        UserInfo user = userInfoService.findByOpenId(openid);
//        if (user == null) {
//            return new ModelAndView("proxy", map);
//        }
//        Proxy proxy = new Proxy();
//        map.put("user", user);
//        //one
//        List<UserInfo> one = userInfoService.findByOid(user.getYOpenid());
//        if (null != one && one.size() > 0) {
//            proxy.setOne(one.size());
//            //tow
//            List<UserInfo> tow = userInfoService.findByOidIn(one.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            if (null != tow && tow.size() > 0) {
//                proxy.setTwo(tow.size());
//                //three
//                List<UserInfo> three = userInfoService.findByOidIn(tow.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//                if (null != three && three.size() > 0) {
//                    proxy.setThree(three.size());
//                    //four
//                    List<UserInfo> four = userInfoService.findByOidIn(three.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//                    if (null != four && four.size() > 0) {
//                        proxy.setFour(four.size());
//                        //five
//                        List<UserInfo> five = userInfoService.findByOidIn(four.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//                        if (null != five && five.size() > 0) {
//                            proxy.setFive(five.size());
//                            //six
//                            List<UserInfo> six = userInfoService.findByOidIn(five.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//                            if (null != six && six.size() > 0) {
//                                proxy.setSix(six.size());
//                                //seven
//                                List<UserInfo> seven = userInfoService.findByOidIn(six.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//                                if (null != seven && seven.size() > 0) {
//                                    proxy.setSeven(seven.size());
//                                    //eight
////                                    List<UserInfo> eight = userInfoService.findByOidIn(seven.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
////                                    if (null != eight && eight.size() > 0) {
////                                        proxy.setEight(eight.size());
////                                        //nine
////                                        List<UserInfo> nine = userInfoService.findByOidIn(eight.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
////                                        if (null != nine && nine.size() > 0) {
////                                            proxy.setNine(nine.size());
////                                            //ten
////                                            List<UserInfo> ten = userInfoService.findByOidIn(nine.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
////                                            if (null != ten && ten.size() > 0) {
////                                                proxy.setTen(ten.size());
////                                            }
////                                        }
////                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//            }
//
//        }
//
//        List<Commission> list = commissionService.findCommissionsByCOpenidEqualsOOrderByCIdDesc(openid);
//        if (null != list && list.size() > 0) {
//            map.put("commissionsInfo", list);
//        }
//        map.put("proxy", proxy);
//        return new ModelAndView("proxy", map);
//    }

    @PostMapping("/proxyInfo")
    public ModelAndView proxyInfo(@RequestParam("openid") String openid,
                                  @RequestParam("leven") String leven,
                                  Map<String, Object> map) {

        UserInfo user = userInfoService.findByOpenId(openid);
        map.put("user", user);
        if (leven.equals("1")) {
            List<UserInfo> leven1 = userInfoService.findByOid(openid);
            map.put("leven", leven1);
            map.put("flag", "1");
        }
        if (leven.equals("2")) {
            List<UserInfo> leven1 = userInfoService.findByOid(openid);
            List<UserInfo> leven2 = userInfoService.findByOidIn(leven1.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            map.put("leven", leven2);
            map.put("flag", "2");
        }
        if (leven.equals("3")) {
            List<UserInfo> leven1 = userInfoService.findByOid(openid);
            List<UserInfo> leven2 = userInfoService.findByOidIn(leven1.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            List<UserInfo> leven3 = userInfoService.findByOidIn(leven2.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            map.put("leven", leven3);
            map.put("flag", "3");
        }
        if (leven.equals("4")) {
            List<UserInfo> leven1 = userInfoService.findByOid(openid);
            List<UserInfo> leven2 = userInfoService.findByOidIn(leven1.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            List<UserInfo> leven3 = userInfoService.findByOidIn(leven2.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            List<UserInfo> leven4 = userInfoService.findByOidIn(leven3.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            map.put("leven", leven4);
            map.put("flag", "4");
        }
        if (leven.equals("5")) {
            List<UserInfo> leven1 = userInfoService.findByOid(openid);
            List<UserInfo> leven2 = userInfoService.findByOidIn(leven1.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            List<UserInfo> leven3 = userInfoService.findByOidIn(leven2.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            List<UserInfo> leven4 = userInfoService.findByOidIn(leven3.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            List<UserInfo> leven5 = userInfoService.findByOidIn(leven4.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            map.put("leven", leven5);
            map.put("flag", "5");
        }
        if (leven.equals("6")) {
            List<UserInfo> leven1 = userInfoService.findByOid(openid);
            List<UserInfo> leven2 = userInfoService.findByOidIn(leven1.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            List<UserInfo> leven3 = userInfoService.findByOidIn(leven2.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            List<UserInfo> leven4 = userInfoService.findByOidIn(leven3.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            List<UserInfo> leven5 = userInfoService.findByOidIn(leven4.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            List<UserInfo> leven6 = userInfoService.findByOidIn(leven5.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            map.put("leven", leven6);
            map.put("flag", "6");
        }
        if (leven.equals("7")) {
            List<UserInfo> leven1 = userInfoService.findByOid(openid);
            List<UserInfo> leven2 = userInfoService.findByOidIn(leven1.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            List<UserInfo> leven3 = userInfoService.findByOidIn(leven2.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            List<UserInfo> leven4 = userInfoService.findByOidIn(leven3.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            List<UserInfo> leven5 = userInfoService.findByOidIn(leven4.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            List<UserInfo> leven6 = userInfoService.findByOidIn(leven5.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            List<UserInfo> leven7 = userInfoService.findByOidIn(leven6.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            map.put("leven", leven7);
            map.put("flag", "7");
        }
//        if (leven.equals("8")) {
//            List<UserInfo> leven1 = userInfoService.findByOid(openid);
//            List<UserInfo> leven2 = userInfoService.findByOidIn(leven1.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven3 = userInfoService.findByOidIn(leven2.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven4 = userInfoService.findByOidIn(leven3.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven5 = userInfoService.findByOidIn(leven4.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven6 = userInfoService.findByOidIn(leven5.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven7 = userInfoService.findByOidIn(leven6.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven8 = userInfoService.findByOidIn(leven7.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            map.put("leven", leven8);
//            map.put("flag", "8");
//        }
//        if (leven.equals("9")) {
//            List<UserInfo> leven1 = userInfoService.findByOid(openid);
//            List<UserInfo> leven2 = userInfoService.findByOidIn(leven1.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven3 = userInfoService.findByOidIn(leven2.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven4 = userInfoService.findByOidIn(leven3.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven5 = userInfoService.findByOidIn(leven4.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven6 = userInfoService.findByOidIn(leven5.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven7 = userInfoService.findByOidIn(leven6.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven8 = userInfoService.findByOidIn(leven7.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven9 = userInfoService.findByOidIn(leven8.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            map.put("leven", leven9);
//            map.put("flag", "9");
//        }
//        if (leven.equals("10")) {
//            List<UserInfo> leven1 = userInfoService.findByOid(openid);
//            List<UserInfo> leven2 = userInfoService.findByOidIn(leven1.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven3 = userInfoService.findByOidIn(leven2.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven4 = userInfoService.findByOidIn(leven3.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven5 = userInfoService.findByOidIn(leven4.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven6 = userInfoService.findByOidIn(leven5.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven7 = userInfoService.findByOidIn(leven6.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven8 = userInfoService.findByOidIn(leven7.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven9 = userInfoService.findByOidIn(leven8.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            List<UserInfo> leven10 = userInfoService.findByOidIn(leven9.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//            map.put("leven", leven10);
//            map.put("flag", "10");
//        }
        return new ModelAndView("zddetail", map);
//        return new ModelAndView("proxyinfo", map);
    }

    @GetMapping("/proxyCount")
    @ResponseBody
    public Map<String, Integer> proxyCount(@RequestParam("openid") String openid) {
        Map<String, Integer> map = new HashMap<>();
        UserInfo user = userInfoService.findByOpenId(openid);
        //-----start
        //one
        if (user == null) {
            map.put("proxyCount", 0);
            return map;
        }
        List<UserInfo> one = userInfoService.findByOid(user.getYOpenid());
        if (one != null && one.size() > 0) {
            map.put("proxyCount", one.size());
            //tow
            List<UserInfo> tow = userInfoService.findByOidIn(one.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
            if (null != tow && tow.size() > 0) {
                map.put("proxyCount", one.size() + tow.size());
                //three
                List<UserInfo> three = userInfoService.findByOidIn(tow.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
                if (null != three && three.size() > 0) {
                    map.put("proxyCount", one.size() + tow.size() + three.size());
                    //four
                    List<UserInfo> four = userInfoService.findByOidIn(three.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
                    if (null != four && four.size() > 0) {
                        map.put("proxyCount", one.size() + tow.size() + three.size() + four.size());
                        //five
                        List<UserInfo> five = userInfoService.findByOidIn(four.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
                        if (null != five && five.size() > 0) {
                            map.put("proxyCount", one.size() + tow.size() + three.size() + four.size() + five.size());
                            //six
                            List<UserInfo> six = userInfoService.findByOidIn(five.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
                            if (null != six && six.size() > 0) {
                                map.put("proxyCount", one.size() + tow.size() + three.size() + four.size() + five.size() + six.size());
                                //seven
                                List<UserInfo> seven = userInfoService.findByOidIn(six.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
                                if (null != seven && seven.size() > 0) {
                                    map.put("proxyCount", one.size() + tow.size() + three.size() + four.size() + five.size() + six.size() + seven.size());
                                    //eight
//                                    List<UserInfo> eight = userInfoService.findByOidIn(seven.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//                                    if (null != eight && eight.size() > 0) {
//                                        map.put("proxyCount", one.size() + tow.size() + three.size() + four.size() + five.size() + six.size() + seven.size() + eight.size());
//                                    }
//                                    //nine
//                                    List<UserInfo> nine = userInfoService.findByOidIn(eight.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//                                    if (null != nine && nine.size() > 0) {
//                                        map.put("proxyCount", one.size() + tow.size() + three.size() + four.size() + five.size() + six.size() + seven.size() + eight.size() + nine.size());
//                                    }
//                                    //ten
//                                    List<UserInfo> ten = userInfoService.findByOidIn(nine.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//                                    if (null != ten && ten.size() > 0) {
//                                        map.put("proxyCount", one.size() + tow.size() + three.size() + four.size() + five.size() + six.size() + seven.size() + eight.size() + nine.size() + ten.size());
//                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //-----end
//        map.put("proxyCount", one.size() + tow.size() + three.size() + four.size() + five.size() + six.size() + seven.size());
        return map;
    }

    @GetMapping("/historyCommission")
    @ResponseBody
    public Map<String, Object> historyCommission(@RequestParam("openid") String openid) {

        String commission = commissionService.findByCOpenid(openid);
        Map<String, Object> map = new HashMap<>();
        map.put("historyCommission", commission);
        return map;
    }

    @GetMapping("/historyCommissionByTime")
    @ResponseBody
    public Map<String, Object> historyCommissionByTime(@RequestParam("openid") String openid) {

        String commission = commissionService.findByCOpenidAndTime(openid);
        Map<String, Object> map = new HashMap<>();
        map.put("historyCommissionByTime", commission);
        return map;
    }

    @GetMapping("/ranking")
    public ModelAndView rightph(Map<String, Object> map) {
        List<JSONObject> result3 = commissionService.findCommissionTop50();
//        for (int i = IMAGE_LOCATIONS.length - 1; i >= 0; i--) {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("y_upic", imagelocations[i][0]);
//            jsonObject.put("y_nickname", imagelocations[i][1]);
//            jsonObject.put("c_openid", "openid");
//            result3.add(0, jsonObject);
//        }
//        map.put("no1", result3.get(0));
//        map.put("no2", result3.get(1));
//        map.put("no3", result3.get(2));
//        result3.remove(0);
//        result3.remove(0);
//        result3.remove(0);
        map.put("ods3", result3);
        return new ModelAndView("ranking", map);
    }


    @GetMapping("/oneCommission")
    public ModelAndView oneCommission(@RequestParam("openid") String openid, Map<String, Object> map) {
        List<Commission> list = commissionService.findCommissionsByCOpenidEqualsOOrderByCIdDesc(openid);
        if (null != list && list.size() > 0) {
            map.put("commissionsInfo", list);
        }
        return new ModelAndView("onecommission", map);
    }


    @GetMapping("/allCommission")
    public ModelAndView allCommission(@RequestParam("openid") String openid, Map<String, Object> map) {
        List<Commission> list = commissionService.findAllDayCommissionsByCOpenidEqualsOOrderByCIdDesc(openid);
        if (null != list && list.size() > 0) {
            map.put("commissionsInfo", list);
        }
        return new ModelAndView("allcommission", map);
    }

    //获取订单信息
    @GetMapping("/winOrder")
    @ResponseBody
    public List<Order> winOrder() {
        return orderService.getByResult("盈");
    }

    //获取订单信息
    @GetMapping("/newOrder")
    @ResponseBody
    public List<Order> newOrder() {
        return orderService.findOrder20();
    }

    @GetMapping("test")
    public String String() {

        return "redirect:https://www.baidu.com/";
    }

}
