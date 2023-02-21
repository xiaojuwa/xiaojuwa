package com.xiaojuwa.xuexi.fengzhuang;

public class Application {
    public static void main(String[] args) {
        Student s1=new Student();
        s1.setName("老六");
        String name = s1.getName();
        s1.setId(5);
        int id = s1.getId();
        s1.setAge(99);
        System.out.println(name);
        System.out.println(id);
        System.out.println(s1.getAge());

    }
}
