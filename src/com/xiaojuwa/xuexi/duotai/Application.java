package com.xiaojuwa.xuexi.duotai;

public class Application {
    public static void main(String[] args) {
        //一个对象的实际类型是确定的
        //new Student();
        //new Person();

        //可以指向的引用类型是不确定的
        //Student 能调用的方法都是自己的或则继承父类的！
        Student s1 = new Student();
        //Person 父类 可以指向子类·，但是不能调用子类独有的方法
        Person s2 = new Student();
        Object s3 = new Student();

        s1.run();
        s2.run();//子类重写了父类的方法，就执行子类的方法

        s1.eat();
        ((Student) s2).eat();  //强制转换
        //s2.eat();  对象能执行哪些方法，主要看左边的类型，和右边关系不大
    }
}
