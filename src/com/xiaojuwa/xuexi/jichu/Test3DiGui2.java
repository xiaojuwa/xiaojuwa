package com.xiaojuwa.xuexi.jichu;

import java.util.Arrays;

public class Test3DiGui2 {
    //阶乘 例：5！=5*4*3*2*1
    public static void main(String[] args) {
        System.out.println(f(4));
    }
    public static int f(int n){
        if (n==1){
            return 1;
        }else {
            return n*f(n-1);
            //讲解：例n=3 则第一次为“3*f(3-1)”,此时传入“f(2)”
            //此时，n=2 则第二次为“2*f(2-1)”，此时传入“f(1)”
            //此时，n=1 则第三次返回n=1 此时到尽头
            //此时知道f(1)=1则n=2时 为“2*f(1)” 结果为2
            //此时知道f(2)=2则n=3时 为“3*f(2)” 结果为6


        }
    }

}
