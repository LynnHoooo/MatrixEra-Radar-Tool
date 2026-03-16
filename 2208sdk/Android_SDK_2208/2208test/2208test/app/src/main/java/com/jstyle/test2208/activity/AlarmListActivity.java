package com.jstyle.test2208.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.constant.DeviceKey;
import com.jstyle.blesdk2208.model.Clock;
import com.jstyle.test2208.R;
import com.jstyle.test2208.adapter.ClockAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 闹钟列表 （Alarm list）
 */
public class AlarmListActivity extends BaseActivity implements ClockAdapter.onClockItemClickListener {
    byte ModeStart=0x00;       //开始获取数据 start getting data
    byte ModeContinue=0x02;    //继续读取数据 continue reading data
    byte ModeDelete=(byte) 0x99;//删除数据  delete data

    @BindView(R.id.RecyclerView_alarm)
    RecyclerView RecyclerViewAlarm;
    @BindArray(R.array.weekarray)
    String[] weekArray;
    @BindView(R.id.bt_add)
    Button btAdd;
    private ClockAdapter clockAdapter;
    List<Map<String, String>> list = new ArrayList<>();
    public static final String KEY_CLOCK_LIST = "CLOCK_LIST";
    private int REQUEST_Clock = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set);
        ButterKnife.bind(this);
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewAlarm.setLayoutManager(linearLayoutManager);
        clockAdapter = new ClockAdapter(this);
        RecyclerViewAlarm.setAdapter(clockAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        RecyclerViewAlarm.addItemDecoration(dividerItemDecoration);
        sendValue(BleSDK.GetAlarmClock(ModeStart));
    }


    /**
     *编辑闹钟
     * Edit alarm clock
     * @param clock
     */
    @Override
    public void onItemClick(Clock clock) {
        Intent intent = new Intent(this, AlarmSetActivity.class);
        intent.putExtra("clockid", clock.getNumber());
        intent.putExtra(KEY_CLOCK_LIST, (Serializable) clockAdapter.getClockList());
        startActivityForResult(intent, REQUEST_Clock);
    }

    /**
     * 删除闹钟
     * @param clock
     * Delete alarm clock
     */
    @Override
    public void onDelete(Clock clock) {
        updateClock(clockAdapter.getClockList());
    }


    /**
     * 更新闹钟
     * @param clockList
     * Update alarm clock
     */
    @Override
    public void onUpdate(List<Clock> clockList) {
        updateClock(clockList);
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        Log.e("ddddd",maps.toString());
        String dataType = getDataType(maps);
        boolean finish = getEnd(maps);
        switch (dataType) {
            case BleConst.Delete_AlarmClock://删除闹钟后返回结果 The result is returned after deleting the alarm clock
                showDialogInfo(maps.toString());
                break;
            case BleConst.GetAlarmClock://获取闹钟数据 Get alarm data
                List<Map<String, String>> mapValue = ((List<Map<String, String>>) maps.get(DeviceKey.Data));
                for (Map<String, String> map : mapValue) {
                    String hour = String.format("%02d", Integer.valueOf(map.get(DeviceKey.ClockTime)));
                    String min = String.format("%02d", Integer.valueOf(map.get(DeviceKey.KAlarmMinter)));
                    String content = map.get(DeviceKey.KAlarmContent);
                    int clockType = Integer.parseInt(map.get(DeviceKey.ClockType));
                    int id = Integer.parseInt(map.get(DeviceKey.KAlarmId));
                    int enable = Integer.parseInt(map.get(DeviceKey.OpenOrClose));
                    String week = map.get(DeviceKey.Week);
                    final Clock clock = new Clock();
                    clock.setContent(content);
                    clock.setNumber(id);
                    clock.setEnable(enable == 1);
                    clock.setHour(Integer.parseInt(hour));
                    clock.setMinute(Integer.parseInt(min));
                    clock.setType(clockType);
                    byte weekByte = 0;
                    String[] weekString = week.split("-");
                    for (int i = 0; i < 7; i++) {
                        String weekEnable = weekString[i];
                        if (weekEnable.equals("1")) {
                            weekByte += Math.pow(2, i);
                        }
                    }
                    clock.setWeek(weekByte);
                    clockAdapter.setData(weekArray, clock);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_Clock && resultCode == RESULT_OK) {
            clockAdapter.clear();
            sendValue(BleSDK.GetAlarmClock(ModeStart));
        }
    }

    private void updateClock(List<Clock> clockList) {

        for (int i = 0; i < clockList.size(); i++) {
            Clock clock = clockList.get(i);
            clock.setNumber(i);
        }
        byte[] value = clockList.size() == 0 ? BleSDK.deleteAllClock() : BleSDK.setClockData(clockList);
        int maxLength = MainActivity.phoneDataLength;
        if (value.length > maxLength) {
            int size = maxLength / 38;//一个包最多发的闹钟个数
            int length = size * 38;//最大闹钟数占用的字节
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
    }

    @OnClick({R.id.bt_add, R.id.bt_edit})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.bt_add:
                List<Clock> list=clockAdapter.getClockList();
                if(list.size()==10){
                    showToast("No more alarm clock");
                    return;
                }
                Intent intent = new Intent(this, AlarmSetActivity.class);
                intent.putExtra(KEY_CLOCK_LIST, (Serializable) clockAdapter.getClockList());
                startActivityForResult(intent, REQUEST_Clock);

                break;
            case R.id.bt_edit:
                clockAdapter.enableDelete();
                break;
        }

    }
}
