package com.xiaojuwa.xuexi.array;

import java.util.Arrays;
import java.util.Random;

public class HouZiPaiXu {
    public static void main(String[] args) {
        int[] a = {1877,566,85456,3676,435346,97894,7899};
        monkeySort(a);
    }
    public static boolean isSort(int[] arr){
        for (int i = 0; i < arr.length-1; i++) {
            if(arr[i+1]<arr[i]){
                return false;
            }
        }
        return true;
    }

    public static void monkeySort(int[] arr){
        int count=0;
        Random random=new Random();
        do{
            /* 从待排序数组右边随机抽取一位数与左边进行交换*/
            for (int i = 0; i < arr.length-1; i++) {
                count++;
                int t=(random.nextInt(arr.length-i)+i);
                int tmp=arr[i];
                arr[i]=arr[t];
                arr[t]=tmp;
            }
        }while(!isSort(arr));

        System.out.println("算了"+count+"次，得到结果"+ Arrays.toString(arr));
    }
}
