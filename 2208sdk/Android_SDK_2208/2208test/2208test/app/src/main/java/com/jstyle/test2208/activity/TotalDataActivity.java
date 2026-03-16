package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.constant.DeviceKey;
import com.jstyle.test2208.R;
import com.jstyle.test2208.adapter.TotalDataAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 获得计步总数据 (Obtain step total data)
 */
public class TotalDataActivity extends BaseActivity {
    @BindView(R.id.bt_readData)
    Button btReadData;
    @BindView(R.id.bt_DeleteData)
    Button btDeleteData;
    @BindView(R.id.RecyclerView_totalData)
    RecyclerView RecyclerViewDetailData;
    private TotalDataAdapter toatlDataAdapter;
    byte ModeStart=0;
    byte ModeContinue=2;
    byte ModeDelete=(byte)0x99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_data);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewDetailData.setLayoutManager(linearLayoutManager);
        toatlDataAdapter = new TotalDataAdapter();
        RecyclerViewDetailData.setAdapter(toatlDataAdapter);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        RecyclerViewDetailData.addItemDecoration(dividerItemDecoration);
    }

    @OnClick({R.id.bt_readData, R.id.bt_DeleteData})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_readData://获取计步总数据 Obtain total step count data
                dataCount=0;
                list.clear();
                getTotalData(ModeStart);
                break;
            case R.id.bt_DeleteData://删除计步总数据 Delete step total data
                getTotalData(ModeDelete);
                break;
        }
    }
    List<Map<String,String>>list=new ArrayList<>();
    int dataCount=0;
    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        boolean finish=getEnd(maps);
        switch (dataType){
            case BleConst.Delete_GetTotalActivityData://删除计步总数据后数据返回结果 After deleting the total step count data, the data returns the result
                showDialogInfo(maps.toString());
                break;
            case BleConst.GetTotalActivityData://获取计步总数据后数据返回结果 After obtaining the total step count data, the data returns the result
                dataCount++;
                list.addAll((List<Map<String,String>>)maps.get(DeviceKey.Data));
                if(finish){
                    toatlDataAdapter.setData(list);
                }
                if(dataCount==50){
                   dataCount=0;
                    if(finish){
                        toatlDataAdapter.setData(list);
                    }else{
                        getTotalData(ModeContinue);
                    }
                }
                break;
        }
    }


    private void getTotalData(byte mode){//发送命令给设备 send command to device
        /**
         * dateOfLastData 设备返回所有数据后最后的时间戳，第一次没有的情况为null，或者“”，同步数据后保存数据库
         *dateOfLastData The last timestamp after the device returns all data, null if there is no data for the first time, or "", save the database after synchronizing the data
         */
        sendValue(BleSDK.GetTotalActivityDataWithMode(mode,""));
    }
}
