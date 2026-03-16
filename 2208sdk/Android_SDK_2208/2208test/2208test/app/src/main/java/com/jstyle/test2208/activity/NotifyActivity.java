package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.model.Notifier;
import com.jstyle.test2208.R;
import com.jstyle.test2208.adapter.NotifyAdapter;
import java.util.Map;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 通知提醒命令 （Notification reminder command）
 */
public class NotifyActivity extends BaseActivity {
    @BindView(R.id.RecyclerView_notify)
    RecyclerView RecyclerViewNotify;
    @BindArray(R.array.nofityarray)
    String[] array;
    NotifyAdapter notifyAdapter;
    @BindView(R.id.ed_content)
    EditText edContent;
    @BindView(R.id.ed_title)
    EditText ed_title;

    @BindView(R.id.bt_send)
    Button btSend;
    @BindView(R.id.info)
    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 3);
        RecyclerViewNotify.setLayoutManager(linearLayoutManager);
        notifyAdapter = new NotifyAdapter(array);
        RecyclerViewNotify.setAdapter(notifyAdapter);
    }


    @OnClick(R.id.bt_send)
    public void onViewClicked() {
        int type=getNotifyType();//发送通知提醒 Send notification reminder
        String content=edContent.getText().toString();
        String title=ed_title.getText().toString();
        if(TextUtils.isEmpty(content))return;
        Notifier notifier=new Notifier();
        if(!TextUtils.isEmpty(title)){
            notifier.setTitle(title);
        }
        notifier.setInfo(content);//内容 content
        notifier.setType(type);//0 Tel，1 Sms，2 Wechat，3 Facebook，4 Instagram，5 Skype，6 Telegram，7 Twitter，8 vkclient，9 WhatApp，10 QQ， 11 In，12 Stop Tel
        sendValue(BleSDK.setNotifyData(notifier));
    }

    public int getNotifyType() {
        int type=notifyAdapter.getCheck()==array.length-1?0xff:notifyAdapter.getCheck();
        return type;
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        switch (dataType){
            case BleConst.Notify:
                if(null!=info){
                    info.setText(maps.toString());
                }
                break;
        }}

}
