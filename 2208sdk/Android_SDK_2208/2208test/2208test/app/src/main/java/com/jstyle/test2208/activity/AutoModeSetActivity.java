package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.constant.DeviceKey;
import com.jstyle.blesdk2208.model.AutoMode;
import com.jstyle.test2208.R;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 心率，血氧，心率变异性，体温自动监测设置 （Heart rate, blood oxygen, heart rate variability, body temperature automatic monitoring settings）
 */
public class AutoModeSetActivity extends BaseActivity {
    @BindView(R.id.radioGroup_all)
    RadioGroup radioGroup_all;
    @BindView(R.id.edit_minute)
    EditText editMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_set);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        sendValue(BleSDK.GetAutomatic(AutoMode.AutoHeartRate));
    }
    @OnClick({R.id.button_set_activitytime, R.id.button_get_activitytime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_set_activitytime:
                setAutoMode();
                break;
            case R.id.button_get_activitytime:
                switch (radioGroup_all.getCheckedRadioButtonId()){
                    case R.id.radio_1://获取自动监测心率设置 Get automatic heart rate monitoring settings
                        sendValue(BleSDK.GetAutomatic(AutoMode.AutoHeartRate));
                        break;
                    case R.id.radio_2://获取自动监测血氧设置 Get settings for automatic monitoring of blood oxygen
                        sendValue(BleSDK.GetAutomatic(AutoMode.AutoSpo2));
                        break;
                    case R.id.radio_3://获取自动监测温度设置 Get automatic monitoring temperature settings
                        sendValue(BleSDK.GetAutomatic(AutoMode.AutoTemp));
                        break;
                    case R.id.radio_4://获取自动监测心率变异性设置 Get settings for automatic monitoring of heart rate variability
                        sendValue(BleSDK.GetAutomatic(AutoMode.AutoHrv));
                        break;
                }
                break;
        }
    }



    private void setAutoMode() {
        // TODO Auto-generated method stub
        if (TextUtils.isEmpty(editMinute.getText().toString())) return;;
        int minInterval = Integer.parseInt(editMinute.getText().toString());
        AutoMode autoMode=AutoMode.AutoHeartRate;
        switch (radioGroup_all.getCheckedRadioButtonId()){
            case R.id.radio_1://自动监测心率设置 Automatic monitoring heart rate setting
                autoMode=AutoMode.AutoHeartRate;
                break;
            case R.id.radio_2://自动监测血氧设置 Automatic monitoring of blood oxygen settings
                autoMode=AutoMode.AutoSpo2;
                break;
            case R.id.radio_3://自动监测体温设置 Automatic monitoring temperature setting
                autoMode=AutoMode.AutoTemp;
                break;
            case R.id.radio_4://自动监测心率变异性设置 Automatic monitoring of heart rate variability settings
                autoMode=AutoMode.AutoHrv;
                break;
        }
        sendValue(BleSDK.SetAutomatic(true,minInterval, autoMode));
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        Log.e("info",maps.toString());
        String dataType= getDataType(maps);
        Map<String,String>data= getData(maps);
        switch (dataType){
            case BleConst.SetAutomatic://设置  Set
                showSetSuccessfulDialogInfo(dataType);
                break;
            case BleConst.GetAutomatic:// 获取 Get
                String timeInterval = data.get(DeviceKey.IntervalTime);
                editMinute.setText(timeInterval);
                break;
        }
    }


}
