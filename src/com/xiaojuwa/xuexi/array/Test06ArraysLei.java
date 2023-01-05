package com.xiaojuwa.xuexi.array;

import java.util.Arrays;


//数组本省自带的几个类
public class Test06ArraysLei {
    public static void main(String[] args) {
        int[] a = {1,3,87,8,36,854,53};
        System.out.println("a = " + Arrays.toString(a));//打印数组元素内容的方法Arrays.toString()

        Arrays.sort(a);//给数组排序的方法Arrays.sort() ：升序
        System.out.println("a = " + Arrays.toString(a));//再次打印数组元素内容
        Arrays.fill(a,1,2,0);
        System.out.println("a = " + Arrays.toString(a));//再次打印数组元素内容
    }

}
