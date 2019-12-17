package com.cocoker.service.impl;

import com.cocoker.beans.Commission;
import com.cocoker.beans.Echarts;
import com.cocoker.beans.UserInfo;
import com.cocoker.dao.UserInfoDao;
import com.cocoker.enums.ResultEnum;
import com.cocoker.enums.UserStatusEnum;
import com.cocoker.exception.CocokerException;
import com.cocoker.service.*;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.cocoker.utils.DateUtil.getTime;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2018/12/24 8:11 PM
 * @Version: 1.0
 */
@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private OrderService orderService;

    @Autowired
    private EchartsService echartsService;

    @Autowired
    private WebSocket webSocket;


    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private CommissionService commissionService;


    private static SimpleDateFormat yMd = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public UserInfo findOne(Integer id) {
        return userInfoDao.findById(id).get();
    }

    @Override
    @Transactional
    public UserInfo save(String openid, String num, String flag, String index, String currentDate) {
        if (openid == null || num == null || flag == null) {
            throw new CocokerException(ResultEnum.PARAM_ERROR);
        }

        UserInfo userInfo = userInfoDao.findByYOpenid(openid);
        if (userInfo == null) {
            throw new CocokerException(ResultEnum.USER_NOT_EXIST);
        }

        try {
            Date d1 = sdf.parse(yMd.format(new Date()) + " " + currentDate);
            Echarts e1 = echartsService.findByCreateTime(getTime(d1, 0));
            if (!e1.getPrice().equals(index) || e1 == null) {
                userInfo.setYUstatus(2);
                userInfo.setYNickname("非法用户" + index + "，" + e1.getPrice());
                userInfoService.save(userInfo);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (Double.valueOf(index) < 6 || Double.valueOf(index) > 7 || Integer.valueOf(num) < 5) {
            userInfo.setYUstatus(2);
            userInfoDao.save(userInfo);
            log.error("[< 提交订单信息异常----+++++++++++++++++++],index: {},money : {}", index, num);
            return null;
        }
        if (userInfo.getYUstatus() != UserStatusEnum.DEFAULT_USER_STATUS.getCode() && userInfo.getYUstatus() != 11) {
            throw new CocokerException(ResultEnum.USER_STATUS_ERROR);
        }
        if (userInfo.getYUsermoney().doubleValue() + 0.01 < Double.valueOf(num)) {
//        if (userInfo.getYUsermoney().doubleValue() + 0.01 < Double.valueOf(num) * 1.1) {
            throw new CocokerException(ResultEnum.MONEY_ERROR);
        }

        //下单
        int oid = orderService.addOrder(flag, index, num, openid, currentDate);
        log.info("[下单] 订单id:{}", oid);
//        System.out.println(oid);

        userInfo.setYUsermoney(userInfo.getYUsermoney().subtract(new BigDecimal(num).multiply(new BigDecimal(1))).setScale(2, BigDecimal.ROUND_HALF_UP));
//        userInfo.setYUsermoney(userInfo.getYUsermoney().subtract(new BigDecimal(num).multiply(new BigDecimal(1.1))).setScale(2, BigDecimal.ROUND_HALF_UP));
        UserInfo save = userInfoDao.save(userInfo);
        //发送websocket消息
        webSocket.sendMessage("有新的订单！金额:" + num);

        if (save == null) {
            throw new CocokerException(ResultEnum.MONEY_ERROR);
        }

        //给代理加余额
        //one
        UserInfo one = userInfoDao.findByYOpenid(userInfo.getYOid());

        if (one != null) {
            //5%,2%,1%,0.5%,0.5%,0.5%,0.5  --10
            //10%,4%,2%,1%,1%,1%,1  --20
            one.setYUsermoney(one.getYUsermoney().add(new BigDecimal((Double.valueOf(num) * 0.1) * 0.5)));
            userInfoDao.save(one);
            Commission c1 = new Commission();
            c1.setCMoney(new BigDecimal((Double.valueOf(num) * 0.1) * 0.5));
//            c1.setCMoney(new BigDecimal((Double.valueOf(num) * 0.13) * 0.3));
            c1.setCOpenid(one.getYOpenid());
            c1.setCreateTime(new Date());
            c1.setCOpenidM(userInfo.getYNickname());
            c1.setCLeven(1);
            commissionService.save(c1);

            //two
            UserInfo tow = userInfoDao.findByYOpenid(one.getYOid());
            if (tow != null) {
                //4/20
                tow.setYUsermoney(tow.getYUsermoney().add(new BigDecimal((Double.valueOf(num) * 0.1) * 0.2)));
                userInfoDao.save(tow);
                Commission c2 = new Commission();
                c2.setCMoney(new BigDecimal((Double.valueOf(num) * 0.1) * 0.2));
//                c2.setCMoney(new BigDecimal((Double.valueOf(num) * 0.2) * 0.15));
                c2.setCOpenid(tow.getYOpenid());
                c2.setCreateTime(new Date());
                c2.setCLeven(2);
                c2.setCOpenidM(userInfo.getYNickname());
                commissionService.save(c2);
                //three
                UserInfo three = userInfoDao.findByYOpenid(tow.getYOid());
                if (three != null) {
                    //2/20
                    three.setYUsermoney(three.getYUsermoney().add(new BigDecimal((Double.valueOf(num) * 0.1) * 0.1)));
                    userInfoDao.save(three);
                    Commission c3 = new Commission();
                    c3.setCMoney(new BigDecimal((Double.valueOf(num) * 0.1) * 0.1));
                    c3.setCOpenid(three.getYOpenid());
                    c3.setCreateTime(new Date());
                    c3.setCLeven(3);
                    c3.setCOpenidM(userInfo.getYNickname());
                    commissionService.save(c3);
                    //four
                    UserInfo four = userInfoDao.findByYOpenid(three.getYOid());
                    if (four != null) {
                        //1/20
                        four.setYUsermoney(four.getYUsermoney().add(new BigDecimal((Double.valueOf(num) * 0.1) * 0.05)));
                        userInfoDao.save(four);
                        Commission c4 = new Commission();
                        c4.setCMoney(new BigDecimal((Double.valueOf(num) * 0.1) * 0.05));
                        c4.setCOpenid(four.getYOpenid());
                        c4.setCreateTime(new Date());
                        c4.setCLeven(4);
                        c4.setCOpenidM(userInfo.getYNickname());
                        commissionService.save(c4);
                        //five
                        UserInfo five = userInfoDao.findByYOpenid(four.getYOid());
                        if (five != null) {
                            //1/20
                            five.setYUsermoney(five.getYUsermoney().add(new BigDecimal((Double.valueOf(num) * 0.1) * 0.05)));
                            userInfoDao.save(five);
                            Commission c5 = new Commission();
                            c5.setCMoney(new BigDecimal((Double.valueOf(num) * 0.1) * 0.05));
                            c5.setCOpenid(five.getYOpenid());
                            c5.setCreateTime(new Date());
                            c5.setCLeven(5);
                            c5.setCOpenidM(userInfo.getYNickname());
                            commissionService.save(c5);
                            //six
                            UserInfo six = userInfoDao.findByYOpenid(five.getYOid());
                            if (six != null) {
                                //1/20
                                six.setYUsermoney(six.getYUsermoney().add(new BigDecimal((Double.valueOf(num) * 0.1) * 0.05)));
                                userInfoDao.save(six);
                                Commission c6 = new Commission();
                                c6.setCMoney(new BigDecimal((Double.valueOf(num) * 0.1) * 0.05));
                                c6.setCOpenid(six.getYOpenid());
                                c6.setCreateTime(new Date());
                                c6.setCLeven(6);
                                c6.setCOpenidM(userInfo.getYNickname());
                                commissionService.save(c6);
                                //seven
                                UserInfo seven = userInfoDao.findByYOpenid(six.getYOid());
                                if (seven != null) {
                                    //1/20
                                    seven.setYUsermoney(seven.getYUsermoney().add(new BigDecimal((Double.valueOf(num) * 0.1) * 0.05)));
                                    userInfoDao.save(seven);
                                    Commission c7 = new Commission();
                                    c7.setCMoney(new BigDecimal((Double.valueOf(num) * 0.1) * 0.05));
                                    c7.setCOpenid(seven.getYOpenid());
                                    c7.setCreateTime(new Date());
                                    c7.setCLeven(7);
                                    c7.setCOpenidM(userInfo.getYNickname());
                                    commissionService.save(c7);
                                    //eight
//                                    UserInfo eight = userInfoDao.findByYOpenid(seven.getYOid());
//                                    if (eight != null) {
//                                        //1/20
//                                        eight.setYUsermoney(eight.getYUsermoney().add(new BigDecimal((Double.valueOf(num) * 0.2) * 0.05)));
//                                        userInfoDao.save(eight);
//                                        Commission c8 = new Commission();
//                                        c8.setCMoney(new BigDecimal((Double.valueOf(num) * 0.2) * 0.05));
//                                        c8.setCOpenid(eight.getYOpenid());
//                                        c8.setCreateTime(new Date());
//                                        commissionService.save(c8);
//                                        //nine
//                                        UserInfo nine = userInfoDao.findByYOpenid(eight.getYOid());
//                                        if (nine != null) {
//                                            //1/20
//                                            nine.setYUsermoney(nine.getYUsermoney().add(new BigDecimal((Double.valueOf(num) * 0.2) * 0.01)));
//                                            userInfoDao.save(nine);
//                                            Commission c9 = new Commission();
//                                            c9.setCMoney(new BigDecimal((Double.valueOf(num) * 0.2) * 0.01));
//                                            c9.setCOpenid(nine.getYOpenid());
//                                            c9.setCreateTime(new Date());
//                                            commissionService.save(c9);
//                                            //ten
//                                            UserInfo ten = userInfoDao.findByYOpenid(nine.getYOid());
//                                            if (ten != null) {
//                                                //1/20
//                                                ten.setYUsermoney(ten.getYUsermoney().add(new BigDecimal((Double.valueOf(num) * 0.2) * 0.01)));
//                                                userInfoDao.save(ten);
//                                                Commission c10 = new Commission();
//                                                c10.setCMoney(new BigDecimal((Double.valueOf(num) * 0.2) * 0.01));
//                                                c10.setCOpenid(ten.getYOpenid());
//                                                c10.setCreateTime(new Date());
//                                                commissionService.save(c10);
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
        save.setYOrderid(oid + "");
        return save;
    }

    @Override
    public Page<UserInfo> findList(Pageable pageable) {
        Page<UserInfo> all = userInfoDao.findAll(pageable);
        return all;
    }

    @Override
    public boolean delOne(Integer id) {
        userInfoDao.deleteById(id);
        return true;
    }


    @Override
    public UserInfo findByOpenId(String openid) {
        return userInfoDao.findByYOpenid(openid);
    }


    @Override
    public UserInfo save(WxMpUser wxMpUser, String oid) {
        UserInfo userInfo = new UserInfo();
        userInfo.setYOtype(UserStatusEnum.DEFAULT_USER_STATUS.getCode());
        userInfo.setYUstatus(UserStatusEnum.DEFAULT_USER_STATUS.getCode());
        if (oid.equals(wxMpUser.getOpenId())) {
            userInfo.setYOid(null);
        } else {
            userInfo.setYOid(oid);
        }
        userInfo.setYUsermoney(new BigDecimal(0));
        userInfo.setYUpic(wxMpUser.getHeadImgUrl());
        userInfo.setYUsertype(UserStatusEnum.DEFAULT_USER_STATUS.getCode());
//        userInfo.setYUsermoney(new BigDecimal(2));
        userInfo.setYOpenid(wxMpUser.getOpenId());
        userInfo.setYNickname(wxMpUser.getNickname());
        userInfo.setYCreatetime(new Date());
        userInfoDao.save(userInfo);
        return userInfo;
    }

    @Override
    @Transactional
    public UserInfo save(UserInfo userInfo) {
        return userInfoDao.save(userInfo);
    }

    @Override
    public UserInfo login(String username, String pwd) {
        return userInfoDao.login(username, pwd);
    }

    @Override
    public Page<UserInfo> findWithPage(Pageable pageable) {
        return userInfoDao.findAll(pageable);
    }

    @Override
    public List<UserInfo> findByOid(String openid) {
        return userInfoDao.findByYOid(openid);
    }

    @Override
    public List<UserInfo> findByOidIn(List<String> openid) {
        return userInfoDao.findByYOidIn(openid);
    }

    @Override
    public List<UserInfo> findByOpenidIn(List<String> openid) {
        return userInfoDao.findByYOpenidIn(openid);
    }

    @Override
    public String findAllMoney() {
        return userInfoDao.findAllMoney();
    }

    @Override
    public String findDayAllMoney() {
        return userInfoDao.findDayAllMoney();
    }

    @Override
    public String allUserCount() {
        return userInfoDao.allUserCount();
    }

    @Override
    public List<UserInfo> findByNicknameLike(String str) {
        return userInfoDao.findByYNicknameLike(str);
    }
}
