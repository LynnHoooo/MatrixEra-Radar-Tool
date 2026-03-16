package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.test2208.R;
import com.jstyle.test2208.adapter.GpsDataAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GpsActivity extends BaseActivity {
    @BindView(R.id.Opengps)
    SwitchCompat Opengps;
    @BindView(R.id.RecyclerView_gps)
    RecyclerView RecyclerView_gps;
    GpsDataAdapter hrvDataAdapter;
    List<Map<String, String>> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        ButterKnife.bind(this);
        list.clear();
        Opengps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!buttonView.isPressed()){
                    sendValue(BleSDK.GPSControlCommand(isChecked));
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView_gps.setLayoutManager(linearLayoutManager);
           hrvDataAdapter = new GpsDataAdapter();
        RecyclerView_gps.setAdapter(hrvDataAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        RecyclerView_gps.addItemDecoration(dividerItemDecoration);

    }

    @OnClick({R.id.ReadGps})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ReadGps:
                sendValue(BleSDK.GetGpsData(1));
                break;


        }
    }



    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        boolean finish = getEnd(maps);
        Log.e("info",maps.toString());
        switch (dataType){
            case BleConst.GPSControlCommand:
                showDialogInfo(maps.toString());
                break;
            case BleConst.Gps:
                    hrvDataAdapter.ADDData(maps.toString());
                break;

        }
    }
}
