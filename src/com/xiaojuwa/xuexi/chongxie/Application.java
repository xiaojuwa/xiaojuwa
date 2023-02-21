package com.xiaojuwa.xuexi.chongxie;

public class Application {
    public static void main(String[] args) {
        A a = new A();
        a.test();


        //父类的引用指向了子类
        B b = new A();  //子类重写了父类的方法
        b.test();
    }
}
