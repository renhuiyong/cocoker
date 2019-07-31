package com.cocoker.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/5/6 2:05 PM
 * @Version: 1.0
 */
public class DateUtil {
	private static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat yMd = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DecimalFormat df = new DecimalFormat("#.0000");
    
    public static Date getDateTimeBedore30s2(String time) {
		try {
			return sdf.parse(yMd.format(new Date())+" "+format.format(format.parse(time).getTime()-30*1000));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
    
    
    /** 
    * @Title: getDateTimeBedore30s 
    * @Description: TODO(获取30秒前的时间) 
    * @param @return    设定文件 
    * @return Date    返回类型 
    * @throws 
    */
    public static Date getDateTimeBedore30s(Date date) {
		return new Date(date.getTime()-30*1000);
	}
    
    /** 
    * @Title: getNowDate 
    * @Description: TODO(获取当前日期  yyyy-MM-dd )  
    * @param @return    设定文件 
    * @return String    返回类型 
    * @throws 
    */
    public static String getNowDate() {
		return yMd.format(new Date());
	}
    
    /** 
    * @Title: strToDate 
    * @Description: TODO(字符串转Date  yyyy-MM-dd HH:mm:ss ) 
    * @param @param d
    * @param @return    设定文件 
    * @return Date    返回类型 
    * @throws 
    */
    public static Date strToDate(String d){
    	try {
			return sdf.parse(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    public static Date getTime(Integer h, Integer m, Integer s) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);
        calendar.set(Calendar.SECOND, s);
        Date time = calendar.getTime();
        return time;
    }
    
    public static String getTime(Date date,int s) {
    	date = date==null?new Date():date;
		return format.format((date.getTime() + s * 1000)).toString();
	}
    public static String getTime(long s) {
		return format.format(s).toString();
	}
}
