package com.xiaojuwa.xuexi.fengzhuang;

//类  private:私有
public class Student {
    //名字
    private String name;
    //学号
    private int id;
    //性别
    private int age;
    private String sex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 0 || age > 120){ //不合法
           this.age = 3;
        }else {
           this.age = age;
        }

    }
}
