package com.jstyle.blesdk2208.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/16.
 */

public class Clock extends SendData implements Serializable {

    int number;
    int type;
    int hour;
    int minute;
    byte week;
    String content;
    boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(byte week) {
        this.week = week;
    }

    @Override
    public String toString() {
        return "Clock{" +
                "number=" + number +
                ", type=" + type +
                ", hour=" + hour +
                ", minute=" + minute +
                ", week=" + week +
                ", content='" + content + '\'' +
                ", enable=" + enable +
                '}';
    }
}
