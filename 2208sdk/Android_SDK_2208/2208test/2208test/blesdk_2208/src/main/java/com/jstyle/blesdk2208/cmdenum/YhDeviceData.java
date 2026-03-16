package com.jstyle.blesdk2208.cmdenum;

import java.io.Serializable;

public class YhDeviceData implements Serializable {
    int dataType;// is index
    String data="";//data object
    boolean dataEnd=false; //Is it over?

    public boolean isDataEnd() {
        return dataEnd;
    }

    public void setDataEnd(boolean dataEnd) {
        this.dataEnd = dataEnd;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getData() {
        return data==null?"":data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "YhDeviceData{" +
                "dataType=" + dataType +
                ", data=" + data  +
                ", dataEnd=" + dataEnd +
                '}';
    }
}
