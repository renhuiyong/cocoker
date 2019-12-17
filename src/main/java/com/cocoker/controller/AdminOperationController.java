package com.cocoker.controller;

import com.cocoker.VO.ResultVO;
import com.cocoker.beans.*;
import com.cocoker.dao.OrderDao;
import com.cocoker.enums.ResultEnum;
import com.cocoker.service.*;
import com.cocoker.utils.DateUtil;
import com.cocoker.utils.MD5;
import com.cocoker.utils.RandomUtil;
import com.cocoker.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import static com.cocoker.utils.MathUtil.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.cocoker.utils.MathUtil.*;
import static com.cocoker.utils.DateUtil.*;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/1/9 2:35 PM
 * @Version: 1.0
 */
@RestController
@RequestMapping("/admin/operation")
@Slf4j
public class AdminOperationController {
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private OrderService orderService;
    @Autowired
    public EchartsService echartsService;
    @Autowired
    private TempService tempService;
    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private TipService tipService;

    public static Integer EchartsConst = 0;

    public static Integer OrderConst = 0;

    public static Integer updConst = 0;

    private static String[] IMAGE_LOCATIONS = new String[]{"https://pic.qqtn.com/up/2019-5/2019052907541954443.jpg",
            "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1481540776,4222990614&fm=26&gp=0.jpg",
            "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2567486939,2426939985&fm=26&gp=0.jpg",
            "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3505540718,2809886054&fm=26&gp=0.jpg",
            "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1557475058,916927548&fm=26&gp=0.jpg",
            "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1230600435,1649419807&fm=26&gp=0.jpg",
            "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=4090061760,3566002114&fm=27&gp=0.jpg",
            "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=700542685,1150922907&fm=26&gp=0.jpg",
            "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3482408811,947324587&fm=26&gp=0.jpg",
            "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2824234787,3553844716&fm=26&gp=0.jpg",
            "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1732022464,4158907838&fm=26&gp=0.jpg",
            "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2850986207,1715652474&fm=27&gp=0.jpg",
            "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2527648803,3556430236&fm=27&gp=0.jpg",
            "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1809377770,2532269682&fm=27&gp=0.jpg",
            "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2508649768,1309602683&fm=27&gp=0.jpg",
            "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1988911137,4266060008&fm=27&gp=0.jpg",
            "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1941739257,2931686383&fm=27&gp=0.jpg",
            "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=895046909,477415061&fm=27&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=3526052315,3595700012&fm=26&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=4061012364,2388910935&fm=26&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=1928635196,4109493064&fm=26&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=1679221648,2532333860&fm=26&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=1388373050,1079845682&fm=26&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=2055186473,1354558157&fm=26&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=1761813507,679296415&fm=26&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=2478023645,3383307706&fm=26&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=1761172662,2670545896&fm=26&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=3229557893,1220314715&fm=26&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=1617701295,3521002877&fm=26&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=2767013220,2068790448&fm=26&gp=0.jpg",
            "http://img1.imgtn.bdimg.com/it/u=788220329,3588177750&fm=26&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=2068174366,1400943784&fm=26&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=1874096570,2977284772&fm=26&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=3226134130,687428196&fm=26&gp=0.jpg"
    };

    private String[] imagelocations;

    public AdminOperationController() {
        this.imagelocations = IMAGE_LOCATIONS;
    }

    private static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DecimalFormat df = new DecimalFormat("#.0000");
    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;

    /**
     * 删除用户
     *
     * @param uid
     * @param session
     * @return
     */
    @GetMapping("/delUser")
    public ModelAndView delUser(@RequestParam("id") String uid, HttpSession session) {
        if (uid == null) {

        }
        userInfoService.delOne(Integer.valueOf(uid));
        return new ModelAndView("redirect:/admin/index");
    }

    /**
     * 锁用户
     *
     * @param id
     * @return
     */
    @PostMapping("/lock/{id}")
    public ResultVO lock(@PathVariable("id") String id) {
        if (id == null) {
            return ResultVOUtil.error(-1, ResultEnum.USER_NOT_EXIST.getMsg());
        }
        UserInfo result = userInfoService.findOne(Integer.valueOf(id));
        if (result == null) {
            return ResultVOUtil.error(-1, ResultEnum.USER_NOT_EXIST.getMsg());
        }
        result.setYUstatus(1);
        UserInfo save = userInfoService.save(result);
        if (save == null) {
            return ResultVOUtil.error(-1, ResultEnum.UPDATE_USER_ERROR.getMsg());
        }
        return ResultVOUtil.success();
    }


