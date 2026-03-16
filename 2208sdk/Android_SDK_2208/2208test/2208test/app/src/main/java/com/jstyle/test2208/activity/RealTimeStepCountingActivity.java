package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.constant.DeviceKey;
import com.jstyle.test2208.R;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 实时计步 (Real time step counting)
 */
public class RealTimeStepCountingActivity extends BaseActivity {
    @BindView(R.id.info)
    TextView info;
    @BindView(R.id.button_startreal)
    Button buttonStartreal;
    @BindView(R.id.SwitchCompat_temp)
    SwitchCompat SwitchCompatTemp;
    @BindView(R.id.textView_step)
    TextView textViewStep;
    @BindView(R.id.textView_cal)
    TextView textViewCal;
    @BindView(R.id.textView_distance)
    TextView textViewDistance;
    @BindView(R.id.textView_time)
    TextView textViewTime;
    @BindView(R.id.textView_heartValue)
    TextView textViewHeartValue;
    @BindView(R.id.textView_ActiveTime)
    TextView textViewActiveTime;
    @BindView(R.id.textView_tempValue)
    TextView textViewTempValue;
    boolean isStartReal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_step);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button_startreal})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.button_startreal:
                isStartReal = !isStartReal;
                buttonStartreal.setText(isStartReal ? "Stop" : "Start");
                sendValue(BleSDK.RealTimeStep(isStartReal,SwitchCompatTemp.isChecked()));
                break;
        }
    }


    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        Log.e("dataCallback",maps.toString());
        switch (dataType){
            case BleConst.RealTimeStep:
                Map<String, String> mmp = getData(maps);
                String step = mmp.get(DeviceKey.Step);//步数  Number of steps
                String cal = mmp.get(DeviceKey.Calories); // 卡路里 calorie
                String distance = mmp.get(DeviceKey.Distance);//距离 distance
                String time = mmp.get(DeviceKey.ExerciseMinutes);//锻炼分钟 Exercise minutes
                String ActiveTime = mmp.get(DeviceKey.ActiveMinutes);//活动分钟 Activity minutes
                String heart = mmp.get(DeviceKey.HeartRate);//心率 HeartRate
                String TEMP= mmp.get(DeviceKey.TempData);//温度 temperature
                textViewCal.setText(cal);
                textViewStep.setText(step);
                textViewDistance.setText(distance);
                textViewTime.setText(time);
                textViewHeartValue.setText(heart);
                textViewActiveTime.setText(ActiveTime);
                textViewTempValue.setText(TEMP);
                info.setText(maps.toString());
                break;
        }}

}
