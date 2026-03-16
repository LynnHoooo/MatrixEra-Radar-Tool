package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.test2208.R;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClockDialActivity extends BaseActivity {
    @BindView(R.id.main_RadioGroup)
    RadioGroup main_RadioGroup;


int mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);
        ButterKnife.bind(this);
        mode=0;
        main_RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.dia0:
                        mode=0;
                        break;
                    case R.id.dia1:
                        mode=1;
                        break;
                    case R.id.dia2:
                        mode=2;
                        break;
                    case R.id.dia3:
                        mode=3;
                        break;
                    case R.id.dia4:
                        mode=4;
                        break;
                }
            }
        });
    }




  @OnClick({R.id.Set,R.id.get})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.Set:
                sendValue(BleSDK.SetBraceletdial(mode));
                break;
            case R.id.get:
                sendValue(BleSDK.GetBraceletdial());
                break;
        }

    }
    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        switch (dataType){
            case BleConst.Braceletdial:
            case BleConst.Braceletdialok:
                showDialogInfo(maps.toString());
                break;
        }}

}