    /**
     * 解锁用户
     *
     * @param id
     * @return
     */
    @PostMapping("/unlock/{id}")
    public ResultVO unlock(@PathVariable("id") String id) {
        if (id == null) {
            return ResultVOUtil.error(-1, ResultEnum.USER_NOT_EXIST.getMsg());
        }
        UserInfo result = userInfoService.findOne(Integer.valueOf(id));
        if (result == null) {
            return ResultVOUtil.error(-1, ResultEnum.USER_NOT_EXIST.getMsg());
        }
        result.setYUstatus(0);
        UserInfo save = userInfoService.save(result);
        if (save == null) {
            return ResultVOUtil.error(-1, ResultEnum.UPDATE_USER_ERROR.getMsg());
        }
        return ResultVOUtil.success();
    }

    private static SimpleDateFormat yMd = new SimpleDateFormat("yyyy-MM-dd");

    private ResultVO fkPublic(Order order) throws Exception {
        //计算下单点  对于echarts的时间
        Date d = order.getCreateTime();
        Echarts begin = echartsService.findByCreateTime(getTime(d, 26));

        double s = subtract(order.getOfinal(), begin.getPrice());//投注点与终点距离
        double runTime = Math.floor(divide(Double.valueOf(abs(s)), 0.0003));//能够执行几次
        double tmp = Double.valueOf(begin.getPrice());//起始值
        double vg = s > 0 ? 0.0005 : -0.0005;//每次的增减量
        double[] pricesArray = new double[24];
        String[] timeArrayEcharts = new String[24];//曲线时间
        int arrayIndex = 0;
        if (runTime < 3) {
            for (int i = 1; i <= 3; i++) {
                tmp = i < 3 ? (subtract(tmp, vg)) : (add(tmp, vg));
                pricesArray[arrayIndex] = tmp;
                timeArrayEcharts[arrayIndex] = getTime(d, 26 + i);
                arrayIndex++;
            }
            //System.out.println("模式1：s="+s+"runtime="+runTime);
        } else if (runTime < 4) {
            for (int i = 1; i <= 3; i++) {
                if (i == 1) {
                    tmp = add(tmp, vg);
                } else {
                    if (i == 2) {
                        vg = divide(add(s, vg), 3);//获取接下来的平均值
                    }
                    tmp = add(tmp, vg);
                }
                pricesArray[arrayIndex] = tmp;
                timeArrayEcharts[arrayIndex] = getTime(d, 26 + i);
                arrayIndex++;
            }
            //System.out.println("模式2：s="+s+"runtime="+runTime);
        } else {
            //ofinal 需要重新赋值   要在ofinl到index之间
            if (runTime > 5) {
                if (Double.valueOf(order.getOfinal()) > Double.valueOf(order.getOindex())) {
                    order.setOfinal(add(order.getOindex(), "0.0003") + "");
                } else {
                    order.setOfinal(subtract(order.getOindex(), "0.0003") + "");
                }
                s = subtract(order.getOfinal(), begin.getPrice());
            }
            vg = divide(s, 4);
            for (int i = 1; i <= 3; i++) {
                tmp = add(tmp, vg);
                pricesArray[arrayIndex] = tmp;
                timeArrayEcharts[arrayIndex] = getTime(d, 26 + i);
                arrayIndex++;
            }
            // System.out.println("模式1：3="+s+"runtime="+runTime);
        }
        tmp = Double.valueOf(order.getOfinal());
        pricesArray[arrayIndex] = tmp;
        timeArrayEcharts[arrayIndex] = getTime(d, 30);
        arrayIndex++;
        Echarts end = echartsService.findByCreateTime(getTime(d, 35));
        int j = 1;
        boolean idLow = false;//2个点以后是否  小于0.0009
        boolean find = false;
        for (int i = 1; i <= 4; i++) {
            if (i == 1) {//对过度点过大做一个调整
                if (vg < -0.0005) {
                    vg = -0.0004;
                } else if (vg > 0.0005) {
                    vg = 0.0004;
                }
            }
            if (i == 3) {//过度2个点
                s = subtract(end.getPrice(), tmp + "");
                if (abs(s) > 0.0015) {//大于15重新找点
                    //System.out.println("大于15重新找点");
                    for (; j <= 15; j++) {
                        Echarts echarts = echartsService.findByCreateTime(getTime(d, 35 + j));
                        vg = divide(abs(subtract(echarts.getPrice(), tmp + "")), (3 + j));
                        //System.out.println("vg1："+vg);
                        if (vg > 0.0005) {//平均值大于4  再找
                            continue;
                        } else {//否则就取这个点
                            s = subtract(echarts.getPrice(), tmp + "");
                            vg = divide(s, (3 + j));
                            //System.out.println("vg2："+vg);
                            find = true;
                            break;
                        }
                    }
                } else if (abs(s) < 0.0009) {
                    idLow = true;
                }
                if (!find) {
                    vg = divide(s, 3);
                }
            }
            if (idLow) {
                if (i == 3) {
                    vg = vg > 0 ? 0.0004 : -0.0004;
                } else {
                    vg = divide(add(s, vg), 2);
                }
            }
            tmp = add(tmp, vg);
            pricesArray[arrayIndex] = tmp;
            timeArrayEcharts[arrayIndex] = getTime(d, 30 + i);
            arrayIndex++;
        }
        if (find) {//需要扩充点  35以后的点（包括35）
            int xb = 0;
            for (; j > 0; j--) {
                tmp = add(tmp, vg);
                pricesArray[arrayIndex] = tmp;
                timeArrayEcharts[arrayIndex] = getTime(d, 35 + xb++);
                arrayIndex++;
            }
        }
        for (int i = 0; i < pricesArray.length; i++) {
            if (pricesArray[i] != 0) {//去掉未赋值的
                echartsService.updPriceAndLockByTime(df.format(pricesArray[i]), timeArrayEcharts[i].toString());
                //订单信息
                orderService.changeOrders(timeArrayEcharts[i].toString(), pricesArray[i] + "");
            }
        }
        Integer result = orderService.saveResult(df.format(Double.valueOf(order.getOfinal())), order.getResult(), "1", order.getOid().toString());
        if (result < 0) {
            return ResultVOUtil.error(ResultEnum.UPDATE_ORDER_ERROR.getCode(), ResultEnum.UPDATE_ORDER_ERROR.getMsg());
        }
        return null;
    }

