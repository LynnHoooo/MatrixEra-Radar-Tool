package com.jstyle.test2208.activity;

import android.Manifest;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.Util.ResolveUtil;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.constant.DeviceKey;
import com.jstyle.test2208.R;
import com.jstyle.test2208.Util.BleData;
import com.jstyle.test2208.Util.ChartDataUtil;
import com.jstyle.test2208.Util.CsvUtils;
import com.jstyle.test2208.Util.PermissionsUtil;
import com.jstyle.test2208.Util.ResolveData;
import com.jstyle.test2208.Util.RxBus;
import com.jstyle.test2208.Util.SchedulersTransformer;
import com.jstyle.test2208.ble.BleManager;
import com.jstyle.test2208.ble.BleService;
import com.jstyle.test2208.daomananger.StepDetailDataDaoManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

import static com.jstyle.test2208.Util.ChartDataUtil.initDataChartView;

public class EcgPPgStatusActivity extends BaseActivity {


    @BindView(R.id.myswitch)
    SwitchCompat myswitch;
    @BindView(R.id.lineChartView_ecg)
    LineChartView lineChartView_ecg;
    @BindView(R.id.lineChartView_ecger)
    LineChartView lineChartView_ecger;

    @BindView(R.id.lineChartView_ecg2)
    LineChartView lineChartView_ecg2;
    @BindView(R.id.lineChartView_ecg3)
    LineChartView lineChartView_ecg3;
    @BindView(R.id.lineChartView_ecg4)
    LineChartView lineChartView_ecg4;

    @BindView(R.id.csv)
    Button csv;
    @BindView(R.id.DATA)
    Button DATA;

    @BindView(R.id.ppg_tt0)
    TextView ppg_tt0;
    @BindView(R.id.ppg_tt1)
    TextView ppg_tt1;
    @BindView(R.id.ppg_tt2)
    TextView ppg_tt2;
    @BindView(R.id.ppg_tt3)
    TextView ppg_tt3;

