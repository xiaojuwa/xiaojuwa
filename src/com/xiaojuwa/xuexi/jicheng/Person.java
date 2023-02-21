package com.xiaojuwa.xuexi.jicheng;

public class Person {
    public Person() {
        System.out.println("person无参构造执行");
    }

//public
    //protected
    //default
    //private


    //在java中，所有的类，都默认直接或间接继承object类


    private int money = 10_0000_0000;
    protected String name = "小明";


    public void say(){
        System.out.println("说话了");
    }

    public int getMoney() {
        return money;
    }
    public void print(){
        System.out.println("Person");
    }
    public void setMoney(int money) {
        this.money = money;
    }
}
