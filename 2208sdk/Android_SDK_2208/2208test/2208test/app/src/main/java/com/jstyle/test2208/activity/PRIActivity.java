package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.test2208.R;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PRIActivity extends BaseActivity {
    @BindView(R.id._switchCompat)
    SwitchCompat _switchCompat;

    @BindView(R.id.data_info)
    TextView data_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pri);
        ButterKnife.bind(this);

        _switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendValue(BleSDK.openRRIntervalTime(isChecked));
            }
        });
    }




    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        Log.e("dataCallback",maps.toString());
        switch (dataType){
            case BleConst.openRRInterval:
            case BleConst.closeRRInterval:
            case BleConst.realtimeRRIntervalData:
                data_info.setText(maps.toString());
                break;
        }}

}
