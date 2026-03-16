package com.jstyle.test2208.activity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.model.SportPeriod;
import com.jstyle.test2208.R;
import com.jstyle.test2208.adapter.MyWheelAdapter;
import com.jstyle.test2208.views.WheelView;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorkOutReminderActivity extends BaseActivity {
    @BindView(R.id.button6)
    Button button6;
    @BindView(R.id.button_starttime)
    Button buttonInterval;
    @BindView(R.id.view_start)
    View viewStart;
    @BindView(R.id.button_end)
    Button buttonEnd;
    @BindView(R.id.button_endtime)
    Button buttonEndtime;
    @BindView(R.id.view_endtime)
    View viewEndtime;
    @BindView(R.id.button_8)
    Button button8;
    @BindView(R.id.button_interval)
    Button buttonStarttime;
    @BindView(R.id.view_interval)
    View viewInterval;
    @BindView(R.id.tv_week_title)
    TextView tvWeekTitle;
    @BindView(R.id.tv_week_content)
    Button tvWeekContent;
    @BindView(R.id.bt_activityclock_confim)
    Button btActivityclockConfim;
    private AlertDialog weekDialog;
    private int [] weekPosition=new int[7];
    @BindArray(R.array.week)
    String[]weekArray;
    private TimePickerDialog timePickerDialog;

    private AlertDialog intervalDialog;
    private WheelView wheelView;
    private MyWheelAdapter wheelAdapter;
    private List<String> workoutList;
    private List<String> everyList;
    int mode=0;
    int mode_workOut=0;
    int mode_every=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_out_reminder);
        ButterKnife.bind(this);
        init();
    }
    private void init() {
        workoutList=getListData(1,1,61);
        everyList=getListData(1,1,8);
    }
    @OnClick({R.id.tv_week_content,R.id.tv_week_title, R.id.button_starttime, R.id.button_endtime,
            R.id.button_interval, R.id.bt_activityclock_confim,R.id.getinfo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.getinfo:
                sendValue(BleSDK.getWorkOutReminder());
                break;
            case R.id.button_starttime:
                mode=mode_workOut;
                showIntervalDialog();
                break;
            case R.id.button_endtime:
                mode=mode_every;
                showIntervalDialog();
                break;
            case R.id.button_interval:
                showTimeDialog();
                break;

            case R.id.bt_activityclock_confim:
                setActivityClock();
                break;
            case R.id.tv_week_content:
            case R.id.tv_week_title:
                showWeekDialog();
                break;
        }
    }
    private void showWeekDialog() {
        boolean[]checked=new boolean[7];
        for(int i=0;i<7;i++){
            checked[i]=weekPosition[i]==1;
        }
        weekDialog=new AlertDialog.Builder(this,R.style.alertdialog_week)
                .setMultiChoiceItems(weekArray,checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        weekPosition[which]=isChecked?1:0;
                    }
                })
                .setPositiveButton("ok" ,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWeekText();
                    }
                }).setNegativeButton(getString(R.string.cancel),null).create();
        weekDialog.show();
        //ScreenUtils.setDialogButtonTextColor(weekDialog);
    }
    private void showWeekText(){
        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<weekPosition.length;i++){
            if(weekPosition[i]==1){
                stringBuffer.append(weekArray[i]).append(", ");
            }
        }
        String weekString=stringBuffer.toString();
        if(!TextUtils.isEmpty(weekString)){
            weekString= weekString.substring(0,weekString.length()-1);
        }
        tvWeekContent.setText(weekString);
    }
    private void showTimeDialog() {

        String text=buttonStarttime.getText().toString();
        int hour=0;
        int min=0;
        if(!TextUtils.isEmpty(text)){
            hour=Integer.valueOf(text.split(":")[0]);
            min=Integer.valueOf(text.split(":")[1]);
        }
        timePickerDialog=new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time=String.format("%1$02d:%2$02d",hourOfDay,minute);

                buttonStarttime.setText(time);


            }
        },hour,min,true);
        timePickerDialog.show();

    }

    private void showIntervalDialog() {
        if(intervalDialog==null){
            intervalDialog=new AlertDialog.Builder(this,R.style.alertdialog_week)
                    .setTitle("Interval:")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int current = wheelView.getCurrentItem();
                            String currentInterval = mode==mode_every?everyList.get(current):workoutList.get(current);
                           // buttonInterval.setText(currentInterval+getString(R.string.mins));
                            if(mode==mode_every){
                                buttonEndtime.setText(currentInterval+" days");
                            }else {
                                buttonInterval.setText(currentInterval+" mins");
                            }
                        }
                    })
                    .setView(R.layout.dialog_activity_interval)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create();
        }
        intervalDialog.show();
        wheelView=(WheelView)intervalDialog.findViewById(R.id.WheelView_interval) ;
        wheelAdapter = new MyWheelAdapter(this, mode==mode_every?everyList:workoutList, 0, 24, 14);
        wheelView.setViewAdapter(wheelAdapter);
        //  ScreenUtils.setDialogButtonTextColor(intervalDialog);

    }
    private List<String> getListData(int start, int interval, int count) {
        List<String> list = new ArrayList<>();
        for (int i = start; i < count; i++) {
            list.add(String.valueOf(i*interval));
        }
        return list;
    }


    private void setActivityClock() {

        String startTime=buttonStarttime.getText().toString();
        String mins=buttonInterval.getText().toString();
        String dayString=buttonEndtime.getText().toString();
        if(TextUtils.isEmpty(startTime)||TextUtils.isEmpty(mins)||TextUtils.isEmpty(dayString))return;
        int hourStart = Integer.parseInt(startTime.split(":")[0]);
        int minStart = Integer.parseInt(startTime.split(":")[1]);

        int minInterval = Integer.parseInt(mins.split("mins")[0].trim());
        int days = Integer.parseInt(dayString.split("days")[0].trim());
        int week = 0;
        for (int  i =0;i<7;i++) {
            if(weekPosition[i]==1)
                week += Math.pow(2, i);
        }
        SportPeriod sportPeriod = new SportPeriod();
        sportPeriod.setStartHour(hourStart);
        sportPeriod.setStartMinute(minStart);

        sportPeriod.setIntervalTime(minInterval);
        sportPeriod.setDays(days);
        sportPeriod.setWeek(week);
        sportPeriod.setEnable(true);

        //sendValue(BleSDK.setWorkOutReminder(sportPeriod));
    }
    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType = getDataType(maps);
        Log.e("info",maps.toString());
        switch (dataType) {
            case BleConst.CMD_Set_WorkOutReminder:
            case BleConst.CMD_Get_WorkOutReminder:
                showDialogInfo(maps.toString());
                break;
        }
    }

}
