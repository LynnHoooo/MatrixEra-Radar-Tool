package com.jstyle.test2208.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.test2208.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 测量控制 HRV,心率，血氧（Health measurement control）
 */
public class MeasurementActivity extends BaseActivity {
    @BindView(R.id.radioGroup_mian)
    RadioGroup radioGroup_mian;
    @BindView(R.id._switch)
    SwitchCompat _switch;
    @BindView(R.id.data_info)
    TextView data_info;
    @BindView(R.id.data_info2)
    TextView data_info2;

    @BindView(R.id.send)
    Button send;


    int type=1;
    boolean open=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        ButterKnife.bind(this);


        radioGroup_mian.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.hrv://HRV测量
                        type=1;
                        break;
                    case R.id.heart://心率测量 heart rate
                        type=2;
                        break;
                    case R.id.oxygen://血氧测量 Blood oxygen
                        type=3;
                        break;
                }

            }
        });
        _switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                open=isChecked;
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendValue(BleSDK.StartDeviceMeasurementWithType(type,open,120000));
            }
        });

    }


    //取整数数组
    @SuppressLint("DefaultLocale")
    public static List<String> getListDataCout(int start, int cout, int end) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < end; i++) {
            list.add(String.format("%02d", start + (i * cout)));
        }
        return list;
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        Log.e("dataCallback",maps.toString());
        switch (dataType){
            case BleConst.MeasurementHrvCallback:
            case BleConst.MeasurementHeartCallback:
            case BleConst.MeasurementOxygenCallback:
            case BleConst.StopMeasurementHrvCallback:
            case BleConst.StopMeasurementHeartCallback:
            case BleConst.StopMeasurementOxygenCallback:

                data_info.setText("");
                data_info.setText(maps.toString());
                break;

        }}

}
