package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.constant.DeviceKey;
import com.jstyle.test2208.R;
import com.jstyle.test2208.adapter.ECGDataAdapter;
import com.jstyle.test2208.daomananger.EcgDataDaoManager;
import com.jstyle.test2208.model.EcgHistoryData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ECG 历史数据 （ECG historical data）
 */
public class EcgDataActivity extends BaseActivity {
    @BindView(R.id.RecyclerView_exerciseHistory)
    RecyclerView ECGDATA;
    private ECGDataAdapter ecgDataAdapter;
    int index=0;
    private  String ecgDate;
    private String lastEcgDate;
    StringBuffer   stringBuffer;
    EcgHistoryData healthEcgData;
    String address="";
    List<EcgHistoryData> ecgDataList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecghisdata);
        ButterKnife.bind(this);
        address=getIntent().getStringExtra("address");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ECGDATA.setLayoutManager(linearLayoutManager);
        ecgDataAdapter = new ECGDataAdapter();
        ECGDATA.setAdapter(ecgDataAdapter);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        ECGDATA.addItemDecoration(dividerItemDecoration);
        EcgHistoryData lastHealthData = EcgDataDaoManager.getLastEcgData(address);
        lastEcgDate = "";
        if (lastHealthData != null) {
            lastEcgDate = lastHealthData.getTime();
            Log.e("jsjsjsj",lastEcgDate);
        }
    }

    @OnClick({R.id.readECG,R.id.delete_ECG})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.readECG://读取ecg历史数据 Read ECG history data
                index=0;
                ecgDataAdapter.Clear();
                ecgDataList.clear();
                EcgHistoryData lastHealthData = EcgDataDaoManager.getLastEcgData(address);
                if (lastHealthData != null) {
                    lastEcgDate = lastHealthData.getTime();
                    Log.e("jsjsjsj",lastEcgDate);
                }
                sendValue(BleSDK.getEcgHistoryData(0,lastEcgDate));
                break;
            case R.id.delete_ECG://删除ecg历史数据 Delete ECG history data
                sendValue(BleSDK.DeleteEcgHistoryData());
                ecgDataAdapter.Clear();
                break;
        }
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        if(null!=maps){
            String dataType= getDataType(maps);
            switch (dataType){
                case BleConst.ECGdata://读取ecg历史数据 Read ECG history data
                    boolean finish=getEnd(maps);
                   Map<String,String>ecgmaps= getData(maps);
                    if (null!=ecgmaps.get(DeviceKey.Date)) {
                        String KDate = ecgmaps.get(DeviceKey.Date);
                        ecgDate = KDate;//每一条ecg返回的日期
                        stringBuffer = new StringBuffer();
                        healthEcgData = new EcgHistoryData();
                        int hrv = Integer.parseInt(ecgmaps.get(DeviceKey.HRV));
                        int hr = Integer.parseInt(ecgmaps.get(DeviceKey.HeartRate));
                        int moodValue = Integer.parseInt(ecgmaps.get(DeviceKey.ECGMoodValue));
                        healthEcgData.setTime(ecgDate);
                        healthEcgData.setHrv(hrv);
                        healthEcgData.setHeartRate(hr);
                        healthEcgData.setBreathValue(moodValue);
                        healthEcgData.setAddress(address);
                    }
                    if (null!=ecgmaps.get(DeviceKey.ECGValue)) {//ecg数据拼接
                        String ecgData = ecgmaps.get(DeviceKey.ECGValue);
                        stringBuffer.append(ecgData);
                    }
                    if (finish) {//收到结束标志
                        if (!TextUtils.isEmpty(ecgDate)) {//有数据返回
                            Log.e("jsjsjsj","isEmpty(ecgDate)"+ecgDate+"***"+lastEcgDate);
                            healthEcgData.setArrayECGData(stringBuffer.toString());
                            ecgDataList.add(healthEcgData);
                            //每条日期与数据库最后的日期比较下，ecg最大条数为9的情况看是否还需要读取数据
                            if ( !ecgDate.equals(lastEcgDate) &&index < 9) {
                                index++;
                                ecgDate = "";
                                //继续读取ecg数据
                                sendValue(BleSDK.getEcgHistoryData(index,lastEcgDate));
                            } else {
                                Log.e("jsjsjsj","end__ecgDate");
                                ecgDate = "";
                                index = 0;
                                ecgDataAdapter.setData(ecgDataList);
                                EcgDataDaoManager.insertData(ecgDataList);
                            }
                        } else {
                            //没有任何ecg数据
                            Log.e("jsjsjsj","end__ecgDate");
                            ecgDate = "";
                            index = 0;
                            ecgDataAdapter.setData(ecgDataList);
                            EcgDataDaoManager.insertData(ecgDataList);
                        }
                    }
                    break;
                case BleConst.DeleteECGdata://删除ecg历史数据 Delete ECG history data
                    showDialogInfo(maps.toString());
                    break;
            }
        }
      }
}
