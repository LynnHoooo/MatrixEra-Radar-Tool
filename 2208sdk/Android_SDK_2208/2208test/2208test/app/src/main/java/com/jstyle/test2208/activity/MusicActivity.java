package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.test2208.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MusicActivity extends BaseActivity {

    @BindView(R.id.radioGroup_music)
    RadioGroup radioGroup_music;
    @BindView(R.id.ed_content)
    EditText ed_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mucic);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        radioGroup_music.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_mucic_start:
                        sendValue(BleSDK.SetMusicStatus(true));
                        break;
                    case R.id.radio_mucic_stop:
                        sendValue(BleSDK.SetMusicStatus(false));
                        break;
                }

            }
        });

    }


   @OnClick({R.id.bt_send})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.bt_send:
                if(!TextUtils.isEmpty(ed_content.getText())){
                    sendValue(BleSDK.SendMusicname(ed_content.getText().toString()));
                }
                break;
        }




    }


}
