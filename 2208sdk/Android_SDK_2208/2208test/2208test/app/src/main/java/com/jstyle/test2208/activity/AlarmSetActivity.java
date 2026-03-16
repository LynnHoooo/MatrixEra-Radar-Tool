package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.Util.ResolveUtil;
import com.jstyle.blesdk2208.model.Clock;
import com.jstyle.test2208.R;
import com.jstyle.test2208.adapter.ClockWeekAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlarmSetActivity extends BaseActivity {


    @BindView(R.id.timePicker_clock_set)
    TimePicker timePickerClockSet;

    @BindView(R.id.radio_normal)
    RadioButton radioNormal;
    @BindView(R.id.radio_Medicine)
    RadioButton radioMedicine;
    @BindView(R.id.radio_Drink)
    RadioButton radioDrink;
    @BindView(R.id.radioGroup_gender)
    RadioGroup radioGroupGender;
    @BindView(R.id.RecyclerView_alarm_set)
    RecyclerView RecyclerViewAlarmSet;
    @BindView(R.id.bt_clock_save)
    Button btClockSave;
    @BindArray(R.array.weekarray)
    String[] weekArray;
    @BindView(R.id.SwitchCompat)
    android.support.v7.widget.SwitchCompat SwitchCompat;
    private Clock clock;
    private ClockWeekAdapter clockWeekAdapter;
    private int clockId;
    private List<Clock> clockList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_set);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        clockId = getIntent().getIntExtra("clockid", -1);
        clockList = (List<Clock>) getIntent().getSerializableExtra(AlarmListActivity.KEY_CLOCK_LIST);
        timePickerClockSet.setIs24HourView(true);
        if (clockId != -1) {
            clock = clockList.get(clockId);
            int hour = clock.getHour();
            int min = clock.getMinute();
            int type = clock.getType();
            int week = clock.getWeek();
            SwitchCompat.setChecked(clock.isEnable());
            initGroup(type);
            initWeek(week);
            timePickerClockSet.setCurrentHour(hour);
            timePickerClockSet.setCurrentMinute(min);
        } else {
            initWeek(0);
            clock = new Clock();
            if (clockList == null) {
                clockList = new ArrayList<>();
                clock.setNumber(0);
            } else {
                clock.setNumber(clockList.size());
            }
            clockList.add(clock);
        }

    }

    private void initWeek(int week) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewAlarmSet.setLayoutManager(linearLayoutManager);
        int[] positions = new int[7];
        String weekString = ResolveUtil.getByteString((byte) week);
        String[] weekArrs = weekString.split("-");
        for (int i = 0; i < 7; i++) {
            if (weekArrs[i].equals("1")) {
                positions[i] = 1;
            }
        }
        clockWeekAdapter = new ClockWeekAdapter(weekArray, positions);
        RecyclerViewAlarmSet.setAdapter(clockWeekAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        RecyclerViewAlarmSet.addItemDecoration(dividerItemDecoration);
    }

    private void initGroup(int type) {
        int id = R.id.radio_normal;
        switch (type) {
            case 1:
                id = R.id.radio_normal;
                break;
            case 2:
                id = R.id.radio_Medicine;
                break;
            case 3:
                id = R.id.radio_Drink;
                break;
        }
        radioGroupGender.check(id);
    }

    private int getClockType() {
        int id = 0;
        switch (radioGroupGender.getCheckedRadioButtonId()) {
            case R.id.radio_normal:
                id = 1;
                break;
            case R.id.radio_Medicine:
                id = 2;
                break;
            case R.id.radio_Drink:
                id = 3;
                break;
        }
        return id;
    }

    private int getCheckWeek() {
        int week = 0;
        int[] positions = clockWeekAdapter.getCheckWeek();
        for (int i = 0; i < 7; i++) {
            if (positions[i] == 1) week += Math.pow(2, i);
        }
        return week;
    }

    @OnClick(R.id.bt_clock_save)
    public void onViewClicked() {

        int hour = timePickerClockSet.getCurrentHour();
        int min = timePickerClockSet.getCurrentMinute();
        int type = getClockType();
        int week = getCheckWeek();
        clock.setHour(hour);
        clock.setMinute(min);
        clock.setType(type);
        clock.setWeek((byte) week);
        clock.setEnable(SwitchCompat.isChecked());
        clock.setContent("");
        byte[] value = BleSDK.setClockData(clockList);
        int maxLength = MainActivity.phoneDataLength;
        if (value.length > maxLength) {
            int size = maxLength / 39;//一个包最多发的闹钟个数
            int length = size * 39;//最大闹钟数占用的字节
            int count = value.length % length == 0 ? value.length / length : value.length / length + 1;//需要多少个包来发送
            for (int i = 0; i < count; i++) {
                int end = length * (i + 1);
                int endLength = length;
                if (end >= value.length) endLength = value.length - length * i;
                byte[] data = new byte[endLength];
                System.arraycopy(value, length * i, data, 0, endLength);

                offerData(data);
            }
            offerData();
        } else {
            sendValue(value);
        }
        setResult(RESULT_OK);
    }

}
