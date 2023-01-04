package com.xiaojuwa.xuexi.array;
//数组的声明和创建

public class Test01ShuZhu01 {
    public static void main(String[] args) {

        int[] test1;//1.声明数组  （此时创建栈）
        test1 = new int[5];//2.创建数组  （此时创建堆）

        int[] test2  = new int[10];//声明并创建数组
        //数组索引从0开始
        //arrays.length获取数组长度

        test1[0]=111; //给test1数组0号元素赋值111
        test1[1]=1111;//给test1数组1号元素赋值1111
        test2[0]=222; //给test2数组0号元素赋值222
        System.out.println(test1[0]);
        System.out.println(test2[0]);

        //不同数组之间元素相加
        int add=test1[0]+test2[0];
        System.out.println("add = " + add);
        int sum=0;
        for (int i = 0; i < test1.length; i++) {
            sum = sum + test1[i];
        }
        System.out.println(sum);

    }
}
