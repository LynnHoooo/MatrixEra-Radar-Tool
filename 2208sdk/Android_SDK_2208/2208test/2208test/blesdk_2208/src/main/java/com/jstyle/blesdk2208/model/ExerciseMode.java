package com.jstyle.blesdk2208.model;

/**
 * Created by Administrator on 2018/8/2.
 */

public class ExerciseMode extends SendData {
    public static final int Status_START=1;
    public static final int Status_PAUSE=2;
    public static final int Status_CONTUINE=3;
    public static final int Status_FINISH=4;

    public static final int Mode_RUN=0;
    public static final int Mode_CYCLING=1;
    public static final int Mode_BADMINTON=2;
    public static final int Mode_FOOTBALL=3;
    public static final int Mode_TENNIS=4;
    public static final int Mode_YOGA=5;
    public static final int Mode_BREATH=6;
    public static final int Mode_DANCE=7;
    public static final int Mode_BASKETBALL=8;
    public static final int Mode_Walk=9;
    public static final int Mode_workout=10;
    public static final int Mode_Cricket=11;
    public static final int Mode_Hiking=12;
    public static final int Mode_Aerobics=13;
    public static final int Mode_PingPong=14;
    public static final int Mode_RopeJump=15;
    public static final int Mode_Situps=16;
    public static final int Volleyball=17;


    public static int[]modes=new int[]{Mode_RUN,Mode_CYCLING,Mode_BADMINTON,Mode_FOOTBALL,
            Mode_TENNIS,Mode_YOGA,Mode_BREATH,Mode_DANCE,Mode_BASKETBALL,Mode_Walk,Mode_workout
    ,Mode_Cricket,Mode_Hiking,Mode_Aerobics,Mode_PingPong, Mode_RopeJump,Mode_Situps,Volleyball};
    int exerciseMode;
    int enableStatus;
    public int getExerciseMode(int position){
        return modes[position];
    }
    public int getExerciseMode() {
        return exerciseMode;
    }

    public void setExerciseMode(int exerciseMode) {
        this.exerciseMode = exerciseMode;
    }

    public int getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(int enableStatus) {
        this.enableStatus = enableStatus;
    }
}
