package com.cocoker.utils;

import java.util.Arrays;
import java.util.Map;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019-07-03 10:08
 * @Version: 1.0
 */
public class ASCIISort {
    public static String sort(Map<String, Object> map) {
        String[] sortedKeys = map.keySet().toArray(new String[]{});
        Arrays.sort(sortedKeys);// 排序请求参数
        StringBuilder s2 = new StringBuilder();
        for (String k : sortedKeys) {
            s2.append(k).append("=").append(map.get(k)).append("&");
        }
        s2.deleteCharAt(s2.length() - 1);
        return s2.toString();
    }
}
