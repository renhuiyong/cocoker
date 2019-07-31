package com.cocoker.utils;
import static com.cocoker.utils.RandomUtil.*;
import static com.cocoker.utils.MathUtil.*;
/** 
* @ClassName: DataGenerateUtil 
* @Description: TODO(随机数生成工具辅助类) 
* @author lcy
* @date 2019年7月8日 下午3:55:48 
*  
*/
public class DataGenerateUtil {
	/** 
	* @Title: canTransition 
	* @Description: TODO(是否可以转折) 
	* @param @return    设定文件 
	* @return boolean    返回类型 
	* @throws 
	*/
	public static boolean canTransition(){
		return    (transitionCount<=divide(maxCount, 3)&&maxCount>5)
				||(transitionCount<=2&&maxCount<=5);
	}
	/** 
	* @Title: mustTransition 
	* @Description: TODO(必定转折条件) 
	* @param @return    设定文件 
	* @return boolean    返回类型 
	* @throws 
	*/
	public static boolean mustTransition(){
		return    (transitionCount == 0 && count > 3 && maxCount >=10&& r.nextInt(10)>6)
				||(transitionCount < 2 && maxCount >10 && maxCount - count < 3 && (transitionCount>0?r.nextBoolean():true));
	}
	
	public static double getDoubleValue(boolean j) {
		return Double.valueOf("0.000" + getRandomBet(j ? 5 : 3, j ? 6 : 5));
	}
	
	/** 
	* @Title: changeStatus 
	* @Description: TODO(折现状态重置) 
	* @param     设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	public static void changeStatus() {
		// 大小折
		if (r.nextInt(100) > 40) {//40
			if (time>maxTime) {
				time=0;
				maxTime = getRandomBet(15, 35);
				bigUp = !bigUp;
			}
			maxCount = getRandomBet(9, 15);
			if(upCount<2){
				up = bigUp;
				upCount++;
			}else {
				up=!bigUp;
				upCount=0;
			}
			
		} else {
			//if (getRandomBet(1, 10) > 3) {
				maxCount = getRandomBet(3, 7);
			//} else {
			//	maxCount = getRandomBet(1, 5);
			//}
			up = !up;
		}
		count = 0;
		transitionCount = 0;
	}
}
