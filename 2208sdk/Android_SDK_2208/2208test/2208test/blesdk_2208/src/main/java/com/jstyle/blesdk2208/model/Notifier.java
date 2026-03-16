package com.jstyle.blesdk2208.model;

/**
 * Created by Administrator on 2018/1/16.
 */

public class Notifier extends SendData{
    public static final  int Data_Tel=0;
    public static final  int Data_Sms=1;
    public static final  int Data_WeChat=2;
    public static final  int Data_Facebook=3;
    public static final  int Data_Instagram=4;
    public static final  int Data_Skype=5;
    public static final  int Data_Telegram=6;
    public static final  int Data_Twitter=7;
    public static final  int Data_Vkclient=8;
    public static final  int Data_WhatApp=9;
    public static final  int Data_QQ=10;
    public static final  int Data_IN=11;
    public static final  int Data_Stop_Tel=0xff;

    int type;
    String info="";
    String title="";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "Notifier{" +
                "type=" + type +
                ", info='" + info + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
