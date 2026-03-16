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
 * 拍照进入/退出 （Photo entry/exit）
 */
public class PhotoActivity extends BaseActivity {
    @BindView(R.id.info)
    TextView info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.EnterPhotoMode,R.id.Exitphotomode})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.EnterPhotoMode://进入拍照模式 Enter photo mode
                sendValue(BleSDK.EnterPhotoMode());
                break;
            case R.id.Exitphotomode://退出拍照模式 Exit photo mode
                sendValue(BleSDK.ExitPhotoMode());
                break;
        }
    }


    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        Log.e("dataCallback",maps.toString());
        switch (dataType){
            case BleConst.Enter_photo_mode:
            case BleConst.BackHomeView:
            if(null!=info){
                info.setText(maps.toString());
            }
                break;
        }}

}
