package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.test2208.R;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 读取设备版本号 （Read device version number）
 */
public class VersionActivity extends BaseActivity {
    @BindView(R.id.info)
    TextView info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.set})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.set://读取设备版本号 （Read device version number）
                sendValue(BleSDK.GetDeviceVersion());
                break;
        }
    }


    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        Log.e("dataCallback",maps.toString());
        switch (dataType){
            case BleConst.GetDeviceVersion:
            if(null!=info){
                info.setText(maps.toString());
            }
                break;
        }}

}
