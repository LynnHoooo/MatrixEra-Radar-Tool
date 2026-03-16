package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.test2208.R;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityLanguageSwitching extends BaseActivity {
    @BindView(R.id.radioGroup1)
    RadioGroup radioGroupmian;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languageswitching);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        radioGroupmian.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.radio_English){
                    sendValue(BleSDK.LanguageSwitching(true));
                }else{
                    sendValue(BleSDK.LanguageSwitching(false));
                }
            }
        });
    }



    @OnClick({})
    public void onViewClicked(View view) {
        switch (view.getId()) {
       /*     case R.id.bt_startMode:
                mode = activityModeAdapter.getSelectPosition();
                if(mode==-1)return;
                sendValue(BleSDK.EnterActivityMode(mode, ExerciseMode.Status_START));
                break;
            case R.id.bt_stopMode:
                mode = activityModeAdapter.getSelectPosition();
                if(mode==-1)return;
                sendValue(BleSDK.EnterActivityMode(mode, ExerciseMode.Status_FINISH));
                break;*/
        }

    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        showDialogInfo(maps.toString());
      }
}
