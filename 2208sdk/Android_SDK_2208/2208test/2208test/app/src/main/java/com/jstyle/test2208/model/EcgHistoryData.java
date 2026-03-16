package com.jstyle.test2208.model;

/**
 * Created by Administrator on 2018/8/25.
 */

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

@Entity(indexes = {
        @Index(value = "time DESC, address DESC", unique = true)
})
public class EcgHistoryData {
    String userId="";
    String time="";
    int hrv;
    int pressure;
    int highBloodPressure;
    int lowBloodPressure;
    int heartRate;
    int bloodValue=1;
    int breathValue;
    int moodValue;
    long fileName;
    String address="";
    String arrayECGData="";

    @Generated(hash = 286315197)
    public EcgHistoryData(String userId, String time, int hrv, int pressure,
            int highBloodPressure, int lowBloodPressure, int heartRate,
            int bloodValue, int breathValue, int moodValue, long fileName,
            String address, String arrayECGData) {
        this.userId = userId;
        this.time = time;
        this.hrv = hrv;
        this.pressure = pressure;
        this.highBloodPressure = highBloodPressure;
        this.lowBloodPressure = lowBloodPressure;
        this.heartRate = heartRate;
        this.bloodValue = bloodValue;
        this.breathValue = breathValue;
        this.moodValue = moodValue;
        this.fileName = fileName;
        this.address = address;
        this.arrayECGData = arrayECGData;
    }

    @Generated(hash = 678880532)
    public EcgHistoryData() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getHrv() {
        return hrv;
    }

    public void setHrv(int hrv) {
        this.hrv = hrv;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHighBloodPressure() {
        return highBloodPressure;
    }

    public void setHighBloodPressure(int highBloodPressure) {
        this.highBloodPressure = highBloodPressure;
    }

    public int getLowBloodPressure() {
        return lowBloodPressure;
    }

    public void setLowBloodPressure(int lowBloodPressure) {
        this.lowBloodPressure = lowBloodPressure;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getBloodValue() {
        return bloodValue;
    }

    public void setBloodValue(int bloodValue) {
        this.bloodValue = bloodValue;
    }

    public int getBreathValue() {
        return breathValue;
    }

    public void setBreathValue(int breathValue) {
        this.breathValue = breathValue;
    }

    public int getMoodValue() {
        return moodValue;
    }

    public void setMoodValue(int moodValue) {
        this.moodValue = moodValue;
    }

    public long getFileName() {
        return fileName;
    }

    public void setFileName(long fileName) {
        this.fileName = fileName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArrayECGData() {
        return arrayECGData;
    }

    public void setArrayECGData(String arrayECGData) {
        this.arrayECGData = arrayECGData;
    }

    @Override
    public String toString() {
        return "EcgHistoryData{" +
                "userId='" + userId + '\'' +
                ", time='" + time + '\'' +
                ", hrv=" + hrv +
                ", pressure=" + pressure +
                ", highBloodPressure=" + highBloodPressure +
                ", lowBloodPressure=" + lowBloodPressure +
                ", heartRate=" + heartRate +
                ", bloodValue=" + bloodValue +
                ", breathValue=" + breathValue +
                ", moodValue=" + moodValue +
                ", fileName=" + fileName +
                ", address='" + address + '\'' +
                ", arrayECGData='" + arrayECGData + '\'' +
                '}';
    }
}

