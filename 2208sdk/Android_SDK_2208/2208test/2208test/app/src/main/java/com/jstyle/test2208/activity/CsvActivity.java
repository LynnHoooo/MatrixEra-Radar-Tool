package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;

import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.constant.DeviceKey;
import com.jstyle.test2208.R;
import com.jstyle.test2208.Util.CsvUtils;
import com.jstyle.test2208.Util.DateUtil;
import com.jstyle.test2208.Util.SchedulersTransformer;
import com.jstyle.test2208.Util.SharedPreferenceUtils;
import com.jstyle.test2208.daomananger.HeartDataDaoManager;
import com.jstyle.test2208.daomananger.SleepDataDaoManager;
import com.jstyle.test2208.daomananger.StepDetailDataDaoManager;
import com.jstyle.test2208.model.HeartData;
import com.jstyle.test2208.model.SleepData;
import com.jstyle.test2208.model.StepDetailData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class CsvActivity extends BaseActivity {

    @BindView(R.id.CalendarView)
    android.widget.CalendarView CalendarView;
    @BindView(R.id.bt_share_csv)
    Button btShareCsv;
    byte ModeStart = 0;
    byte ModeContinue = 2;
    byte ModeDelete = (byte)0x99;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv);
        ButterKnife.bind(this);
        init();
    }

    private static final String TAG = "CsvActivity";

    private void init() {

        CalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull android.widget.CalendarView view, int year, int month, int dayOfMonth) {
                Log.i(TAG, "onSelectedDayChange: " + year + " " + month + " " + dayOfMonth);
                 date=year+"."+String.format("%02d.",month+1)+String.format("%02d",dayOfMonth);
            }
        });
    }

    @OnClick(R.id.bt_share_csv)
    public void onViewClicked() {
        startSycData();
    }

    private void startSycData() {
        showProgressDialog("Sync...");
        getDetailData(ModeStart);
    }

    List<Map<String, String>> list = new ArrayList<>();
    private List<Map<String, String>> listDetail = new ArrayList<>();
    private List<Map<String, String>> listSleep = new ArrayList<>();
    private List<Map<String, String>> listHeart = new ArrayList<>();//单次心率历史数据
    private List<Map<String, String>> listHistoryHeart = new ArrayList<>();


    int dataCount = 0;

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType = getDataType(maps);
        boolean finish = getEnd(maps);
        Log.i(TAG, "dataCallback: " + finish);
        switch (dataType) {
            case BleConst.GetDetailActivityData:
                dataCount++;
                listDetail.addAll((List<Map<String, String>>) maps.get(DeviceKey.Data));
                if (finish) {
                    dataCount=0;
                    saveStepDetailData();
                    getSleepData(ModeStart);
                }
                if (dataCount == 50) {
                    dataCount = 0;
                    if (finish) {
                        dataCount=0;
                        disMissProgressDialog();
                       saveStepDetailData();
                        getSleepData(ModeStart);
                    } else {
                        getDetailData(ModeContinue);
                    }
                }
                break;
            case BleConst.GetDetailSleepData:
                dataCount++;
                listSleep.addAll((List<Map<String, String>>) maps.get(DeviceKey.Data));
                if (finish) {
                    getDynamicHeartHistoryData(ModeStart);
                    saveSleepData();
                    disMissProgressDialog();
                }
                if (dataCount == 50) {
                    dataCount = 0;
                    if (finish) {
                        getDynamicHeartHistoryData(ModeStart);
                        saveSleepData();
                        disMissProgressDialog();
                    } else {
                        getSleepData(ModeContinue);
                    }
                }
                break;
            case BleConst.GetDynamicHR:
                dataCount++;
                listHistoryHeart.addAll((List<Map<String,String>>)maps.get(DeviceKey.Data));
                if(finish){
                    dataCount=0;
                    saveHeartHistoryData();
                    getStaticHeartHistoryData(ModeStart);
                 //   heartRateDataAdapter.setData(list,SendCmdState.GET_HEART_DATA);
                }
                if(dataCount==50){
                    dataCount=0;
                    if(finish){
                        saveHeartHistoryData();
                        getStaticHeartHistoryData(ModeStart);
                      //  heartRateDataAdapter.setData(list,SendCmdState.GET_HEART_DATA);
                    }else{
                        getDynamicHeartHistoryData(ModeContinue);
                    }
                }

                break;
            case BleConst.GetStaticHR:
                dataCount++;
                listHeart.addAll((List<Map<String,String>>)maps.get(DeviceKey.Data));
                if(finish){
                    dataCount=0;
                    saveHeartData();
                  //  heartRateDataAdapter.setData(list,SendCmdState.GET_ONCE_HEARTDATA);
                }
                if(dataCount==50){
                    dataCount=0;
                    if(finish){
                        saveHeartData();
                    //    heartRateDataAdapter.setData(list,SendCmdState.GET_ONCE_HEARTDATA);
                    }else{
                        getStaticHeartHistoryData(ModeContinue);
                    }
                }
                break;
        }

    }

    private void getDynamicHeartHistoryData(byte mode){
        sendValue(BleSDK.GetDynamicHRWithMode(mode,""));
    }
    private void getStaticHeartHistoryData(byte mode){
        sendValue(BleSDK.GetStaticHRWithMode(mode,""));
    }
    private void getDetailData(byte mode) {
        showProgressDialog("同步数据");
        sendValue(BleSDK.GetDetailActivityDataWithMode(mode,""));
    }
    private void getSleepData(byte mode) {
        showProgressDialog("同步数据");
        sendValue(BleSDK.GetDetailSleepDataWithMode(mode,""));
    }

    private void saveStepDetailData() {
        final List<StepDetailData> stepDataList = new ArrayList<>();
        String deviceAddress = SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);
        for (Map<String, String> map : listDetail) {
            List<String> list = new ArrayList<>();
            StepDetailData stepData = new StepDetailData();
            stepData.setAddress(deviceAddress);
            String date = map.get(DeviceKey.Date);
            String totalStep = map.get(DeviceKey.KDetailMinterStep);
            String distance = map.get(DeviceKey.Distance);
            String cal = map.get(DeviceKey.Calories);
            String detailStep = map.get(DeviceKey.ArraySteps);
            String[] stepArrays = detailStep.split(" ");
            for (String step : stepArrays) {
                list.add(step);
            }
            stepData.setDate(date);
            stepData.setStep(totalStep);
            stepData.setDistance(distance);
            stepData.setCal(cal);
            stepData.setMinterStep(detailStep);
            stepDataList.add(stepData);
        }

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                StepDetailDataDaoManager.insertData(stepDataList);
                e.onComplete();
            }
        }).compose(SchedulersTransformer.<String>applySchedulers()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String value) {
                Log.i(TAG, "onNext: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                listDetail.clear();

                //if (homePageFragment != null) homePageFragment.saveFinish();

            }
        });

    }

    private void saveSleepData() {
        String deviceAddress = SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);
        final List<SleepData> sleepDataList = new ArrayList<>();
        for (Map<String, String> map : listSleep) {
            List<String> listHealth = new ArrayList<>();
            String time = map.get(DeviceKey.Date);
            long startMil = DateUtil.getDefaultLongMi(time);
            String[] sleepQuantity = map.get(DeviceKey.ArraySleep).split(" ");
            for (int i = 0; i < sleepQuantity.length; i++) {
                SleepData sleepData = new SleepData();
                sleepData.setAddress(deviceAddress);
                sleepData.setDateString(sleepQuantity[i]);
                long timeMil = startMil + 1 * 60 * 1000l * i;
                sleepData.setTime(DateUtil.getFormatTimeString(timeMil));
                sleepDataList.add(sleepData);
                listHealth.add(sleepQuantity[i]);
            }

        }

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                SleepDataDaoManager.insertData(sleepDataList);
                e.onComplete();
            }
        }).compose(SchedulersTransformer.<String>applySchedulers()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String value) {
                Log.i(TAG, "onNext: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                listSleep.clear();
                // if (homePageFragment != null) homePageFragment.saveFinish();

            }
        });

    }

    private void saveHeartData() {
        final List<HeartData> heartDataList = new ArrayList<>();
        String deviceAddress = SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);

        for (Map<String, String> map : listHeart) {

            List<String> listHealth = new ArrayList<>();
            String date = map.get(DeviceKey.Date);
            int heartRate = Integer.parseInt(map.get(DeviceKey.StaticHR));
            if (heartRate != 0) {
                HeartData heartData = new HeartData();
                heartData.setAddress(deviceAddress);
                heartData.setHeart(heartRate);
                heartData.setTime(date);
                heartDataList.add(heartData);
                listHealth.add(String.valueOf(heartRate));

            }
        }

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                HeartDataDaoManager.insertData(heartDataList);
                e.onComplete();
            }
        }).compose(SchedulersTransformer.<String>applySchedulers()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String value) {
                Log.i(TAG, "onNext: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                listHeart.clear();
                startCreateCsvUtils();
                // if (homePageFragment != null) homePageFragment.saveFinish();

            }
        });
    }

    private void startCreateCsvUtils() {
        String dataDate="";
        if(TextUtils.isEmpty(date)){
            dataDate=DateUtil.getDefaultFormatTime(new Date(CalendarView.getDate()));
        }else {
            dataDate=date.substring(2);
        }
       // CsvUtils.createCsvFile(this,dataDate);
    }

    private void saveHeartHistoryData() {
        final List<HeartData> heartDataList = new ArrayList<>();
        String deviceAddress = SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);
        for (Map<String, String> map : listHistoryHeart) {
            List<String> listHealth = new ArrayList<>();
            String date = map.get(DeviceKey.Date);
            String hrString = map.get(DeviceKey.ArrayDynamicHR);
            String[] hrArray = hrString.split(" ");
            // Log.i(TAG, "saveHeartHistoryData: "+date+" "+hrString);
            long startL = DateUtil.getGpsDateLong(date);
            for (int i = 0; i < hrArray.length; i++) {
                int hr = Integer.valueOf(hrArray[i]);
                long endL = startL + i * 1000 * 60l;
                String endTime = DateUtil.getFormatTimeString(endL);
                if (hr != 0) {
                    HeartData heartData = new HeartData();
                    heartData.setAddress(deviceAddress);
                    heartData.setHeart(hr);
                    heartData.setTime(endTime);
                    heartDataList.add(heartData);
                    listHealth.add(String.valueOf(hr));
                }
            }

        }

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                HeartDataDaoManager.insertData(heartDataList);
                e.onComplete();
            }
        }).compose(SchedulersTransformer.<String>applySchedulers()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String value) {
                Log.i(TAG, "onNext: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                listHistoryHeart.clear();

                //  if (homePageFragment != null) homePageFragment.saveFinish();

            }
        });
    }

}
