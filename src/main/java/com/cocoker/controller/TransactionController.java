package com.cocoker.controller;

import com.cocoker.VO.ExchangeVO;
import com.cocoker.VO.PayReturnParamVO;
import com.cocoker.VO.ResultVO;
import com.cocoker.beans.*;
import com.cocoker.config.ProjectUrl;
import com.cocoker.config.WechatAccountConfig;
import com.cocoker.converter.ExchangeResult2Bean;
import com.cocoker.converter.Notify2Bean;
import com.cocoker.converter.PayResult2Bean;
import com.cocoker.enums.ResultEnum;
import com.cocoker.exception.CocokerException;
import com.cocoker.service.*;
import com.cocoker.service.impl.PayServiceImpl;
import com.cocoker.utils.*;
import com.lly835.bestpay.config.AlipayConfig;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.rest.type.Get;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.catalina.connector.OutputBuffer;
import org.apache.catalina.connector.Response;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/1/3 3:45 PM
 * @Version: 1.0
 */
@RequestMapping("/transaction")
@Controller
@Slf4j
public class TransactionController {
    @Autowired
    private PayService payService;

    @Autowired
    private TipService tipService;

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private ProjectUrl projectUrl;

    @Autowired
    private WechatAccountConfig accountConfig;

    @Autowired
    private WebSocket webSocket;


    private static RestTemplate restTemplate = new RestTemplate();
    private static final DecimalFormat df = new DecimalFormat("#.00");
    public static final Double RATE = 6.9;

    /**
     * 充值
     */
    @Autowired
    private RechargeService rechargeService;

    @RequestMapping("/recharge")
    @ResponseBody
    public ModelAndView recharge(@RequestParam("openid") String openid, @RequestParam("remake") String rm, @RequestParam("money") String moeny, @RequestParam("type") String type, Map<String, Object> map) {
        checkMoney(openid, moeny);
        if (type.equals("wx1") || type.equals("ali1")) {
            Map<String, Object> result = payService.create(openid, moeny, type);
            map = result;
//        map.put("payUrl", result.getPayUrl());
            return new ModelAndView("pay/create2", map);
//            return result;
        }
        return null;
    }

    @GetMapping("/recharge2")
    @ResponseBody
    public ModelAndView recharge2(@RequestParam("openid") String openid, @RequestParam("money") String moeny, @RequestParam("type") String type, Map<String, Object> map) {
        checkMoney(openid, moeny);
        if (type.equals("wx1")) {
        } else if (type.equals("wx2")) {
            PayReturnParamVO result = payService.create2(openid, moeny);
            map.put("payUrl", result.getResult().getPay_url());
            return new ModelAndView("pay/create", map);
        }
        return null;

    }

    @GetMapping("/recharge3")
    @ResponseBody
    public ModelAndView recharge3(@RequestParam("openid") String openid, @RequestParam("money") String moeny, @RequestParam("type") String type, Map<String, Object> map) {
        checkMoney(openid, moeny);
        if (type.equals("ali1")) {
            PayReturnParamVO result = payService.create2(openid, moeny);
            map.put("payUrl", result.getResult().getPay_url());
            return new ModelAndView("pay/create", map);
        }
        return null;

    }

    private void checkMoney(String openid, String money) {
//        boolean flag = false;
        Tip allRechargeMoeny = tipService.findAllRechargeMoeny();
        boolean result = Arrays.asList(allRechargeMoeny.getTipMsg().split("-")).contains(money);
//        String str = "20-30-50-100-200-300-400-500-600-800-1000-2000-3000";
//        String[] split = str.split("-");
//        for (int i = 0; i < split.length; i++) {
//            if (split[i].equals(money)) {
//                flag = true;
//                break;
//            }
//        }
        if (!result) {
            log.error("[充值] 充值金额异常, openid={},moeny={},正常金额={}", openid, money, allRechargeMoeny.getTipMsg());
            throw new CocokerException(ResultEnum.RECHARGE_MONEY_ERROR);
        }
    }

