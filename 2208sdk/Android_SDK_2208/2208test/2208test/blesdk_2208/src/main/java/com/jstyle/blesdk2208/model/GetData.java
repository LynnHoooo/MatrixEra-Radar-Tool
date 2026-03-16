package com.jstyle.blesdk2208.model;

/**
 * Created by Administrator on 2018/4/9.
 */

public class GetData extends SendData{
    public final static byte DataNum_Delete= (byte) 0x99;
    public final static int DataNum_Last=0;
    /**
     * 99删除数据，0获取数据
     */
    int dataNum=DataNum_Last;

    public int getDataNum() {
        return dataNum;
    }

    public void setDataNum(int dataNum) {
        this.dataNum = dataNum;
    }
}
