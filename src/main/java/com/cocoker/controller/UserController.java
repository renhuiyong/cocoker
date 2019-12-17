package com.cocoker.controller;

import com.cocoker.VO.ResultVO;
import com.cocoker.beans.CommissionRedPack;
import com.cocoker.beans.Complaint;
import com.cocoker.beans.Signin;
import com.cocoker.beans.UserInfo;
import com.cocoker.config.ProjectUrl;
import com.cocoker.dto.UserDetailDTO;
import com.cocoker.enums.Positions;
import com.cocoker.enums.ResultEnum;
import com.cocoker.enums.UserStatusEnum;
import com.cocoker.exception.CocokerException;
import com.cocoker.service.*;
import com.cocoker.utils.QRCodeUtil;
import com.cocoker.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2018/12/24 9:31 PM
 * @Version: 1.0
 */
@Controller
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private CommissionRedPackService commissionRedPackService;

    @Autowired
    private TipService tipService;

    @Autowired
    private SigninService signinService;


    @Autowired
    private ProjectUrl projectUrl;

    private static String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();

    private static final String TG_PIC2 = "/Users/renhuiyong/Desktop/tgqr2.png";
    private static final String TG_PIC3 = "/Users/renhuiyong/Desktop/tgqr3.png";
    private static final String TG_PIC4 = "/Users/renhuiyong/Desktop/tgqr4.png";

    @GetMapping("/complaint")
    public ModelAndView complaint(@RequestParam("openid") String openid) {
        UserInfo userInfo = userInfoService.findByOpenId(openid);
        if (userInfo != null) {
            userInfo.setYUstatus(UserStatusEnum.USER_COMPLAINT_STATUS.getCode());
            userInfoService.save(userInfo);
            complaintService.addComplaint(new Complaint().setBalance(userInfo.getYUsermoney()).setCreateTime(new Date()).setWxNickname(userInfo.getYNickname()).setWxOpenid(userInfo.getYOpenid()));
        }
        return new ModelAndView("wxtousu/suc");
    }

    @PostMapping("/redpack")
    @ResponseBody
    @Transactional
    public synchronized ResultVO redpack(@RequestParam("m") String money, @RequestParam("openid") String openid) {
        //检查金额
        checkMoney(openid, money);

        //今日佣金
        String commission = commissionService.findByCOpenidAndTime(openid);

        if (commission == null || Double.valueOf(money) > Double.valueOf(commission)) {
            return ResultVOUtil.error(-1, "条件未达成！");
        }
        CommissionRedPack exist = commissionRedPackService.selectRedpackDoesItExist(openid, money);
        if (exist != null) {
            return ResultVOUtil.error(-1, "已经领取过啦！");
        }
        CommissionRedPack save = commissionRedPackService.save(new CommissionRedPack().setMoney(new BigDecimal(money)).setCreateTime(new Date()).setOpenid(openid));
        UserInfo userInfo = userInfoService.findByOpenId(openid);
        userInfo.setYUsermoney(userInfo.getYUsermoney().add(new BigDecimal(money)));
        UserInfo result = userInfoService.save(userInfo);
        if (result == null || save == null) {
            return ResultVOUtil.error(-1, "请稍后再试！");
        }
        return ResultVOUtil.success();
    }

    private void checkMoney(String openid, String money) {
        String str = "5-20-50-188";
        boolean result = Arrays.asList(str.split("-")).contains(money);
        if (!result) {
            log.error("[红包] 红包领取金额异常, openid={},moeny={},正常金额={}", openid, money, str);
            throw new CocokerException(ResultEnum.RECHARGE_MONEY_ERROR);
        }
    }

    @ResponseBody
    @GetMapping("/get")
    public ResultVO getUserByOpenId(@RequestParam("openid") String openid) {
        UserInfo userInfo = userInfoService.findByOpenId(openid);
        if (userInfo == null) {
//            log.error("[用户查询] 查询失败 用户openid={}",openid);
            return ResultVOUtil.error(ResultEnum.USER_NOT_EXIST.getCode(), ResultEnum.USER_NOT_EXIST.getMsg());
        } else {
            return ResultVOUtil.success(new UserDetailDTO("", userInfo.getYUsername(), userInfo.getYNickname(), userInfo.getYUpic(), userInfo.getYUsermoney(), "请不要攻击我，谢谢！"));
        }
    }

    @GetMapping("/signin")
    @ResponseBody
    @Transactional
    public synchronized ResultVO signIn(@RequestParam("openid") String openid) {


        String s = signinService.selectSigninDoesItExist(openid);
        if (s.equals("0")) {
//            Signin signin = signinService.selectSigninBytoDay(openid);
//            if (signin == null) {
            Signin signin1 = new Signin();
            signin1.setOpenid(openid);
            String money = String.format("%.2f", (Math.random() * 0.2 + 0.1));
//            Random r = new Random();
//            if (r.nextInt(100) < 1) {
//                money = "8.88";
//            }
            signin1.setMoney(new BigDecimal(money));
            signin1.setCreateTime(new Date());
            signinService.insertSignin(signin1);
            UserInfo user = userInfoService.findByOpenId(openid);
            user.setYUsermoney(user.getYUsermoney().add(new BigDecimal(money)));
            userInfoService.save(user);
            return ResultVOUtil.success(signin1);
//            }
        } else {
            Signin signin = signinService.selectSigninBytoDay(openid);
            if (signin == null) {
                Signin signin1 = new Signin();
                signin1.setOpenid(openid);
                String money = String.format("%.2f", (Math.random() * 0.1) + 0.1);
                signin1.setMoney(new BigDecimal(money));
                signin1.setCreateTime(new Date());
                signinService.insertSignin(signin1);
                UserInfo user = userInfoService.findByOpenId(openid);
                user.setYUsermoney(user.getYUsermoney().add(new BigDecimal(money)));
                userInfoService.save(user);
                return ResultVOUtil.success(signin1);
            } else {
                return ResultVOUtil.error(signin);
            }
        }
    }

    @RequestMapping(value = "/uniqueQrcode", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] qr(@RequestParam("openid") String openid) throws Exception {
        UserInfo userInfo = userInfoService.findByOpenId(openid);
        if (userInfo == null) {
            return null;
        }
        String text = projectUrl.getQrUrl() + "/cocoker/wechat/authorize?upOpenid=" + openid + "&returnUrl=" + tipService.getReturnUrl() + "/cocoker/coc"; // 随机生成验证码
//        String text = projectUrl.getQrUrl() + "/cocoker/wechat/authorize?upOpenid=" + openid + "&returnUrl=" + projectUrl.getReturnUrl() + "/cocoker/coc"; // 随机生成验证码
        int width = 135; // 二维码图片的宽
        int height = 135; // 二维码图片的高
        String format = "png"; // 二维码图片的格式
        try {
            Thumbnails.of(new File("/Users/renhuiyong/Desktop/tg2.png")).size(414, 736)
                    .watermark(com.cocoker.enums.Positions.CUSTOM_CENBUT, ImageIO.read(new File(QRCodeUtil.generateQRCode(text, width, height, format, "/Users/renhuiyong/Desktop/qr2.png"))), 1f)
                    .outputQuality(0.9).toFile(TG_PIC2);
        } catch (Exception e) {
            e.getMessage();
        }


        File file = new File(TG_PIC2);

        saveToFile(userInfo.getYUpic());
        changeSize(80, 80, TG_PIC3);


        Thumbnails.of(file).size(414, 736).watermark(Positions.CUSTOM_BUTLEF2, ImageIO.read(new File(TG_PIC3)), 1F)
                .outputQuality(0.9).toFile(TG_PIC4);
        File file4 = new File(TG_PIC4);

        FileInputStream inputStream = new FileInputStream(file4);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes, 0, inputStream.available());
        return bytes;
    }


    public void saveToFile(String destUrl) {
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        HttpURLConnection httpUrl = null;
        URL url = null;
        int BUFFER_SIZE = 1024;
        byte[] buf = new byte[BUFFER_SIZE];
        int size = 0;
        try {
            url = new URL(destUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            fos = new FileOutputStream(TG_PIC3);
            while ((size = bis.read(buf)) != -1) {
                fos.write(buf, 0, size);
            }
            fos.flush();
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                fos.close();
                bis.close();
                httpUrl.disconnect();
            } catch (IOException e) {
                e.getMessage();
            }
        }
    }

    public boolean changeSize(int newWidth, int newHeight, String path) {
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(path));

            //字节流转图片对象
            Image bi = ImageIO.read(in);
            //构建图片流
            BufferedImage tag = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            //绘制改变尺寸后的图
            tag.getGraphics().drawImage(bi, 0, 0, newWidth, newHeight, null);
            //输出流
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(path));
            //JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            //encoder.encode(tag);
            ImageIO.write(tag, "PNG", out);
            in.close();
            out.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
