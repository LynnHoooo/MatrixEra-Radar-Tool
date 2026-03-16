package com.jstyle.test2208.Util;

/**
 * Created by Administrator on 2017/4/11.
 */

public class BleData{
    byte[]value;
    String action;
    String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }
}
