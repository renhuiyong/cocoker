package com.cocoker.utils;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2018/12/20 3:03 PM
 * @Version: 1.0
 */
public class MathUtil {
    private static final Double MONEY_RANGE = 0.01;
    public static Boolean equals(Double d1,Double d2){
        double abs = Math.abs(d1 - d2);
        if(abs < MONEY_RANGE){
            return true;
        }else{
            return false;
        }
    }
    public static double add(double a, double b){
        return new BigDecimal(a).add(new BigDecimal(b)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    public static double add(String  a, String b){
        return new BigDecimal(a).add(new BigDecimal(b)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    public static int add(int a, int b){
        return new BigDecimal(a).add(new BigDecimal(b)).setScale(4,BigDecimal.ROUND_HALF_UP).intValue();
    }
    public static double multiply(double a, double b){
        return new BigDecimal(a).multiply(new BigDecimal(b)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    public static int multiply(int a, int b){
        return new BigDecimal(a).multiply(new BigDecimal(b)).setScale(4,BigDecimal.ROUND_HALF_UP).intValue();
    }
    public static int multiply(String a, String b){
        return new BigDecimal(a).multiply(new BigDecimal(b)).setScale(4,BigDecimal.ROUND_HALF_UP).intValue();
    }
    public static int subtract(int a, int b){
        return new BigDecimal(a).subtract(new BigDecimal(b)).setScale(4,BigDecimal.ROUND_HALF_UP).intValue();
    }
    public static double subtract(String a, String b){
        return new BigDecimal(a).subtract(new BigDecimal(b)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    public static double subtract(double a, double b){
        return new BigDecimal(a).subtract(new BigDecimal(b)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    public static double divide(double a, double b){
        return new BigDecimal(a).divide(new BigDecimal(b),4).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    public static int divide(int a, int b){
        return new BigDecimal(a).divide(new BigDecimal(b),4).setScale(4,BigDecimal.ROUND_HALF_UP).intValue();
    }
    public static double abs(double a){
        return new BigDecimal(a).abs().setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    public static double abs(String a){
        return new BigDecimal(a).abs().setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    public static double abs(int a){
        return new BigDecimal(a).abs().setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    public static double xfs(double a) {
		return multiply(a, divide(a, abs(a)));
	}
    public static double xfs(String b) {
    	double a = Double.valueOf(b);
		return multiply(a, divide(a, abs(a)));
	}
    //绝对值
    //BigDecimal result4 = num3.abs();
   // BigDecimal result42 = num32.abs();
}
