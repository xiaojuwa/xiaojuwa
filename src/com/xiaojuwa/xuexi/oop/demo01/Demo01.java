package com.xiaojuwa.xuexi.oop.demo01;
//Demo01 类
public class Demo01 {
    //main 方法
    public static void main(String[] args) {
        Studen.say();
        Studen studen = new Studen();
        studen.jiao();

    }
/*
* 修饰符  返回值类型  方法名（...）{
*   方法体
*   return 返回值；
* }
* */
    public String sayHello(){
        return "Hello,world";
    }

    public int max(int a,int b){
        return a>b ? a :b;//三目运算符   如果a＞b,就输出a，否则就输出b
    }
}
