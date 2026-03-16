package com.jstyle.test2208.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.constant.DeviceKey;
import com.jstyle.blesdk2208.model.MyAutomaticHRMonitoring;
import com.jstyle.test2208.R;

import java.util.Map;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 自动心率监测设置
 */
public class Spo2SetActivity extends BaseActivity {


    @BindView(R.id.textView1)
    TextView textView1;
    @BindView(R.id.timePicker_start)
    TimePicker timePickerStart;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.timePicker_stop)
    TimePicker timePickerStop;
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.button_weekchoose)
    Button buttonWeekchoose;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.edit_minute)
    EditText editMinute;
    @BindView(R.id.textView4)
    TextView textView4;
    @BindView(R.id.ll2)
    LinearLayout ll2;



    @BindView(R.id.button_set_activitytime)
    Button buttonSetActivitytime;
    @BindView(R.id.button_get_activitytime)
    Button buttonGetActivitytime;
    @BindArray(R.array.weekarray)
    String[] weekArray;
    int[] weekPosition;
    private static final String TAG = "HeartRateSetActivity";



    private void init() {
        weekPosition = new int[7];
        timePickerStart.setIs24HourView(true);
        timePickerStop.setIs24HourView(true);
        sendValue(BleSDK.GetAutomaticSpo2Monitoring());
    }

    @OnClick({R.id.button_weekchoose, R.id.button_set_activitytime, R.id.button_get_activitytime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_weekchoose:
                showWeekDialog();
                break;
            case R.id.button_set_activitytime:
                setActivityTimeAlarm();
                break;
            case R.id.button_get_activitytime:
                sendValue(BleSDK.GetAutomaticSpo2Monitoring());
                break;
        }
    }

    AlertDialog weekDialog;

    private void showWeekDialog() {
        boolean[] checked = new boolean[7];
        for (int i = 0; i < 7; i++) {
            checked[i] = weekPosition[i] == 1;
        }
        weekDialog = new AlertDialog.Builder(this)
                .setMultiChoiceItems(weekArray, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        weekPosition[which] = isChecked ? 1 : 0;
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("Cancel", null).create();

        weekDialog.show();
    }

    private void setActivityTimeAlarm() {
        // TODO Auto-generated method stub
        if (TextUtils.isEmpty(editMinute.getText().toString())) return;
        int hourStart = timePickerStart.getCurrentHour();
        int minStart = timePickerStart.getCurrentMinute();
        int hourEnd = timePickerStop.getCurrentHour();
        int minEnd = timePickerStop.getCurrentMinute();
        int minInterval = Integer.parseInt(editMinute.getText().toString());

        int week = 0;
        for (int i = 0; i < 7; i++) {
            if (weekPosition[i] == 1)
                week += Math.pow(2, i);
        }
        MyAutomaticHRMonitoring automicHeart = new MyAutomaticHRMonitoring();
        automicHeart.setStartHour(hourStart);
        automicHeart.setStartMinute(minStart);
        automicHeart.setEndHour(hourEnd);
        automicHeart.setEndMinute(minEnd);
        automicHeart.setTime(minInterval);
        automicHeart.setWeek(week);
        sendValue(BleSDK.SetAutomaticSpo2Monitoring(automicHeart));
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        Log.e("info",maps.toString());
        String dataType= getDataType(maps);
        Map<String,String>data= getData(maps);
        switch (dataType){
            case BleConst.SetSpo2:
                showSetSuccessfulDialogInfo(dataType);
                break;
            case BleConst.GEtSpo2:

                int startHour = Integer.valueOf(data.get(DeviceKey.StartTime));
                int startMin = Integer.valueOf(data.get(DeviceKey.KHeartStartMinter));
                int endHour = Integer.valueOf(data.get(DeviceKey.EndTime));
                int endMin = Integer.valueOf(data.get(DeviceKey.KHeartEndMinter));
                String timeInterval = data.get(DeviceKey.IntervalTime);
                String week = data.get(DeviceKey.Weeks);
                String[] weekStrings = week.split("-");
                for (int i = 0; i < 7; i++) {
                    weekPosition[i] = Integer.valueOf(weekStrings[i]);
                }
                timePickerStart.setCurrentHour(startHour);
                timePickerStart.setCurrentMinute(startMin);
                timePickerStop.setCurrentHour(endHour);
                timePickerStop.setCurrentMinute(endMin);
                editMinute.setText(timeInterval);

                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spo2_set);
        ButterKnife.bind(this);
        init();
    }
}
