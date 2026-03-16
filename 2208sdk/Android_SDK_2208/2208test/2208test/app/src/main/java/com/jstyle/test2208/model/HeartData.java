package com.jstyle.test2208.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Administrator on 2018/7/25.
 */
@Entity
public class HeartData {
    @Id
    String time;
    int  heart;
    String address;



    @Generated(hash = 140683494)
    public HeartData(String time, int heart, String address) {
        this.time = time;
        this.heart = heart;
        this.address = address;
    }
    @Generated(hash = 241019369)
    public HeartData() {
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public int getHeart() {
        return this.heart;
    }
    public void setHeart(int heart) {
        this.heart = heart;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "HeartData{" +
                "time='" + time + '\'' +
                ", heart=" + heart +
                ", address='" + address + '\'' +
                '}';
    }
}
