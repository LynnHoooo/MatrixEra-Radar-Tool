package com.jstyle.test2208.activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.model.MyDeviceTime;
import com.jstyle.test2208.R;
import java.util.Calendar;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置/获取设备时间
 * Set / get device time
 */
public class TimeActivity extends BaseActivity {
    @BindView(R.id.info)
    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.set,R.id.get})
    public void onViewClicked(View view) {
     switch (view.getId()){
         case R.id.set://设置设备时间Set device time
             setTime();
             break;
         case R.id.get://获取设备时间 Get device time
             sendValue(BleSDK.GetDeviceTime());
             break;
     }
    }

    private void setTime() {
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);//年 YEAR
        int month=calendar.get(Calendar.MONTH)+1;//月 MONTH
        int day=calendar.get(Calendar.DAY_OF_MONTH);//日 DAY_OF_MONTH
        int hour=calendar.get(Calendar.HOUR_OF_DAY);//时 HOUR_OF_DAY
        int min=calendar.get(Calendar.MINUTE);//分 MINUTE
        int second=calendar.get(Calendar.SECOND);//秒 SECOND
        MyDeviceTime setTime=new MyDeviceTime();
        setTime.setYear(year);
        setTime.setMonth(month);
        setTime.setDay(day);
        setTime.setHour(hour);
        setTime.setMinute(min);
        setTime.setSecond(second);
        sendValue(BleSDK.SetDeviceTime(setTime));//发送 Send
    }




    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        Log.e("info",maps.toString());
        String dataType = getDataType(maps);
        switch (dataType) {
            case BleConst.SetDeviceTime://设置时间后返回的数据 Data returned after setting time
                if(null!=info){
                    info.setText(maps.toString());
                }
                break;
            case BleConst.GetDeviceTime://获取设备时间后返回的数据Get the data returned after the device time
                if(null!=info){
                    info.setText(maps.toString());
                }
                break;
        }

    }



}