    /**
     * 设置赢
     *
     * @param oid
     * @return
     */
    @PostMapping("/up/{oid}")
    public ResultVO up(@PathVariable("oid") Integer oid) {
        Order order = orderService.findByOid(oid);
        if (new Date().getTime() - 30 * 1000 > order.getCreateTime().getTime()) {
            return ResultVOUtil.error(-1, "超过30秒");
        }
        order.setResult("盈");
        int rOfinal = RandomUtil.getRandomBet(8, 15);
        boolean isRepear = true;//是否需要改点
        Echarts echarts30 = echartsService.findByCreateTime(getTime(order.getCreateTime(), 30));
        if (order.getDirection().equals("看涨")) {
            if (Double.valueOf(echarts30.getPrice()) > Double.valueOf(order.getOindex())) {
                order.setOfinal(echarts30.getPrice());
                isRepear = false;
            } else {
                order.setOfinal(add(order.getOindex(), rOfinal >= 10 ? ("0.00" + rOfinal) : ("0.000" + rOfinal)) + "");
            }
        }
        if (order.getDirection().equals("看跌")) {
            if (Double.valueOf(echarts30.getPrice()) < Double.valueOf(order.getOindex())) {
                order.setOfinal(echarts30.getPrice());
                isRepear = false;
            } else {
                order.setOfinal(subtract(order.getOindex(), rOfinal >= 10 ? ("0.00" + rOfinal) : ("0.000" + rOfinal)) + "");
            }
        }
        ResultVO vo = null;
        try {
            if (isRepear) {
                vo = fkPublic(order);
                order = orderService.findByOid(oid);
            } else {
                orderService.saveResult(df.format(Double.valueOf(order.getOfinal())), order.getResult(), "1", order.getOid().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (vo != null)
            return vo;
        //加余额
        Temp temp = new Temp();
        temp.setTMoney(order.getMoney().multiply(new BigDecimal(1.85)));
        temp.setTOpenid(order.getOpenid());
        temp.setCreateTime(order.getCreateTime());
        order = orderService.findLastOrderByOpenidAndCreateDate(order.getOpenid());
        temp.setOrderid(order.getOid());
        Temp r = tempService.save(temp);
        if (r == null) {
            log.error("【用户订单修改 加余额】增加失败,temp: {}", temp);
            return ResultVOUtil.error(ResultEnum.UPDATE_ORDER_ERROR.getCode(), ResultEnum.UPDATE_ORDER_ERROR.getMsg());
        }
        return ResultVOUtil.success();
    }


    /**
     * 设置输
     *
     * @param oid
     * @return
     */
    @PostMapping("/down/{oid}")
    public ResultVO down(@PathVariable("oid") Integer oid) {
        return editOrder(oid);
    }


    public ResultVO editOrder(Integer oid) {
        Order order = orderService.findByOid(oid);
        if (new Date().getTime() - 30 * 1000 > order.getCreateTime().getTime()) {
            return ResultVOUtil.error(-1, "超过30秒");
        }
        order.setResult("亏");
        int rOfinal = RandomUtil.getRandomBet(8, 15);
        boolean isRepear = true;//是否需要改点
        Echarts echarts30 = echartsService.findByCreateTime(getTime(order.getCreateTime(), 30));
        if (order.getDirection().equals("看涨")) {
            if (Double.valueOf(echarts30.getPrice()) < Double.valueOf(order.getOindex())) {
                order.setOfinal(echarts30.getPrice());
                isRepear = false;
            } else {
                order.setOfinal(subtract(order.getOindex(), rOfinal >= 10 ? ("0.00" + rOfinal) : ("0.000" + rOfinal)) + "");
            }
        }
        if (order.getDirection().equals("看跌")) {
            if (Double.valueOf(echarts30.getPrice()) > Double.valueOf(order.getOindex())) {
                order.setOfinal(echarts30.getPrice());
                isRepear = false;
            } else {
                order.setOfinal(add(order.getOindex(), rOfinal >= 10 ? ("0.00" + rOfinal) : ("0.000" + rOfinal)) + "");
            }
        }
        ResultVO vo = null;
        try {
            if (isRepear) {
                vo = fkPublic(order);
                order = orderService.findByOid(oid);
            } else {
                orderService.saveResult(df.format(Double.valueOf(order.getOfinal())), order.getResult(), "1", order.getOid().toString());
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (vo != null)
            return vo;
        //减掉余额
        // List<Temp> temps = tempService.findByTOpenid(order.getOpenid());
        List<Temp> temps = tempService.findByTOpenidAndOrderid(order.getOpenid(), oid + "");
        if (null == temps || temps.size() == 0) {
            return ResultVOUtil.error(ResultEnum.UPDATE_ORDER_ERROR.getCode(), ResultEnum.UPDATE_ORDER_ERROR.getMsg());
        }
        List<Integer> a = temps.stream().map(e -> e.getTId()).collect(Collectors.toList());
        int num = tempService.delTemp(temps.stream().map(e -> e.getTId()).collect(Collectors.toList()));
        if (num < 0) {
            log.error("[更新订单删除余额] 删除失败，temps：{} ", temps);
            return ResultVOUtil.error(ResultEnum.UPDATE_ORDER_ERROR.getCode(), ResultEnum.UPDATE_ORDER_ERROR.getMsg());
        }
        return ResultVOUtil.success();
    }


    /**
     * 修改管理员密码
     *
     * @param session
     * @param oldPwd  旧密码
     * @param pwd1    新密码
     * @param pwd2    确认密码
     * @param uid     uid
     * @return
     */
    @PostMapping("/editAdmin")
    public ResultVO editAdmin(HttpSession session, @RequestParam("oldPwd") String oldPwd, @RequestParam("pwd1") String pwd1, @RequestParam("pwd2") String pwd2, @RequestParam("uid") String uid) {
        if (!pwd1.equals(pwd2)) {
            return ResultVOUtil.error(-1, "2次输入不一致");
        }
        UserInfo one = userInfoService.findOne(Integer.valueOf(uid));
        if (!MD5.md5(oldPwd, "").equals(one.getYUpwd())) {
            return ResultVOUtil.error(-1, "旧密码不对");
        }
        one.setYUpwd(MD5.md5(pwd2, ""));
        UserInfo save = userInfoService.save(one);
        if (save == null) {
            return ResultVOUtil.error(-1, "修改失败");
        }
        session.removeAttribute("admin");
        return ResultVOUtil.success();
    }


    public static boolean isCreate = false;

    /**
     * 设置E charts数据
     *
     * @return
     */
    @GetMapping("/setEchartsDatas")
    @ResponseBody
    @PostConstruct
    public String setEchartsData() {
        DecimalFormat df = new DecimalFormat("#.0000");
        if (EchartsConst == 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Timer timer = new Timer(true);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    timer.schedule(new TimerTask() {
                        public void run() {
                            echartsService.add(new Echarts(sdf.format(new Date().getTime() + 60000).toString(), df.format(Double.valueOf(RandomUtil.getRandomBetween(1, 10).toString()))));
                        }
                    }, 1000, 1000);
                }
            }).start();
            EchartsConst++;
            //定时清除Echarts
            Date date = DateUtil.getTime(00, 00, 00);//凌晨0点//第一次执行定时任务的时间
            //如果第一次执行定时任务的时间 小于当前的时间
            //此时要在 第一次执行定时任务的时间加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
            if (date.before(new Date())) {
                date = this.addDay(date, 1);
            }
            Timer timer = new Timer();
            TimingTask1 task = new TimingTask1(echartsService);
            //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
            timer.schedule(task, date, PERIOD_DAY);

        } else {

            return "只能设置一次";
        }
        return "ok";
    }


    /**
     * 模拟订单数据
     *
     * @return
     */
    @GetMapping("/setOrderDatas")
    @ResponseBody
    @PostConstruct
    public String setOrderDatas() {
        if (OrderConst == 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Timer timer = new Timer(true);
                    timer.schedule(new TimerTask() {
                        public void run() {
                            //'看跌', '6.7980', '6.7972', 10, '盈', '2019-05-23 14:33:51', '0', 'oMjjs5-6Pnvh94PquDiReZKquwAc', 'soul',
                            //        Integer save = orderDao.saveByAll(o.getOpenid(), o.getDirection(), o.getOindex(), df.format(Double.valueOf(o.getOfinal())), o.getMoney(), o.getResult(), sdf.format(o.getCreateTime()).toString(), o.getHandle(), o.getOnickname(), o.getOpic());
//                            orderService.addOrder("看跌","6.7977","0","openid",sdf.format(new Date()));
                            orderService.saveByAll("openid", new Random().nextInt(5) % 2 == 0 ? "看涨" : "看跌", "6.7977", "7.7978", new BigDecimal(RandomUtil.getMoney()), "盈", sdf.format(new Date()), "2", "name", imagelocations[new Random().nextInt(imagelocations.length)]);
                            //设置模拟交易额
                            tipService.setTurnover();
                        }
                    }, 1000, 13000);
                }
            }).start();
            OrderConst++;
        } else {
            return "只能设置一次";
        }
        return "ok";
    }


    @GetMapping("/x")
    @ResponseBody
    public @PostConstruct
    void autoEditOrder() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Timer timer = new Timer(true);
                timer.schedule(new TimerTask() {
                    public void run() {
                        if (updConst == 0) {
                            List<Order> toBeRevised = orderService.findToBeRevised();
                            if (toBeRevised.size() != 0) {
                                toBeRevised.forEach(order -> {
                                    editOrder(order.getOid());
                                });
                            }
                        }
                    }
                }, 1000, 10 * 1000);
            }
        }).start();
    }


    @GetMapping("/updOn")
    @ResponseBody
    public String updOn() {
        updConst = 0;
        return "ok";
    }

    @GetMapping("/updOff")
    @ResponseBody
    public String updOff() {
        updConst = 1;
        return "ok";
    }

    @GetMapping("/disorderSequence")
    @ResponseBody
    public String disorderSequence() {
        Collections.shuffle(Arrays.asList(OrderController.IMAGE_LOCATIONS));
        return "ok";
    }

    // 增加或减少天数
    public Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }

    /**
     * 清除E charts
     *
     * @return
     */
    @GetMapping("/cleanEcharts")
    public String cleanEcharts() {
        Integer clean = echartsService.cleanEcharts();
        if (clean < 0) {
            return "error";
        }
        return "ok";
    }


    /**
     * 查询用户信息
     *
     * @param uid
     * @param map
     * @return
     */
    @PostMapping("/userInfo/{uid}")
    public ResultVO userInfo(@PathVariable("uid") Integer uid, Map<String, Object> map) {
        UserInfo one = userInfoService.findOne(uid);
        if (one == null) {
            return ResultVOUtil.error(-1, "没查到该用户");
        }
        List<Recharge> recharges = rechargeService.findByOpenid(one.getYOpenid());
        map.put("recharges", recharges);
        map.put("one", one);
        return ResultVOUtil.success(map);
    }


    @PostMapping("/editMoney")
    @Transactional
    public ResultVO editMoney(@RequestParam("uid") Integer uid, @RequestParam("umoney") String money, HttpSession session) {
        UserInfo one = userInfoService.findOne(uid);
        if (one == null) {
            return ResultVOUtil.error(-1, "没查到");
        }
        one.setYUsermoney(new BigDecimal(money));
        UserInfo save = userInfoService.save(one);
        if (save == null) {
            return ResultVOUtil.error(-1, "修改失败");
        }
        UserInfo admin = (UserInfo) session.getAttribute("admin");
        Balance b = new Balance();
        b.setUMoney(new BigDecimal(money));
//        b.setCreateTime(new Date());
        b.setUAdmin(admin.getYOpenid());
        b.setUOpenid(one.getYOpenid());
        b.setUUsername(one.getYNickname());
        balanceService.save(b);
        return ResultVOUtil.success();
    }

