package lecho.lib.hellocharts.model;

import android.graphics.Color;

/**
 * Created by Administrator on 2018/9/6.
 */

public class BpValue {
    String date;
    String showValue;
    Object obj;
    //精神
    int maxjs;
    int minjs;
    //疲劳
    int maxpl;
    int minpl;

    //兴奋
    int maxxl;
    int minxl;

    //by
    int maxby;
    int minby;

    public int getMaxby() {
        return maxby;
    }

    public void setMaxby(int maxby) {
        this.maxby = maxby;
    }

    public int getMinby() {
        return minby;
    }

    public void setMinby(int minby) {
        this.minby = minby;
    }

    public int getMaxxl() {
        return maxxl;
    }

    public void setMaxxl(int maxxl) {
        this.maxxl = maxxl;
    }

    public int getMinxl() {
        return minxl;
    }

    public void setMinxl(int minxl) {
        this.minxl = minxl;
    }

    public int getMaxpl() {
        return maxpl;
    }

    public void setMaxpl(int maxpl) {
        this.maxpl = maxpl;
    }

    public int getMinpl() {
        return minpl;
    }

    public void setMinpl(int minpl) {
        this.minpl = minpl;
    }

    public int getMaxjs() {
        return maxjs;
    }

    public void setMaxjs(int maxjs) {
        this.maxjs = maxjs;
    }

    public int getMinjs() {
        return minjs;
    }

    public void setMinjs(int minjs) {
        this.minjs = minjs;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShowValue() {
        return showValue==null?"":showValue;
    }

    public void setShowValue(String showValue) {
        this.showValue = showValue;
    }

    public final static int defaultHighValueColor = Color.parseColor("#B2C52C");
    public final static int defaultLowValueColor = Color.parseColor("#06D095");
    public final static int defaultErrorValueColor = Color.parseColor("#e9603c");
    int HighValueColor = defaultHighValueColor;
    int LowValueColor = defaultLowValueColor;
    int ErrorValueColor = defaultErrorValueColor;

    int highValue;
    int lowValue;

    int errorLowValue=0;
    int errorHighValue=200;
    int bgColor=Color.parseColor("#f4f5f3");

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public BpValue(int highValue, int lowValue) {
        this.highValue = highValue;
        this.lowValue = lowValue;
    }

    public int getHighValue() {
        return highValue;
    }

    public void setHighValue(int highValue) {
        this.highValue = highValue;
    }

    public int getLowValue() {
        return lowValue;
    }

    public void setLowValue(int lowValue) {
        this.lowValue = lowValue;
    }

    public int getHighValueColor() {
        return highValue>=errorHighValue?ErrorValueColor:HighValueColor;
    }

    public void setHighValueColor(int highValueColor) {
        HighValueColor = highValueColor;
    }

    public int getLowValueColor() {
        return lowValue>=errorLowValue?LowValueColor:ErrorValueColor;
    }

    public void setLowValueColor(int lowValueColor) {
        LowValueColor = lowValueColor;
    }

    public int getErrorValueColor() {
        return ErrorValueColor;
    }

    public void setErrorValueColor(int errorValueColor) {
        ErrorValueColor = errorValueColor;
    }

    public int getBgTop(){
        if(highValue==0)return errorHighValue;
        return highValue<=errorHighValue?errorHighValue:highValue;
    }
    public int getBgBottom(){
        if(lowValue==0)return  errorLowValue;
        return lowValue<=errorLowValue?lowValue:errorLowValue;
    }
}
