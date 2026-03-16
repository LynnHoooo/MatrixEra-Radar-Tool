package com.jstyle.blesdk2208.model;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/28.
 */

public class DeviceBean {
    List<Map<String,String>>dataList;
    boolean finish;

    public List<Map<String, String>> getDataList() {
        return dataList;
    }

    public void setDataList(List<Map<String, String>> dataList) {
        this.dataList = dataList;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }
}
