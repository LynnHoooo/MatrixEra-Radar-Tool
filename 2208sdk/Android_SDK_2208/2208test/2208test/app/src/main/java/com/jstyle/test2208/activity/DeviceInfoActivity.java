package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.model.MyDeviceInfo;
import com.jstyle.test2208.R;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 *设置/获取手环基本参数 (Set / obtain basic parameters of Bracelet)
 */
public class DeviceInfoActivity extends BaseActivity {
    @BindView(R.id.radio_12h)
    RadioButton radio12h;
    @BindView(R.id.radio_24h)
    RadioButton radio24h;
    @BindView(R.id.radioGroup1)
    RadioGroup radioGroupTimeMode;
    @BindView(R.id.radio_km)
    RadioButton radioKm;
    @BindView(R.id.radio_mile)
    RadioButton radioMile;
    @BindView(R.id.radioGroup3)
    RadioGroup radioGroup_distanceUnit;
    @BindView(R.id.SwitchCompat_hand)
    SwitchCompat SwitchCompatHand;
    @BindView(R.id.left_or_light)
    SwitchCompat left_or_light;
    @BindView(R.id.Night_mode)
    SwitchCompat Night_mode;
    @BindView(R.id.Social_distance_switch)
    SwitchCompat Social_distance_switch;
    @BindView(R.id.Chinese_English_switch)
    SwitchCompat Chinese_English_switch;
    @BindView(R.id.button_deviceinfo_set)
    Button buttonDeviceinfoSet;
    @BindView(R.id.button_deviceinfo_get)
    Button buttonDeviceinfoGet;
    @BindView(R.id.Dial_switch)
    EditText Dial_switch;
    @BindView(R.id.radio_temp_c)
    RadioButton radioTempC;
    @BindView(R.id.radio_temp_f)
    RadioButton radioTempF;
    @BindView(R.id.radioGroup_tempUnit)
    RadioGroup radioGroupTempUnit;
    @BindView(R.id.AppCompatEditText_baseHr)
    AppCompatEditText AppCompatEditText_baseHr;
    @BindView(R.id.Dialinterface)
    AppCompatEditText Dialinterface;
    @BindView(R.id.BASE_heart)
    AppCompatEditText BASE_heart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        ButterKnife.bind(this);

        radioGroup_distanceUnit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sendValue(BleSDK.SetDistanceUnit(checkedId==R.id.radio_km));//距离单位切换 Distance unit switching
            }
        });
        radioGroupTimeMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sendValue(BleSDK.SetTimeModeUnit(checkedId==R.id.radio_12h));//12/24时间切换 12 / 24 time switching
            }
        });
        SwitchCompatHand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendValue(BleSDK.setWristOnEnable(isChecked));//抬手亮屏开关 Raise the hand to light the screen switch
            }
        });

        radioGroupTempUnit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sendValue(BleSDK.setTemperatureUnit(checkedId!=R.id.radio_temp_c));//温度华氏度/摄氏度切换  Temperature Fahrenheit / Celsius switching
            }
        });
        Night_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendValue(BleSDK.setLightMode(isChecked));//夜间模式开关 Night mode switch
            }
        });
        Social_distance_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendValue(BleSDK.Social_distance_switch(isChecked));//社交距离提醒开关 Social distance reminder switch
            }
        });
        Chinese_English_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendValue(BleSDK.setLauage(isChecked));//中英文切换 Chinese English switching
            }
        });

    }

    @OnClick({R.id.setDialinterface, R.id.button_deviceinfo_set, R.id.button_deviceinfo_get})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setDialinterface://设备表盘切换 Device dial switching
                if(TextUtils.isEmpty(Dialinterface.getText())){
                    showToast("Dialinterface is null");
                    return;
                }
                sendValue(BleSDK.SetDialinterface(Integer.valueOf(Dialinterface.getText().toString())));
                break;
            case R.id.button_deviceinfo_set://设置手环基本参数
                setDeviceInfo();
                break;
            case R.id.button_deviceinfo_get://获取手环基本参数
                sendValue(BleSDK.GetDeviceInfo());
                break;

        }
    }


    private void setDeviceInfo() {
        String brightness = AppCompatEditText_baseHr.getText().toString();
        MyDeviceInfo deviceBaseParameter = new MyDeviceInfo();
        deviceBaseParameter.setDistanceUnit(radioGroup_distanceUnit.getCheckedRadioButtonId() == R.id.radio_mile);//距离单位切换 Distance unit switching
        deviceBaseParameter.setIs12Hour(radioGroupTimeMode.getCheckedRadioButtonId() == R.id.radio_12h);//12/24时间切换 12 / 24 time switching
        deviceBaseParameter.setTemperature_unit(radioGroupTempUnit.getCheckedRadioButtonId() == R.id.radio_temp_c);//温度华氏度/摄氏度切换  Temperature Fahrenheit / Celsius switching
        deviceBaseParameter.setBright_screen(SwitchCompatHand.isChecked());//抬手亮屏开关 Raise the hand to light the screen switch
        deviceBaseParameter.setFahrenheit_or_centigrade(left_or_light.isChecked());//温度华氏度/摄氏度切换  Temperature Fahrenheit / Celsius switching
        deviceBaseParameter.setNight_mode(Night_mode.isChecked());//夜间模式开关 Night mode switch
        if(!TextUtils.isEmpty(Dial_switch.getText())){//设备表盘切换 Device dial switching
            deviceBaseParameter.setDialinterface(Integer.parseInt(Dial_switch.getText().toString()));
        }
        deviceBaseParameter.setSocial_distance_switch(Social_distance_switch.isChecked());//社交距离提醒开关 Social distance reminder switch
        deviceBaseParameter.setChinese_English_switch(Chinese_English_switch.isChecked());//中英文切换 Chinese English switching
        String base = BASE_heart.getText().toString();
        if(!TextUtils.isEmpty(base)&&Integer.valueOf(base)>40){//基础心率值，不低于40  Basic heart rate value, no less than 40
            deviceBaseParameter.setBaseheart(Integer.valueOf(base));
        }else{
            showToast("The input value must be greater than 40");
            return;
        }
        if (!TextUtils.isEmpty(brightness)) {//设备亮度0-5 Equipment brightness 0-5
            deviceBaseParameter.setScreenBrightness(Integer.valueOf(brightness));
        }

        sendValue(BleSDK.SetDeviceInfo(deviceBaseParameter));
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        Log.e("info",maps.toString());
        String dataType = getDataType(maps);
        Map<String, String> data = getData(maps);
        switch (dataType) {
            case BleConst.GetDeviceInfo:// 获取手环基本参数  Get basic parameters of Bracelet
            case BleConst.SetDeviceInfo:// 设置手环基本参数 Set basic parameters of Bracelet
                showDialogInfo(maps.toString());
                    break;
        }
    }
}
