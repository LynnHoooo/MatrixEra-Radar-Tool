package com.jstyle.test2208.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.constant.DeviceKey;
import com.jstyle.blesdk2208.model.MySedentaryReminder;
import com.jstyle.test2208.R;
import java.util.Map;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * 设置/获取运动提醒时段（Set / get motion reminder period）
 */
public class ActivityAlarmSetActivity extends BaseActivity {
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
    @BindView(R.id.textVew)
    TextView textVew;
    @BindView(R.id.edit_step)
    EditText editStep;
    @BindView(R.id.textVie)
    TextView textVie;
    @BindView(R.id.ll3)
    LinearLayout ll3;
    @BindView(R.id.button_set_activitytime)
    Button buttonSetActivitytime;
    @BindView(R.id.button_get_activitytime)
    Button buttonGetActivitytime;
    @BindView(R.id.radioGroup_gender)
    RadioGroup radioGroup_gender;
    @BindArray(R.array.weekarray)
     String[]weekArray;
    int[] weekPosition;
    private static final String TAG = "ActivityAlarmSetActivit";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set2);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        weekPosition=new int[7];
        timePickerStart.setIs24HourView(true);
        timePickerStop.setIs24HourView(true);
        sendValue(BleSDK.GetSedentaryReminder());
    }

    @OnClick({R.id.button_weekchoose, R.id.button_set_activitytime, R.id.button_get_activitytime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_weekchoose://星期选择Week selection
                showWeekDialog();
                break;
            case R.id.button_set_activitytime://设置提醒时段 Set reminder period
                setActivityTimeAlarm();
                break;
            case R.id.button_get_activitytime://获取提醒时段 Get reminder period
                sendValue(BleSDK.GetSedentaryReminder());
                break;
        }
    }

    AlertDialog weekDialog;
    private void showWeekDialog() {
        boolean[]checked=new boolean[7];
        for(int i=0;i<7;i++){
            checked[i]=weekPosition[i]==1;
        }
            weekDialog=new AlertDialog.Builder(this)
                    .setMultiChoiceItems(weekArray,checked, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                           weekPosition[which]=isChecked?1:0;
                        }
                    })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setNegativeButton("Cancel",null).create();

        weekDialog.show();
    }

    private void setActivityTimeAlarm() {
        if (TextUtils.isEmpty(editMinute.getText().toString())
                || TextUtils.isEmpty(editStep.getText().toString())) return;
        int hourStart = timePickerStart.getCurrentHour();
        int minStart = timePickerStart.getCurrentMinute();
        int hourEnd = timePickerStop.getCurrentHour();
        int minEnd = timePickerStop.getCurrentMinute();
        int minInterval = Integer.parseInt(editMinute.getText().toString());
        int minStep = Integer.parseInt(editStep.getText().toString());
        int week = 0;
        for (int  i =0;i<7;i++) {
            if(weekPosition[i]==1)
            week += Math.pow(2, i);
        }
        MySedentaryReminder sportPeriod = new MySedentaryReminder();
        sportPeriod.setStartHour(hourStart);//提醒开始小时 Reminder start hour
        sportPeriod.setStartMinute(minStart);//提醒开始分钟Reminder start minutes
        sportPeriod.setEndHour(hourEnd);//提醒结束小时 Reminder hours end
        sportPeriod.setEndMinute(minEnd);//提醒结束分钟 Reminder end minutes
        sportPeriod.setIntervalTime(minInterval);//持续时间（分钟） Duration (minutes)
        sportPeriod.setLeastStep(minStep);//最少运动步数 Minimum movement steps
        sportPeriod.setWeek(week);//星期选中详情 Week selection details
        sportPeriod.setEnable(radioGroup_gender.getCheckedRadioButtonId() == R.id.open);// open or close
        sendValue(BleSDK.SetSedentaryReminder(sportPeriod));//发送给设备 Send to device
    }

    @Override
    public void dataCallback(Map<String, Object> data) {
        super.dataCallback(data);
        String dataType= getDataType(data);
        Map<String,String>maps= getData(data);
        switch (dataType){
            case BleConst.SetSedentaryReminder://发送设置后数据详情 Send data details after setting
                showSetSuccessfulDialogInfo(dataType);
                break;
            case BleConst.GetSedentaryReminder://获取运动提醒时段数据 Get motion reminder period data
                int startHour = Integer.valueOf(maps.get(DeviceKey.StartTimeHour));
                int startMin = Integer.valueOf(maps.get(DeviceKey.StartTimeMin));
                int endHour = Integer.valueOf(maps.get(DeviceKey.EndTimeHour));
                int endMin = Integer.valueOf(maps.get(DeviceKey.EndTimeMin));
                int KSportswitch = Integer.valueOf(maps.get(DeviceKey.KSportswitch));
                String timeInterval = maps.get(DeviceKey.IntervalTime);
                String week = maps.get(DeviceKey.Week);
                String step = maps.get(DeviceKey.LeastSteps);
                String[]weekStrings=week.split("-");
                for(int i=0;i<7;i++){
                    weekPosition[i]=Integer.valueOf(weekStrings[i]);
                }
                timePickerStart.setCurrentHour(startHour);
                timePickerStart.setCurrentMinute(startMin);
                timePickerStop.setCurrentHour(endHour);
                timePickerStop.setCurrentMinute(endMin);
                radioGroup_gender.check(1==KSportswitch?R.id.open:R.id.close);
                editMinute.setText(timeInterval);
                editStep.setText(step);
                break;
        }
    }
}
