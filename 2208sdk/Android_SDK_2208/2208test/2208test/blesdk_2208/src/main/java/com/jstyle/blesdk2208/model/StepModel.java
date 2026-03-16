package com.jstyle.blesdk2208.model;

/**
 * Created by Administrator on 2018/4/9.
 */

public class StepModel extends SendData{
    boolean stepState;//true开启实时计步，false停止实时计步

    public boolean isStepState() {
        return stepState;
    }

    public void setStepState(boolean stepState) {
        this.stepState = stepState;
    }


    @Override
    public String toString() {
        return "StepModel{" +
                "stepState=" + stepState +
                '}';
    }
}
