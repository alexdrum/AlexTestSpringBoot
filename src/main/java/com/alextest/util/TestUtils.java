package com.alextest.util;

import java.util.Map;

/**
 * Created by ALEXDRUM on 2017/6/19.
 */
public class TestUtils {
    public static void printMap(Map<Object, Object> map){
        for(Map.Entry<Object, Object> entry : map.entrySet()){
            log("Key：" + entry.getKey());
            log("Value：" + entry.getValue());
        }
    }

    public static void log(Object log){
        System.out.println(log);
    }

    /**

     * 打印整型数值的完整二进制格式
     * @param x
     * @return
     */
    public static String toFullBinaryString(int x) {
        int[] buffer = new int[Integer.SIZE];
        for (int i = (Integer.SIZE - 1); i >= 0; i--) {
            buffer[i] = x >> i & 1;
        }
        String s = "";
        for (int j = (Integer.SIZE - 1); j >= 0; j--) {
            s = s + buffer[j];
        }
        return s;
    }
}
