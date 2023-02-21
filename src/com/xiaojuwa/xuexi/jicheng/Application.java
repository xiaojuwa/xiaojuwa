package com.xiaojuwa.xuexi.jicheng;

public class Application {
    public static void main(String[] args) {
        Person person = new Person();
        person.say();
        Student student = new Student();
        student.say();

        Teacher teacher = new Teacher();
        teacher.say();

        System.out.println(teacher.getMoney());
        student.test1();



    }
}
