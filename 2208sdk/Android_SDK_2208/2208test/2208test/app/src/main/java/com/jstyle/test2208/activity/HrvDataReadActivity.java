package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.constant.DeviceKey;
import com.jstyle.test2208.R;
import com.jstyle.test2208.adapter.HrvDataAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 获得HRV测试数据 (Obtain HRV test data)
 */
public class HrvDataReadActivity extends BaseActivity {
    @BindView(R.id.RecyclerView_hrvData)
    RecyclerView RecyclerViewHrvData;
    private HrvDataAdapter hrvDataAdapter;
    byte ModeStart=0x00;       //开始获取数据 start getting data
    byte ModeContinue=0x02;    //继续读取数据 continue reading data
    byte ModeDelete=(byte) 0x99;//删除数据  delete data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hrv_data_read);
        ButterKnife.bind(this);
        init();
    }

    int dataCount = 0;

    @OnClick({R.id.bt_readData, R.id.bt_DeleteData})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_readData://获得HRV测试数据 (Obtain HRV test data)
                list.clear();
                dataCount=0;
                getHrvData(ModeStart);
                break;
            case R.id.bt_DeleteData://删除HRV测试数据 Delete HRV test data
                getHrvData(ModeDelete);
                break;
        }
    }

    List<Map<String, String>> list = new ArrayList<>();

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        Log.e("info",maps.toString());
        String dataType = getDataType(maps);
        boolean finish = getEnd(maps);
        switch (dataType) {
            case BleConst.DeleteHrv://删除HRV测试数据后返回数据 Return data after deleting HRV test data
                showDialogInfo(maps.toString());
                break;
            case BleConst.GetHRVData://获取HRV测试数据后返回数据 Return data after obtaining HRV test data
                dataCount++;
                list.addAll((List<Map<String, String>>) maps.get(DeviceKey.Data));
                if (finish) {
                    hrvDataAdapter.setData(list);
                }
                if (dataCount == 50) {
                    if (finish) {
                        hrvDataAdapter.setData(list);
                    } else {
                        getHrvData(ModeContinue);
                    }
                }
                break;
        }
    }

    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewHrvData.setLayoutManager(linearLayoutManager);
        hrvDataAdapter = new HrvDataAdapter();
        RecyclerViewHrvData.setAdapter(hrvDataAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        RecyclerViewHrvData.addItemDecoration(dividerItemDecoration);

    }

    private void getHrvData(byte mode) {
        /**
         * dateOfLastData 设备返回所有数据后最后的时间戳，第一次没有的情况为null，或者“”，同步数据后保存数据库
         * dateOfLastData The last timestamp after the device returns all data,
         *  null if there is no data for the first time, or "", save the database after synchronizing the data
         */
        sendValue(BleSDK.GetHRVDataWithMode(mode,""));
    }
}
