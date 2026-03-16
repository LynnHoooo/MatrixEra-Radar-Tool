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
 * 重置MUC (Reset MUC)
 */
public class MCUActivity extends BaseActivity {
    @BindView(R.id.info)
    TextView info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcu);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.set})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.set://MCU
                sendValue(BleSDK.MCUReset());
                break;
        }
    }


    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        switch (dataType){
            case BleConst.CMD_MCUReset:
            if(null!=info){
                info.setText(maps.toString());
            }
                break;
        }}
}
