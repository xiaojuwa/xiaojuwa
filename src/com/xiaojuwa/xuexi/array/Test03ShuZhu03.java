package com.xiaojuwa.xuexi.array;
//数组的使用
public class Test03ShuZhu03 {
    public static void main(String[] args) {
        int[] arrays = {1,2,7,4,5};
        
        //打印全部的数组元素
        for (int i = 0; i < arrays.length; i++) {
            System.out.println("arrays[i] = " + arrays[i]);
        }

        System.out.println("-----------------");

        //计算所有元素的和
        int sum = 0;
        for (int i = 0; i < arrays.length; i++) {
            sum += arrays[i];
        }
        System.out.println("sum = " + sum);

        System.out.println("-----------------");

        //查找最大元素
        int max = arrays[0];//设0号元素为最大值
        for (int i = 1; i < arrays.length; i++) {
            if (arrays[i]>max){//遍历判断arrays数组 第i号元素是否大于前面设的0号元素
                max = arrays[i];//如果大于则把第i号元素设置设为最大值
            }
        }
        System.out.println("max = " + max);
    }
}
