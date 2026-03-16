package com.jstyle.blesdk2208.model;

import java.util.ArrayList;
import java.util.List;

public class PPI_Info {
    String  timestamp="";
    List<Integer> ppi=new ArrayList<>();

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<Integer> getPpi() {
        return ppi;
    }

    public void setPpi(List<Integer> ppi) {
        this.ppi = ppi;
    }


    @Override
    public String toString() {
        return "PPI_Info{" +
                "timestamp='" + timestamp + '\'' +
                ", ppi=" + ppi +
                '}';
    }
}