    /**
     * 充值异步通知
     *
     * @return
     */
    @RequestMapping(value = "/notifyurl")
    public synchronized ModelAndView notifyurl(PayNotifyBean bean, HttpServletResponse response) {

        Map<String, Object> s = new HashMap<>();
        s.put("appid", PayServiceImpl.APP_ID);
        s.put("order_no", bean.getOrder_no());
        s.put("order_id", bean.getOrder_id());
        s.put("amount", bean.getAmount());
        s.put("time", bean.getTime());

        String sign = MD5.md5(ASCIISort.sort(s) + "&key=" + accountConfig.getCustomerKey1(), "").toUpperCase();
        if (bean.getAppid().equals(PayServiceImpl.APP_ID) && bean.getSign().equals(sign)) {

            Recharge recharge = rechargeService.findRechargeByOrderId(bean.getOrder_no());

            if (recharge == null) {
                log.error("[异步通知] 订单查询不到, bean {}", bean);
                throw new CocokerException(ResultEnum.CREATE_ORDER_ERROR);
            }
            if (recharge.getTstatus() == 0) {
                UserInfo userInfo = userInfoService.findByOpenId(recharge.getTopenid());
                if (!MathUtil.equals(Double.valueOf(df.format(Double.valueOf(recharge.getTmoney().toString()))), Double.valueOf(df.format(Double.valueOf(bean.getAmount()) / RATE)))) {
                    log.error("[异步通知] 金额异常, recharge {} bean {}", recharge, bean);
                    throw new CocokerException(ResultEnum.CREATE_ORDER_ERROR);
                }
                userInfo.setYUsermoney(userInfo.getYUsermoney().add(new BigDecimal(df.format(Double.valueOf(bean.getAmount()) / RATE))));
                UserInfo save = userInfoService.save(userInfo);
                recharge.setTstatus(1);
                recharge.setTyue(userInfo.getYUsermoney());
                recharge.setTsdpayno(bean.getOrder_id());
                recharge.setTsdorderno(bean.getOrder_no());
                recharge.setTnickname(userInfo.getYNickname());
                Recharge result = rechargeService.save(recharge);
                if (result == null) {
                    log.error("[异步通知] 更新订单信息失败, recharge {}", recharge);
                    throw new CocokerException(ResultEnum.CREATE_ORDER_ERROR);
                }
                if (save == null) {
                    log.error("[异步通知] 更新余额失败, userInfo {}", userInfo);
                    throw new CocokerException(ResultEnum.CREATE_ORDER_ERROR);
                }
                //发送websocket消息
                webSocket.sendMessage("有新的充值！金额:" + new BigDecimal(df.format(Double.valueOf(bean.getAmount()))));
                PrintWriter out = null;
                try {
                    out = response.getWriter();
                    out.write("success");
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("[异步通知] 通知商户回调失败, e {}", e);
                    throw new CocokerException(ResultEnum.NOTICE_MERCHANT_ERROR);
                }
            }
        } else {
            //签名异常
            return new ModelAndView("redirect:https://im.qq.com");
        }

        return new ModelAndView("redirect:".concat(tipService.getReturnUrl()).concat("/cocoker/coc"));
    }

    /**
     * 充值回调
     *
     * @return
     */
    @GetMapping("/returnurl")
    public ModelAndView returnurl() {
//        return new ModelAndView("redirect:http://rhyme.nat300.top/cocoker/coc");
        return new ModelAndView("redirect:".concat(tipService.getReturnUrl()).concat("/cocoker/coc"));

    }

