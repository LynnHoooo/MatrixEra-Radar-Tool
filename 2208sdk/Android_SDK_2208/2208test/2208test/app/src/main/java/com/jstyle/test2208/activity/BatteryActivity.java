package com.jstyle.test2208.activity;
import android.os.Bundle;
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
 * 设备电池电量获取 Equipment power acquisition
 */
public class BatteryActivity extends BaseActivity {
    @BindView(R.id.info)
    TextView info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.set})
    public void onViewClicked(View view) {
     switch (view.getId()){
         case R.id.set://设备电量获取 Equipment power acquisition
             sendValue(BleSDK.GetDeviceBatteryLevel());
             break;
     }
    }


    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType = getDataType(maps);
        switch (dataType) {
            case BleConst.GetDeviceBatteryLevel:
                if(null!=info){
                    info.setText(maps.toString());
                }
                break;
        }

    }
}
