package com.jstyle.test2208.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

/**
 * Created by Administrator on 2018/7/9.
 */
@Entity
public class StepData {
    public String getShowLabel() {
        return showLabel;
    }

    public void setShowLabel(String showLabel) {
        this.showLabel = showLabel;
    }

    @Transient
    String showLabel;
    String step;
    String cal;
    String distance;
    String exerciseTime;
    String address;
    /*
    运动持续时间
     */
    String time;

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    String goal;
    @Id
    String date;
    @Generated(hash = 1475266610)
    public StepData(String step, String cal, String distance, String exerciseTime,
                    String address, String time, String goal, String date) {
        this.step = step;
        this.cal = cal;
        this.distance = distance;
        this.exerciseTime = exerciseTime;
        this.address = address;
        this.time = time;
        this.goal = goal;
        this.date = date;
    }

    @Generated(hash = 90761876)
    public StepData() {
    }
    public String getStep() {
        return this.step;
    }
    public void setStep(String step) {
        this.step = step;
    }
    public String getCal() {
        return this.cal;
    }
    public void setCal(String cal) {
        this.cal = cal;
    }
    public String getDistance() {
        return this.distance;
    }
    public void setDistance(String distance) {
        this.distance = distance;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getExerciseTime() {
        return exerciseTime;
    }

    public void setExerciseTime(String exerciseTime) {
        this.exerciseTime = exerciseTime;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "StepData{" +
                "showLabel='" + showLabel + '\'' +
                ", step='" + step + '\'' +
                ", cal='" + cal + '\'' +
                ", distance='" + distance + '\'' +
                ", exerciseTime='" + exerciseTime + '\'' +
                ", address='" + address + '\'' +
                ", time='" + time + '\'' +
                ", goal='" + goal + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