    @BindView(R.id.heart)
    TextView heart;
    @BindView(R.id.hearter)
    TextView hearter;
    @BindView(R.id.fliter)
    SwitchCompat fliter;
    LinkedList<Float> q1, q2, q3,q4, q5,q6,q7;
    private Disposable subscription;
    List<List<String>> datassss;
    List<String> dataer;
    private static  final int MeasureTimes = 200;
    protected static boolean GetPpgPPGSensor=false;
    boolean myfliter=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecgppgstatus);
        ButterKnife.bind(this);
        fliter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myfliter=isChecked;
            }
        });
        PermissionsUtil.requestPermissions(EcgPPgStatusActivity.this, new PermissionsUtil.PermissionListener() {
            @Override
            public void granted(String name) {
                myswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        GetPpgPPGSensor=isChecked;
                        if(isChecked ){
                            q1=new LinkedList<>();
                            q2=new LinkedList<>();
                            q3=new LinkedList<>();
                            q4=new LinkedList<>();
                            q5=new LinkedList<>();
                            q6=new LinkedList<>();
                            q7=new LinkedList<>();
                            datassss=new ArrayList<>();
                            dataer=new ArrayList<>();
                            String[] head = {"P1", "P2", "P4", "P6", "X", "Y","Z"};
                            List<String> headList = Arrays.asList(head);
                            datassss.add(headList);

                            startPPGTimer();
                        }else{
                           if (ppgDisposable != null && !ppgDisposable.isDisposed()) {
                                ppgDisposable.dispose();
                            }
                        }
                        BleManager.getInstance().offerValue(BleSDK.GetPpgPPGSensor(isChecked));
                      /*  BleManager.getInstance().offerValue(BleSDK.StartDeviceMeasurementWithType(3,isChecked));*/
                        BleManager.getInstance().offerValue(BleSDK.RealTimeStep(isChecked,isChecked));
                        BleManager.getInstance().writeValue();
                    }
                });
                csv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(datassss.size()>0 ){
                            Observable.create(new ObservableOnSubscribe<String>() {
                                @Override
                                public void subscribe(ObservableEmitter<String> e) throws Exception {
                                    CsvUtils.createCsvFile(datassss);
                                    e.onComplete();
                                }
                            }).compose(SchedulersTransformer.<String>applySchedulers()).subscribe(new Observer<String>() {
                                @Override
                                public void onSubscribe(Disposable d) { }
                                @Override
                                public void onNext(String value) { }
                                @Override
                                public void onError(Throwable e) { }
                                @Override
                                public void onComplete() {
                                    CsvUtils.shareByPhone(EcgPPgStatusActivity.this,CsvUtils.testPath+"test.csv");
                                }
                            });



                        }else{
                            Toast.makeText(EcgPPgStatusActivity.this,"File is null",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                DATA.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(dataer.size()>0 ){
                            Observable.create(new ObservableOnSubscribe<String>() {
                                @Override
                                public void subscribe(ObservableEmitter<String> e) throws Exception {
                                    CsvUtils.createCsvFileer(dataer);
                                    e.onComplete();
                                }
                            }).compose(SchedulersTransformer.<String>applySchedulers()).subscribe(new Observer<String>() {
                                @Override
                                public void onSubscribe(Disposable d) { }
                                @Override
                                public void onNext(String value) { }
                                @Override
                                public void onError(Throwable e) { }
                                @Override
                                public void onComplete() {
                                    CsvUtils.shareByPhone(EcgPPgStatusActivity.this,CsvUtils.testPath+"tester.csv");
                                }
                            });

                        }else{
                            Toast.makeText(EcgPPgStatusActivity.this,"File is null",Toast.LENGTH_SHORT).show();
                        }
                    }
                });



     /*   initDataChartView(lineChartView_ecg, 0, 60000, 50, -1);
        initDataChartView(lineChartView_ecg2, 0, 68000, 50, -1);
        initDataChartView(lineChartView_ecg3, 0, 15000, 50, -1);
        initDataChartView(lineChartView_ecg4, 0, 450000, 50, -1);
        initDataChartView(lineChartView_ecger, 0, 2, 50, -2);*/
             /*   initDataChartView(lineChartView_ecg, 0, 405847, rrvalue, -405847/2*//*-195847*//*);
                initDataChartView(lineChartView_ecg2, 0, 103391, rrvalue, -1);
                initDataChartView(lineChartView_ecg3, 0, 15354431, rrvalue, -1);
                initDataChartView(lineChartView_ecg4, 0, 15354431, rrvalue, -1);
                initDataChartView(lineChartView_ecger, 0, 20000, rrvalue, -2);*/

           /*     subscription = RxBus.getInstance().toObservable(BleData.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<BleData>() {
                    @Override
                    public void accept(BleData data) {
                        String action = data.getAction();
                        if (action.equals(BleService.ACTION_DATA_AVAILABLE)) {
                            byte[]value=data.getValue();
                            if((byte)0x3b==value[0]){
                            }
                        }
                    }
                });*/



            }

            @Override
            public void NeverAskAgain() {

            }

            @Override
            public void disallow(String name) {

            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }


     @Override
    protected void on3bBind( byte[]value) {
        super.on3bBind(value);
         if(null!=q1&&GetPpgPPGSensor&&value.length>20){
             byte[] tempValue = new byte[value.length - 3];
             System.arraycopy(value, 3, tempValue, 0, tempValue.length);

             Log.e("BleService", "vvv" + ResolveData.byte2Hex(tempValue));
             int p1 = ResolveUtil.getValue(tempValue[0], 2) + ResolveUtil.getValue(tempValue[ 1], 1)+ResolveUtil.getValue(tempValue[ 2], 0);
             int p2 = ResolveUtil.getValue(tempValue[3], 2) + ResolveUtil.getValue(tempValue[ 4], 1)+ResolveUtil.getValue(tempValue[ 5], 0);
             int p3 = ResolveUtil.getValue(tempValue[6], 2) + ResolveUtil.getValue(tempValue[ 7], 1)+ResolveUtil.getValue(tempValue[ 8], 0);
             int p4 = ResolveUtil.getValue(tempValue[9], 2) + ResolveUtil.getValue(tempValue[ 10], 1)+ResolveUtil.getValue(tempValue[11], 0);

             int p6 = ResolveUtil.getValue(tempValue[15], 2) + ResolveUtil.getValue(tempValue[ 16], 1)+ResolveUtil.getValue(tempValue[17], 0);
             int px = ResolveUtil.getValue(tempValue[18], 1) + ResolveUtil.getValue(tempValue[ 19], 0);
             int py = ResolveUtil.getValue(tempValue[20], 1) + ResolveUtil.getValue(tempValue[ 21], 0);
             int pz = ResolveUtil.getValue(tempValue[22], 1) + ResolveUtil.getValue(tempValue[ 23], 0);
             if (q1.size() > MeasureTimes){
                 q1.removeFirst();
             }
             if (q2.size() > MeasureTimes){
                 q2.removeFirst();
             }
             if (q3.size() > MeasureTimes){
                 q3.remove(0);
             }
             if (q4.size() > MeasureTimes){
                 q4.remove(0);
             }
             if (q5.size() > MeasureTimes){
                 q5.remove(0);
             }
             if (q6.size() > MeasureTimes){
                 q6.remove(0);
             }
             if (q7.size() > MeasureTimes){
                 q7.remove(0);
             }
             q1.add(myfliter?ResolveUtil.getPPGData(Double.valueOf(p1)):Float.valueOf(p1));
             q2.add(myfliter?ResolveUtil.getPPGData2(Double.valueOf(p2)):Float.valueOf(p2));
             q3.add(myfliter?ResolveUtil.getPPGData3(Double.valueOf(p4)):Float.valueOf(p4));
             q4.add(myfliter?ResolveUtil.getPPGData4(Double.valueOf(p6)):Float.valueOf(p6));
             if(null!=ppg_tt0){
                 ppg_tt0.setText("Max: "+ Collections.max(q1)+"\t"+"Min: "+ Collections.min(q1));
                 ppg_tt1.setText("Max: "+ Collections.max(q2)+"\t"+"Min: "+ Collections.min(q2));
                 ppg_tt2.setText("Max: "+ Collections.max(q3)+"\t"+"Min: "+ Collections.min(q3));
                 ppg_tt3.setText("Max: "+ Collections.max(q4)+"\t"+"Min: "+ Collections.min(q4));
             }
             q5.add(ResolveUtil.getFloat( px));
             q6.add(ResolveUtil.getFloat( py));
             q7.add(ResolveUtil.getFloat( pz));
             List<String>   dd=new ArrayList<>();
             dd.add(p1+"");
             dd.add(p2+"");
             dd.add(p4+"");
             dd.add(p6+"");
             dd.add(px+"");
             dd.add(py+"");
             dd.add(pz+"");
             if(null!=datassss&&null!=dataer){
                 datassss.add(dd);
                 dataer.add(ResolveData.byte2Hex(value));
             }

                           /* q5.add(1d);
                            q6.add(1.5d);
                            q7.add(2d);*/
             Log.e("BleService", p1+"*"+p2+"*"+p4+"*"+p6+"*"+px+"*"+py+"*"+pz);
                                   /* lineChartView_ecg.setLineChartData(getEcgLineChartData(q1)) ;
                                    lineChartView_ecg2.setLineChartData(getEcgLineChartData2(q2)) ;
                                    lineChartView_ecg3.setLineChartData(getEcgLineChartData3(q3)) ;
                                    lineChartView_ecg4.setLineChartData(getEcgLineChartData4(q4)) ;
                                    lineChartView_ecger.setLineChartData(getEcgLineChartDataer(q5,q6,q7)) ;*/
                                /*    if(q1.size()>=5){
                                        lineChartView_ecg.setLineChartData(ChartDataUtil.getPpgChartData(lineChartView_ecg, q1, Color.parseColor("#ff99cc00"),500));
                                    }
                                    if(q2.size()>=5){
                                        lineChartView_ecg2.setLineChartData(ChartDataUtil.getPpgChartData(lineChartView_ecg2, q2, Color.parseColor("#ffcc0000"),500));
                                    }
                                    if(q3.size()>=5){
                                        lineChartView_ecg3.setLineChartData(ChartDataUtil.getPpgChartData(lineChartView_ecg3, q3, Color.parseColor("#ffff4444"),500));
                                    }
                                    if(q4.size()>=5){
                                        lineChartView_ecg4.setLineChartData(ChartDataUtil.getPpgChartData(lineChartView_ecg4, q4,  Color.parseColor("#ff669900"),500));
                                    }
                                    if(q5.size()>=5){
                                        lineChartView_ecger.setLineChartData(ChartDataUtil.getPpgChartDataXyz(lineChartView_ecger, q5,q6,q7));
                                    }*/
         }

     }

    Disposable ppgDisposable;
    private void startPPGTimer(){
        if(ppgDisposable!=null&&!ppgDisposable.isDisposed())return;
        Observable.interval(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                ppgDisposable=d;
            }

            @Override
            public void onNext(Long aLong) {

                                  if(q1.size()>=5){
                                        lineChartView_ecg.setLineChartData(ChartDataUtil.getPpgChartData(lineChartView_ecg, q1, Color.parseColor("#ff99cc00")));
                                    }
                                    if(q2.size()>=5){
                                        lineChartView_ecg2.setLineChartData(ChartDataUtil.getPpgChartData1(lineChartView_ecg2, q2, Color.parseColor("#ff669900")));
                                    }
                                    if(q3.size()>=5){
                                        lineChartView_ecg3.setLineChartData(ChartDataUtil.getPpgChartData2(lineChartView_ecg3, q3, Color.parseColor("#ffff4444")));
                                    }
                                    if(q4.size()>=5){
                                        lineChartView_ecg4.setLineChartData(ChartDataUtil.getPpgChartData3(lineChartView_ecg4, q4,  Color.parseColor("#ff669900")));
                                    }
                                    if(q5.size()>=5){
                                        lineChartView_ecger.setLineChartData(ChartDataUtil.getPpgChartDataXyz(lineChartView_ecger, q5,q6,q7));
                                    }


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        Log.e("dataCallback",maps.toString());
        switch (dataType){
            case BleConst.ClosePPGSensor:

                break;
            case BleConst.RealTimeStep:
                Map<String, String> mmp = getData(maps);
                String heartff = mmp.get(DeviceKey.HeartRate);//心率 HeartRate
                if(null!=hearter){
                    hearter.setText("HeartRate:"+heartff+"\n");
                }
                break;
            case BleConst.MeasurementOxygenCallback:
                Map<String,String>mapsa= getData(maps);
                String HeartRate = mapsa.get(DeviceKey.HeartRate);
                String Blood_oxygen = mapsa.get(DeviceKey.Blood_oxygen);
                String HRV = mapsa.get(DeviceKey.HRV);
                String Stress = mapsa.get(DeviceKey.Stress);
                String HighPressure = mapsa.get(DeviceKey.HighPressure);
                String LowPressure = mapsa.get(DeviceKey.LowPressure);
                if(null!=heart){
                    heart.setText("HeartRate:"+HeartRate+"\n"+
                            "Blood_oxygen:"+Blood_oxygen+"\n"+
                            "HRV:"+HRV+"\n"+
                            "Stress:"+Stress+"\n"+
                            "HighPressure:"+HighPressure+"\n"+
                            "LowPressure:"+LowPressure+"\n"
                    );
                }
                break;
        }}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }
}
