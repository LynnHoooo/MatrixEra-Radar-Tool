package com.jstyle.blesdk2208.model;

/**
 * Created by Administrator on 2018/1/16.
 */

public class MyPersonalInfo extends SendData{
    int sex;//1 male,0female
    int age;
    int height;
    int weight;
    int stepLength=70;



    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getStepLength() {
        return stepLength;
    }

    public void setStepLength(int stepLength) {
        this.stepLength = stepLength;
    }

    @Override
    public String toString() {
        return "MyPersonalInfo{" +
                "sex=" + sex +
                ", age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                ", stepLength=" + stepLength +
                '}';
    }
}