    /**
     * 充值异步通知
     *
     * @return
     */
    @PostMapping(value = "/notifyurl2")
    public synchronized void notifyurl2(PayNotifyBean2 bean, HttpServletResponse response, HttpServletRequest request) {
//        String decode = URLDecoder.decode(str, "UTF-8");
//        PayNotifyBean bean = Notify2Bean.convert(decode.substring(0, decode.lastIndexOf("=")));
        Recharge recharge = rechargeService.findRechargeByOrderId(bean.getMerchant_order_num());
        if (recharge == null) {
            log.error("[异步通知] 订单查询不到, bean {}", bean);
            throw new CocokerException(ResultEnum.CREATE_ORDER_ERROR);
        }
        if (recharge.getTstatus() == 0) {
            UserInfo userInfo = userInfoService.findByOpenId(recharge.getTopenid());
//            if (!MathUtil.equals(recharge.getTmoney().doubleValue() , Double.valueOf(bean.getAmount()))) {
            if (Double.doubleToLongBits(Double.valueOf(recharge.getTmoney().toString()) * 6.79) != Double.doubleToLongBits(Double.valueOf(bean.getAmount()))) {
//            if (Double.doubleToLongBits(Double.valueOf(recharge.getTmoney().toString()) * 6.79) != Double.doubleToLongBits(Double.valueOf(bean.getTotal_fee()))) {
                log.error("[异步通知] 金额异常, recharge {} bean {}", recharge, bean);
                throw new CocokerException(ResultEnum.CREATE_ORDER_ERROR);
            }
            userInfo.setYUsermoney(userInfo.getYUsermoney().add(new BigDecimal(Double.valueOf(bean.getAmount()) / 6.79)));
//            userInfo.setYUsermoney(userInfo.getYUsermoney().add(new BigDecimal((Double.valueOf(bean.getTotal_fee()) / 100) / 6.79)));
            UserInfo save = userInfoService.save(userInfo);
            recharge.setTstatus(1);
            recharge.setTyue(userInfo.getYUsermoney());
            recharge.setTsdpayno(bean.getOrder_num());
            recharge.setTsdorderno(bean.getMerchant_order_num());
            recharge.setTnickname(userInfo.getYNickname());
            Recharge result = rechargeService.save(recharge);
            if (result == null) {
                log.error("[异步通知] 更新订单信息失败, recharge {}", recharge);
                throw new CocokerException(ResultEnum.CREATE_ORDER_ERROR);
            }
            if (save == null) {
                log.error("[异步通知] 更新余额失败, userInfo {}", userInfo);
                throw new CocokerException(ResultEnum.CREATE_ORDER_ERROR);
            }
        }
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write("success");
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("[异步通知] 通知商户回调失败, e {}", e);
            throw new CocokerException(ResultEnum.NOTICE_MERCHANT_ERROR);
        }
//        SpringMVCUtil.render("text/html","success",new String []{"encoding:utf-8"});
//        return new ModelAndView("redirect:".concat(projectUrl.getReturnUrl()).concat("/cocoker/coc"));
    }

    /**
     * 充值回调
     *
     * @return
     */
    @GetMapping("/returnurl2")
    public ModelAndView returnurl2() {
//        return new ModelAndView("redirect:http://rhyme.nat300.top/cocoker/coc");
        return new ModelAndView("redirect:".concat(projectUrl.getReturnUrl()).concat("/cocoker/coc"));

    }


    @GetMapping("/toexchange")
    @Transactional
    @ResponseBody
    public synchronized ModelAndView toexchange(@RequestParam("openid") String openid, @RequestParam("u") String u, Map<String, Object> hmap) {
        String[] split = u.split("-----");
        String wxOpenid = split[1];
        String money = split[0];
        hmap.put("oldWxOpenid", wxOpenid);
        hmap.put("money", money);
        hmap.put("TXopenid", openid);
        return new ModelAndView("pay/toexchange", hmap);
    }

    @GetMapping("/exchangeAuthorize")
    public String authorize(@RequestParam("Verification") String Verification) {
//        String url = "http://rhyme.nat300.top/cocoker/transaction/exchange";
        String url = projectUrl.getOauth2buildAuthorizationUrl() + "/cocoker/transaction/exchange";
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_USERINFO, URLEncoder.encode(tipService.getReturnUrl() + "/cocoker/coc&Verification=" + Verification));
        log.info("[提现 微信网页授权] 获取code, redirectUrl = {}", redirectUrl);
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/exchange")
    @Transactional
    @ResponseBody
    public synchronized ModelAndView exchange(@RequestParam("code") String code, @RequestParam("state") String Verification, Map<String, Object> hmap) {

        // openid
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        WxMpUser wxMpUser = new WxMpUser();
        try {
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
        } catch (WxErrorException e) {
            throw new CocokerException(ResultEnum.WECHAT_MP_ERROR.getCode(), e.getError().getErrorMsg());
        }
        String curOpenid = wxMpOAuth2AccessToken.getOpenId();

        hmap.put("returnUrl", tipService.getReturnUrl() + "/cocoker/coc");
        String[] split = Verification.split("-----");
        String oldOpenid = split[0].substring(split[0].indexOf("=") + 1);
        String TxOpenid = split[2];
        String money = split[1];

        UserInfo user = userInfoService.findByOpenId(curOpenid);
        log.info("[用户提现] 用户当前信息user={}", user);

        //查订单
        List<Order> allOrder = orderService.findAllOrder(curOpenid);
        List<Commission> cList = commissionService.findAllDayCommissionsByCOpenidEqualsOOrderByCIdDesc(curOpenid);

        if (allOrder.size() != 0 || cList.size() != 0) {
            String s = exchangeService.findtoDay5(curOpenid);
            if (Integer.valueOf(s) > 10) {
                log.error("【提现】 一天只能提现10次,{}", curOpenid);
                hmap.put("msg", "一天只能提现10次!");
                return new ModelAndView("pay/error", hmap);
            }

            String sIsNull = exchangeService.find60sIsNull(curOpenid);
            if (!sIsNull.equals("0")) {
                log.error("【提现】 60s 只能提现成功一次,{}", curOpenid);
                hmap.put("msg", "60s只能提交一次!");
                return new ModelAndView("pay/error", hmap);
            }

            if (user == null) {
                log.error("【提现】 找不到用户 wxOpenid,{}", curOpenid);
                return new ModelAndView("pay/error", hmap);
            }

            if (user.getYUstatus() != 0 || user.getYUstatus() == 11) {
                log.error("【提现】 状态不对 user,{}", user);
                hmap.put("msg", "状态不对!");
                return new ModelAndView("pay/error", hmap);
            }

            if (!curOpenid.equals(oldOpenid)) {
                log.error("【提现异常】 提现openid与实际openid不一样,实际openid:{},提交openid:{}", curOpenid, oldOpenid);
                hmap.put("msg", "签名错误!");
                userInfoService.save(user.setYUstatus(4).setYNickname("异常用户" + wxMpUser.getSexDesc() + wxMpUser.getCountry()));
                return new ModelAndView("pay/error", hmap);
            }

            if (user.getYUsermoney().doubleValue() < Double.valueOf(money)) {
                log.error("【提现】 金额不足 user,{},{}", user, money);
                hmap.put("msg", "金额不足!");
                return new ModelAndView("pay/error", hmap);
            }
            //小于1大于2000禁用
            if (Double.valueOf(money) < 1 || Double.valueOf(money) >= 1000) {
                user.setYUstatus(10);
                userInfoService.save(user);
                log.error("【提现】 小于1大于1000禁用 ,openId={}, user: {},提现money,{}", curOpenid, user, money);
                return new ModelAndView("pay/error", hmap);
            }

            Exchange exchange = exchangeService.saveOne(curOpenid, money, TxOpenid);
            if (exchange == null) {
                log.error("【提现】 创建订单失败 exchange,{}", exchange);
                return new ModelAndView("pay/error", hmap);
            }

            String Url = projectUrl.getZeroMoney();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.toString());
            MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
            map.add("lailu", curOpenid);//来源【可选参数】
            map.add("mid", projectUrl.getZeroMoneyUID()); //商户uid

            Double d = Double.valueOf(df.format(Double.valueOf(Double.valueOf(money) * RATE * 0.95)));
            map.add("jine", d);    //金额
            map.add("openid", TxOpenid);  //openid
            Integer m = Integer.valueOf("5496") + exchange.getTId();
//        Integer m = 10000 + exchange.getTId();
            map.add("tixianid", m.toString());////本地的提现id【要求唯一】字符串类型的数字，最大长度11位数,这里判断订单是否重复,不能用时间戳，跟本地表的id绑定,(不按照要求后果自负)
            map.add("mkey", MD5.md5(projectUrl.getZeroMoneyUID() + d + exchange.getTExchangeOpenid(), projectUrl.getZeroMoneyKey())); //md5
//        map.add("mkey", MD5.md5(projectUrl.getZeroMoneyUID() + (Double.valueOf(money.toString())) + exchange.getTExchangeOpenid(), projectUrl.getZeroMoneyKey())); //md5
            map.add("lx", "999");

            String result = restTemplate.postForObject(Url, map, String.class);
            log.info("【用户提现】 提现返回结果 result,{}", result);
            ExchangeVO exchangeVO = ExchangeResult2Bean.convert(result);
            if (!exchangeVO.getO().equals("yes")) {
                log.info("【提现】 失败 提现信息 result,{}", exchangeVO);
                return new ModelAndView("pay/error", hmap);
            } else {
                //修改提现订单状态
                exchangeService.updOne(exchange.getTId().toString(), 1);

                IpUtil.getIp();
                //修改用户余额
                user.setYUsermoney(user.getYUsermoney().subtract(new BigDecimal(money)));
                UserInfo save = userInfoService.save(user);
                log.info("[用户提现] 用户当前信息user={}", user);
                if (save == null) {
                    log.error("【提现】 user 保存失败 user,{}", user);
                    return new ModelAndView("pay/error", hmap);
                }
            }
        } else {
            log.error("【提现】 没有订单 wxOpenid,{}", curOpenid);
            hmap.put("msg", "平台禁止套现,需交易或有佣金方可提现!");
            return new ModelAndView("pay/error", hmap);
        }
        return new ModelAndView("pay/success", hmap);
    }

}
