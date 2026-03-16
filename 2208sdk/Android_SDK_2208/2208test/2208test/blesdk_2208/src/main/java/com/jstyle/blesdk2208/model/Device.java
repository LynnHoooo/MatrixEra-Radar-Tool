package com.jstyle.blesdk2208.model;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class Device implements Serializable{
    String name;//设备名字
    String mac;//设备的mac
    int productID;//产品id,预留
    BluetoothDevice getBluetoothDevice;//蓝牙
    boolean isconted=false;//蓝牙是否连接
    boolean IsPaired=false;//是否是配对设备
    boolean isdfu=false;//是否直接升级
    int riss=100;

    public int getRiss() {
        return riss;
    }

    public void setRiss(int riss) {
        this.riss = riss;
    }

    ScheduledThreadPoolExecutor time=null;//每一个设备轮询器

    public boolean isIsdfu() {
        return isdfu;
    }

    public void setIsdfu(boolean isdfu) {
        this.isdfu = isdfu;
    }

    public ScheduledThreadPoolExecutor getTime() {
        return time;
    }

    public void setTime(ScheduledThreadPoolExecutor time) {
        this.time = time;
    }
    public boolean isPaired() {
        return IsPaired;
    }

    public void setPaired(boolean paired) {
        IsPaired = paired;
    }

    public boolean isIsconted() {
        return isconted;
    }

    public void setIsconted(boolean isconted) {
        this.isconted = isconted;
    }

    public String getName() {
        return name==null?"":this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac==null?"":this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public BluetoothDevice getBluetoothDevice() {
        return getBluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.getBluetoothDevice = bluetoothDevice;
    }


    @Override
    public String toString() {
        return "Device{" +
                "name='" + name + '\'' +
                ", mac='" + mac + '\'' +
                ", productID=" + productID +
                ", getBluetoothDevice=" + getBluetoothDevice +
                ", isconted=" + isconted +
                '}';
    }
}
