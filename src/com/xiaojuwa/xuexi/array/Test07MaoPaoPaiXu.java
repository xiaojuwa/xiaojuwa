package com.xiaojuwa.xuexi.array;

import java.util.Arrays;

//冒泡排序：比较相邻的元素，如果第一个数比第二个数大，就交换位置。
public class Test07MaoPaoPaiXu {
    public static void main(String[] args) {
        int[] a = {5,4,8,9,3,4,6,7};
        sort(a);
        System.out.println("Arrays.toString(a) = " + Arrays.toString(a));
    }
    public static int[] sort (int[] array){
        int temp =0; //定义第三个变量
        //这层循环判断需要走多少次
        for (int i = 0; i < array.length; i++) {
            //这层循环 比较判断两个数，如果第一个数比第二个数大，则交换位置
            for (int j = 0; j < array.length-1-i; j++) {
                if (array[j+1]>array[j]){
                    temp=array[j];  //把第一个元素赋给temp
                    array[j]=array[j+1];  //把第二个元素赋给第一个元素
                    array[j+1]=temp;      //把tmmp的元素赋给第二个元素
                    //完成：大的元素在前  小的元素在后
                }
                
            }
        }
        return array;
    }
}
