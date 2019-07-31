package com.cocoker.controller;

import com.alibaba.fastjson.JSONObject;
import com.cocoker.beans.*;
import com.cocoker.config.ProjectUrl;
import com.cocoker.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2018/12/23 12:08 PM
 * @Version: 1.0
 */
@Controller
public class HelloController {

    @Autowired
    private TipService tipService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private ProjectUrl projectUrl;


    @RequestMapping("/coc")
    public ModelAndView hello(HttpServletRequest request, Map<String, Object> map, @RequestParam(value = "openid", required = false) String openid) {

        String contextPath = request.getContextPath();
        map.put("returnUrl", projectUrl.getReturnUrl());

        List<Order> result1 = orderService.getByResult("盈");
        List<Order> result2 = orderService.findOrder20();
//        List<JSONObject> result3 = commissionService.findCommissionTop50();
//        for (int i = IMAGE_LOCATIONS.length - 1; i >= 0; i--) {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("y_upic", imagelocations[i][0]);
//            jsonObject.put("y_nickname", imagelocations[i][1]);
//            jsonObject.put("c_openid", "openid");
//            result3.add(0, jsonObject);
//        }

        //历史提现总金额
        String allExchangeMoneySum = exchangeService.findAllExchangeMoneySum(openid);

//        if(result2.size() == 5){
//            result2.remove(4);
//            result2.remove(3);
//        }
//        result1.addAll(result2);
//        if(result1 != null && result1.size()>0){
//            Collections.sort(result1);
//        }
        map.put("ods1", result1);
        map.put("ods2", result2);
//        map.put("ods3", result3);
        map.put("exchargeMoneySum", allExchangeMoneySum == null ? "0" : allExchangeMoneySum);
        map.put("cOpenid", openid);

        UserInfo user = userInfoService.findByOpenId(openid);
        Proxy proxy = new Proxy();
        map.put("user", user);

        //-----start
        //one
        if (null != user) {
            List<UserInfo> one = userInfoService.findByOid(user.getYOpenid());
            if (null != one && one.size() > 0) {
                proxy.setOne(one.size());
                //tow
                List<UserInfo> tow = userInfoService.findByOidIn(one.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
                if (null != tow && tow.size() > 0) {
                    proxy.setTwo(tow.size());
                    //three
                    List<UserInfo> three = userInfoService.findByOidIn(tow.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
                    if (null != three && three.size() > 0) {
                        proxy.setThree(three.size());
                        //four
                        List<UserInfo> four = userInfoService.findByOidIn(three.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
                        if (null != four && four.size() > 0) {
                            proxy.setFour(four.size());
                            //five
                            List<UserInfo> five = userInfoService.findByOidIn(four.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
                            if (null != five && five.size() > 0) {
                                proxy.setFive(five.size());
                                //six
                                List<UserInfo> six = userInfoService.findByOidIn(five.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
                                if (null != six && six.size() > 0) {
                                    proxy.setSix(six.size());
                                    //seven
                                    List<UserInfo> seven = userInfoService.findByOidIn(six.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
                                    if (null != seven && seven.size() > 0) {
                                        proxy.setSeven(seven.size());
                                        //eight
//                                    List<UserInfo> eight = userInfoService.findByOidIn(seven.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//                                    if (null != eight && eight.size() > 0) {
//                                        proxy.setEight(eight.size());
//                                        //nine
//                                        List<UserInfo> nine = userInfoService.findByOidIn(eight.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//                                        if (null != nine && nine.size() > 0) {
//                                            proxy.setNine(nine.size());
//                                            //ten
//                                            List<UserInfo> ten = userInfoService.findByOidIn(nine.stream().map(e -> e.getYOpenid()).collect(Collectors.toList()));
//                                            if (null != ten && ten.size() > 0) {
//                                                proxy.setTen(ten.size());
//                                            }
//                                        }
//                                    }
                                    }
                                }
                            }
                        }
                    }

                }

            }
        }
        map.put("proxy", proxy);

        return new ModelAndView("index", map);
    }

    @RequestMapping("/tococ")
    public ModelAndView tococ(HttpServletRequest request, Map<String, Object> map, @RequestParam(value = "openid", required = false) String openid) {
        return new ModelAndView("aqjc", map);
    }


//    @GetMapping("/btjq")
//    public ModelAndView btjq() {
//        return new ModelAndView("btjq");
//    }

    @ResponseBody
    @GetMapping("/num")
    public Integer getLineNum() {
        Random random = new Random();
        return random.nextInt(300) + 1000;
    }


    @ResponseBody
    @GetMapping("/tip")
    public Tip getTip() {
        return tipService.findFirst();
    }


    @ResponseBody
    @GetMapping("/getTurnover")
    public Integer getTurnover() {
        return tipService.getTurnover();
    }


    @GetMapping("/dispatch")
    public String dispatch() {

        return "";
    }

}
