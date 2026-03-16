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
 * 清除历史数据 （Clear historical data）
 */
public class ClearDataActivity extends BaseActivity {
    @BindView(R.id.info)
    TextView info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.Clear})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.Clear://清除历史数据 （Clear historical data）
                sendValue(BleSDK.ClearBraceletData());
                break;
        }
    }


    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        Log.e("dataCallback",maps.toString());
        switch (dataType){
            case BleConst.Clear_Bracelet_data://清除历史数据后返回的结果 The result returned after clearing the historical data
            if(null!=info){
                info.setText(maps.toString());
            }
                break;
        }}

}
