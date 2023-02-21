package com.xiaojuwa.xuexi.jicheng;
//学生继承了人的类型

public class Student extends Person {
    public Student() {
        System.out.println("Student无参构造执行");
    }

    protected String name = "小橘";
    public void test(String name){
        System.out.println(name);
        System.out.println(this.name);
        System.out.println(super.name);
    }
    public void print(){
        System.out.println("Student");
    }
     public void test1(){
        print();
        this.print();
        super.print();
    }

}
