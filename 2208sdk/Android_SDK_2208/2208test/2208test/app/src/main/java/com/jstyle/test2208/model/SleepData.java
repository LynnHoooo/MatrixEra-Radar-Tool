package com.jstyle.test2208.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Administrator on 2018/6/11.
 */
@Entity
public class SleepData {
    @Id
    String time;
    String address;
    String dateString;
    @Generated(hash = 305562222)
    public SleepData(String time, String address, String dateString) {
        this.time = time;
        this.address = address;
        this.dateString = dateString;
    }
    @Generated(hash = 1639116881)
    public SleepData() {
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getDateString() {
        return this.dateString;
    }
    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    @Override
    public String toString() {
        return "SleepData{" +
                "time='" + time + '\'' +
                ", address='" + address + '\'' +
                ", dateString='" + dateString + '\'' +
                '}';
    }
}
