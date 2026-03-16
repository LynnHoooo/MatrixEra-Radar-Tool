package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.test2208.R;
import com.jstyle.test2208.adapter.OxyAdapter;

import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 手动测试血氧数据 (Manually test blood oxygen data)
 */
public class Manually_test_blood_oxygen_dataActivity extends BaseActivity {
    @BindView(R.id.bt_readData)
    Button btReadData;
    @BindView(R.id.bt_DeleteData)
    Button btDeleteData;
    @BindView(R.id.RecyclerView_heartData)
    RecyclerView RecyclerViewHeartData;
    private OxyAdapter heartRateDataAdapter; ;
    byte ModeStart=0x00;       //开始获取数据 start getting data
    byte ModeContinue=0x02;    //继续读取数据 continue reading data
    byte ModeDelete=(byte) 0x99;//删除数据  delete data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oxy_info);
        ButterKnife.bind(this);
        init();
    }
    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewHeartData.setLayoutManager(linearLayoutManager);
        heartRateDataAdapter = new OxyAdapter();
        RecyclerViewHeartData.setAdapter(heartRateDataAdapter);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        RecyclerViewHeartData.addItemDecoration(dividerItemDecoration);

    }

    @OnClick({R.id.bt_readData, R.id.bt_DeleteData})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_readData://获取手动测试血氧数据 Obtain manual test blood oxygen data
                heartRateDataAdapter.Clear();
                getHistoryData(ModeStart);
                break;
            case R.id.bt_DeleteData://删除手动测试血氧数据 Delete manual test blood oxygen data
                heartRateDataAdapter.Clear();
                getHistoryData(ModeDelete);
                break;
        }
    }


    int dataCount=0;
    private static final String TAG = "HeartRateInfoActivity";
    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        boolean finish=getEnd(maps);
        switch (dataType){
            case BleConst.Blood_oxygen:
                heartRateDataAdapter.ADDData(maps.toString());
                dataCount++;
                if(finish){
                    dataCount=0;
                }
                if(dataCount==50){
                    dataCount=0;
                    if(finish){
                        disMissProgressDialog();
                    }else{
                        getHistoryData(ModeContinue);
                    }
                }

                break;
            case BleConst.Delete_Blood_oxygen:
                heartRateDataAdapter.ADDData(maps.toString());
                break;

        }
    }


    private void getHistoryData(byte mode){
        sendValue(BleSDK.GetBloodOxygen(mode,""));
    }
}
