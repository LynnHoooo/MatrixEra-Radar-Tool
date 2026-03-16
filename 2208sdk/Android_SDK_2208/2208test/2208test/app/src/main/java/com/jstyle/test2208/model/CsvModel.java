package com.jstyle.test2208.model;

public class CsvModel {
    String date="--";
    String heartRate="--";
    String step="--";;
    String cal="--";;
    String distance="--";
    String SleepQuality="--";

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getCal() {
        return cal;
    }

    public void setCal(String cal) {
        this.cal = cal;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSleepQuality() {
        return SleepQuality;
    }

    public void setSleepQuality(String sleepQuality) {
        SleepQuality = sleepQuality;
    }

    @Override
    public String toString() {
        return "CsvModel{" +
                "date='" + date + '\'' +
                ", heartRate='" + heartRate + '\'' +
                ", step='" + step + '\'' +
                ", cal='" + cal + '\'' +
                ", distance='" + distance + '\'' +
                ", SleepQuality='" + SleepQuality + '\'' +
                '}';
    }
}
