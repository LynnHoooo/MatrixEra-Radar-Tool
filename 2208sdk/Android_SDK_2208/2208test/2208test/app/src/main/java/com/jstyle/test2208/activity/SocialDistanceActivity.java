package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.test2208.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 社交距离提醒 (Social distance reminder)
 */
public class SocialDistanceActivity extends BaseActivity {
    @BindView(R.id.autoText)
    Spinner autoText;
    @BindView(R.id.autoTexter)
    Spinner autoTexter;
    int Interval;//1-250 ,0 or 255 is close
    int duration;//20-60
    List<String> Intervaldata=new ArrayList();
    List<String> durationdata=new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_distance);
        ButterKnife.bind(this);
        for(int i=1;i<251;i++){
            Intervaldata.add(i+"");
        }

        for(int i=20;i<61;i++){
            durationdata.add(i+"");
        }
        ArrayAdapter<String>adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,android.R.id.text1,Intervaldata);
        autoText.setAdapter(adapter);
        autoText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Interval= Integer.valueOf(Intervaldata.get(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<String>adapterer=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,android.R.id.text1,durationdata);
        autoTexter.setAdapter(adapterer);
        autoTexter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                duration= Integer.valueOf(durationdata.get(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @OnClick({R.id.button_SocialDistance_set, R.id.button_SocialDistance_get})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_SocialDistance_set://设置社交距离提醒 Set social distance reminder
               sendValue(BleSDK.setSocialSetting(Interval,duration,(short)-80));
                break;
            case R.id.button_SocialDistance_get://获取社交距离提醒 Get social distance reminders
                sendValue(BleSDK.getSocialSetting());
                break;
        }
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        Log.e("info",maps.toString());
        String dataType= getDataType(maps);
        switch (dataType){
            case BleConst.SocialdistanceSetting:
            case BleConst.SocialdistanceGetting:
                showDialogInfo(maps.toString());
                break;
        }
    }
}
