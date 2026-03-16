package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.test2208.R;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 手表设备震动次数（Vibration times of watch equipment）
 */
public class MotorVibrationActivity extends BaseActivity {
    @BindView(R.id.ed_MotorVibration)
    EditText edMotorVibration;
    @BindView(R.id.info)
    TextView info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motor_vibration);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.set})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.set://手表设备震动次数（Vibration times of watch equipment）
                String times = edMotorVibration.getText().toString();
                if (!TextUtils.isEmpty(times)) {
                    sendValue(BleSDK.MotorVibrationWithTimes(Integer.valueOf(times)));
                }
                break;
        }
    }


    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        Log.e("dataCallback",maps.toString());
        switch (dataType){
            case BleConst.SetMotorVibrationWithTimes:
            if(null!=info){
                info.setText(maps.toString());
            }
                break;
        }}

}