//    @GetMapping("/rhyme")
//    @ResponseBody
//    public ResultVO grh() {
//        List<Balance> all = balanceService.findAll();
//        Map<String, Object> map = new HashMap<>();
//        map.put("all", all);
//        return ResultVOUtil.success(map);
//    }

    @PostMapping("/search/{uName}")
    public ResultVO search(@PathVariable("uName") String uName, Map<String, Object> map) {
        List<UserInfo> userInfoList = userInfoService.findByNicknameLike("%" + uName + "%");
        if (null == userInfoList || userInfoList.size() == 0) {
            return ResultVOUtil.error(-1, "没查到");
        }
        map.put("userInfoList", userInfoList);
        return ResultVOUtil.success(userInfoList);
    }


    @PostMapping("/settip")
    @ResponseBody
    public String setTip(@RequestParam(value = "msg", required = false) String msg) {
        if (msg == "" || msg == null) {
            int i = tipService.updTip("");
            return "设置位空";
        }
        int i = tipService.updTip(msg);
        if (i > 0) {
            return "ok";
        }
        return "设置失败";
    }


    @PostMapping("/setReturnUrl")
    @ResponseBody
    public String setReturnUrl(@RequestParam(value = "url", required = false) String url) {
        if (url == "" || url == null) {
//            int i = tipService.updTip("");
            return "不能设置为空";
        }
        int i = tipService.updReturnUrl(url);
        if (i > 0) {
            return "ok";
        }
        return "设置失败";
    }


    @PostMapping("/setOrderMoney")
    @ResponseBody
    public String setOrderMoney(@RequestParam(value = "str", required = false) String str) {
        if (str == "" || str == null) {
            return "不能设置为空";
        }
        int i = tipService.setOrderMoeny(str);
        if (i > 0) {
            return "ok";
        }
        return "设置失败";
    }

    @PostMapping("/setRechargeMoney")
    @ResponseBody
    public String setRechargeMoney(@RequestParam(value = "str", required = false) String str) {
        if (str == "" || str == null) {
            return "不能设置为空";
        }
        int i = tipService.setRechargeMoeny(str);
        if (i > 0) {
            return "ok";
        }
        return "设置失败";
    }


    @GetMapping("/trueMaintain")
    public String trueMaintain() {
        tipService.setIfMaintain(true);
        return "ok";
    }

    @GetMapping("/falseMaintain")
    public String falseMaintain() {
        tipService.setIfMaintain(false);
        return "ok";
    }

}
