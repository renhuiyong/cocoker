package com.cocoker.utils;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019-07-23 14:43
 * @Version: 1.0
 */
public class TestMain {
    public static void main(String[] args) {
        int c = c(5);
        System.out.println(c);
    }

    private static int c(int num) {
        if (num == 1) {
            return num;
        }
        return num * c(num - 1);
    }
}
