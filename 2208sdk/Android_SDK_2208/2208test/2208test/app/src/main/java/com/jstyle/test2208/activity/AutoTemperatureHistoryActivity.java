package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.test2208.R;
import com.jstyle.test2208.adapter.TempAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设备自动测试出的温度数据 （Temperature data automatically tested by the device）
 */

public class AutoTemperatureHistoryActivity extends BaseActivity {
    @BindView(R.id.RecyclerView_exerciseHistory)
    RecyclerView RecyclerViewExerciseHistory;
    byte ModeStart=0x00;       //开始获取数据 start getting data
    byte ModeContinue=0x02;    //继续读取数据 continue reading data
    byte ModeDelete=(byte) 0x99;//删除数据  delete data
    private List< String> list;
    private TempAdapter activityModeDataAdapter;
    private int dataCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_temp_data);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        list=new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewExerciseHistory.setLayoutManager(linearLayoutManager);
        activityModeDataAdapter = new TempAdapter();
        RecyclerViewExerciseHistory.setAdapter(activityModeDataAdapter);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        RecyclerViewExerciseHistory.addItemDecoration(dividerItemDecoration);
    }

    @OnClick({R.id.read, R.id.delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.read://读取设备自动测试出的温度数据 Read the temperature data automatically tested by the device
                list.clear();
                dataCount=0;
                getDatafortemp(ModeStart);
                break;
            case R.id.delete://删除设备自动测试出的温度数据 Delete the temperature data automatically tested by the device
                list.clear();
                dataCount=0;
                getDatafortemp(ModeDelete);
                break;

        }
    }


    private void getDatafortemp(byte mode){
        /**
         * dateOfLastData 设备返回所有数据后最后的时间戳，第一次没有的情况为null，或者“”，同步数据后保存数据库
         *dateOfLastData The last timestamp after the device returns all data, null if there is no data for the first time, or "", save the database after synchronizing the data
         */
        sendValue(BleSDK.GetAxillaryTemperatureDataWithMode(mode,""));
    }



    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        boolean finish=getEnd(maps);
        switch (dataType){
            case BleConst.deleteGetAxillaryTemperatureDataWithMode://删除设备自动测试出的温度数据 Delete the temperature data automatically tested by the device
                list.add(maps.toString());
                activityModeDataAdapter.setData(list);
                break;
            case BleConst.GetAxillaryTemperatureDataWithMode://读取设备自动测试出的温度数据 Read the temperature data automatically tested by the device
                list.add(maps.toString());
                dataCount++;
                if(finish){
                    dataCount=0;
                    disMissProgressDialog();
                    activityModeDataAdapter.setData(list);
                }
                if(dataCount==50){
                    dataCount=0;
                    if(finish){
                        disMissProgressDialog();
                        activityModeDataAdapter.setData(list);
                    }else{
                        getDatafortemp(ModeContinue);
                    }
                }
                break;
        }
    }
}
