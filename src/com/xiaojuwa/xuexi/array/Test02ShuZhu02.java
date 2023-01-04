package com.xiaojuwa.xuexi.array;
//数组的初始化
public class Test02ShuZhu02 {
    public static void main(String[] args) {
        //静态初始化:创建+赋值
        int[] a ={5,2,3,4};

        System.out.println(a[0]);//输出0号元素

        for (int i = 0; i < a.length; i++) {
            System.out.println("a[i] = " + a[i]);
        }



        //动态初始化:(包含默认初始化 int默认为0)
        int[] b = new int[10];
        b[0]=10;
        System.out.println(b[0]);//已给b[0]赋值为10，所以输出10
        System.out.println(b[1]);//b[1]没有给1号元素赋值  所以默认为0
        System.out.println(b[2]);//b[1]没有给2号元素赋值  所以默认为0
        System.out.println(b[3]=50);//随时给3号元素赋值
        System.out.println(b[3]=60);//随时改写3号元素赋值
    }
}
