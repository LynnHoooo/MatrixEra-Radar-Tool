package com.jstyle.test2208.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Administrator on 2018/7/9.
 */
@Entity
public class StepDetailData {
    String address;
    String step;
    String cal;
    String distance;
    @Id
    String date;
    String MinterStep;
    @Generated(hash = 1138022180)
    public StepDetailData(String address, String step, String cal, String distance,
                          String date, String MinterStep) {
        this.address = address;
        this.step = step;
        this.cal = cal;
        this.distance = distance;
        this.date = date;
        this.MinterStep = MinterStep;
    }
    @Generated(hash = 171672306)
    public StepDetailData() {
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
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getMinterStep() {
        return this.MinterStep;
    }
    public void setMinterStep(String MinterStep) {
        this.MinterStep = MinterStep;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "StepDetailData{" +
                "address='" + address + '\'' +
                ", step='" + step + '\'' +
                ", cal='" + cal + '\'' +
                ", distance='" + distance + '\'' +
                ", date='" + date + '\'' +
                ", MinterStep='" + MinterStep + '\'' +
                '}';
    }
}
