package lecho.lib.hellocharts.model;

/**
 * Created by Administrator on 2018/8/24.
 */

public class RangeColor {
    int color;
    int value;

    public RangeColor(int color, int value) {
        this.color = color;
        this.value = value;
    }

    public RangeColor(){}

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
