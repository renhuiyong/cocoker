package com.cocoker.utils;

import com.cocoker.config.ProjectUrl;
import org.springframework.stereotype.Component;

import java.util.Random;

import static com.cocoker.utils.DataGenerateUtil.*;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2018/12/26 8:36 PM
 * @Version: 1.0
 */
@Component
public class RandomUtil {

    public RandomUtil() {
    }

    public RandomUtil(ProjectUrl projectUrl) {
        this.projectUrl = projectUrl;
    }

    private ProjectUrl projectUrl;

//    public static Integer num = 7950;

    public static double last = 6.795;//7.0368
    public static int count = 0, maxCount = 0;
    public static boolean up = true;
    public static Random r = new Random();
    public static int transitionCount = 0;// 转折数量
    // 全局量观察
    public static int time = 0, maxTime = 30, upCount = 0;//定义时间
    public static boolean bigUp = true;

    // TODO 线条
    public static Double getRandomBetween(Integer min, Integer max) {
        time++;
        if (count >= maxCount) {
            changeStatus();
        }
        if (last >= 7 && up) {
            last = 6.9995;
            changeStatus();
        }
        if (last <= 6 && !up) {
            last = 6.0005;
            changeStatus();
        }
        count++;
        if (up) {
            if (r.nextInt(100) > 20) {
                if (mustTransition()) {
                    last = MathUtil.subtract(last, getDoubleValue(true));
                    transitionCount++;
                } else {
                    last = MathUtil.add(last, getDoubleValue(count < 2 || maxCount - count < 2));
                }
            } else {
                if (canTransition()) {
                    last = MathUtil.subtract(last, getDoubleValue(true));
                    transitionCount++;
                } else {
                    last = MathUtil.add(last, getDoubleValue(count < 2 || maxCount - count < 2));
                }
            }
        } else {
            if (r.nextInt(100) > 20) {
                if (mustTransition()) {
                    last = MathUtil.add(last, getDoubleValue(true));
                    transitionCount++;
                } else {
                    last = MathUtil.subtract(last, getDoubleValue(count < 2 || maxCount - count < 2));
                }
            } else {
                if (canTransition()) {
                    last = MathUtil.add(last, getDoubleValue(true));
                    transitionCount++;
                } else {
                    last = MathUtil.subtract(last, getDoubleValue(count < 2 || maxCount - count < 2));
                }
            }
        }
        return last;
    }

    public static Double getMoney() {
        Random r = new Random();
        if (r.nextInt(10) < 3) {
            return 10.00;
        } else if (r.nextInt(10) < 4) {
            return 50.00;
        } else if (r.nextInt(10) < 6) {
            return 100.00;
        } else if (r.nextInt(10) < 7) {
            return 200.00;
        } else if (r.nextInt(10) < 8) {
            return 500.00;
        } else if (r.nextInt(10) < 9) {
            return 1000.00;
        }
        return 2000.00;
    }

    public static Integer getRandomBet(Integer min, Integer max) {
        Random r = new Random();
        Integer i = r.nextInt(max - min + 1) + min;
        return i;
    }

    public static Double getRandomBetD(Integer min, Integer max) {
        Random r = new Random();
        Integer i = r.nextInt(max - min + 1) + min;
        return Double.valueOf("0.000" + i);
    }

    public String getZD(Integer num) {

        // if (num == 5) {
        // if (r.nextInt(100) < 30) {
        // return "盈";
        // } else {
        // return "亏";
        // }
        // }

//        if (num <= 10) {
//            if (r.nextInt(100) < 1) {
//                return "盈";
//            } else {
//                return "亏";
//            }
//        }

        // if (num <= 20) {
        // if (r.nextInt(100) < 1) {
        // return "盈";
        // } else {
        // return "亏";
        // }
        // }


        if (num <= 5) {
            if (r.nextInt(100) < Integer.valueOf(projectUrl.getMoneylessthan5())) {
                return "盈";
            } else {
                return "亏";
            }
        }

        if (num <= 25) {
            if (r.nextInt(100) < Integer.valueOf(projectUrl.getMoneylessthan25())) {
                return "盈";
            } else {
                return "亏";
            }
        }

        if (num <= 50) {
            if (r.nextInt(100) < Integer.valueOf(projectUrl.getMoneylessthan50())) {
                return "盈";
            } else {
                return "亏";
            }
        }

        if (num <= 100) {
            if (r.nextInt(100) < Integer.valueOf(projectUrl.getMoneylessthan100())) {
                return "盈";
            } else {
                return "亏";
            }
        }

        if (num <= 500) {
            if (r.nextInt(100) < Integer.valueOf(projectUrl.getMoneylessthan500())) {
                return "盈";
            } else {
                return "亏";
            }
        }


        // if(num<=30){
        // if(r.nextInt(100)<15){
        // return "盈";
        // }else{
        // return "亏";
        // }
        // }
        // if(num<=100){
        // if(r.nextInt(100)<1){
        // return "盈";
        // }else{
        // return "亏";
        // }
        // }
        // if(num<=500){
        // if(r.nextInt(100)<0){
        // return "盈";
        // }else{
        // return "亏";
        // }
        // }
        // if(num<=1000){
        // if(r.nextInt(100)<10){
        // return "盈";
        // }else{
        // return "亏";
        // }
        // }
        // if(num<=2000){
        // if(r.nextInt(100)<1){
        // return "盈";
        // }else{
        // return "亏";
        // }
        // }
        return "亏";
    }

}
