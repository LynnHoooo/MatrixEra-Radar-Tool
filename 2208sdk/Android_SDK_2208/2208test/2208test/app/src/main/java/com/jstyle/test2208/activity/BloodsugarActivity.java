package com.jstyle.test2208.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.Util.CustomCountDownTimer;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.constant.DeviceKey;
import com.jstyle.blesdk2208.model.PPI_Info;
import com.jstyle.test2208.R;
import com.jstyle.test2208.ble.BleManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 血糖  blood sugar
 *采集5分钟ppi 数据，之后用这段数据上传到服务器得到血糖结果。
 * Five minutes of ppi data was collected, and this data was later used to upload to the server to get the blood glucose results.
 */
public class BloodsugarActivity extends BaseActivity {
    @BindView(R.id.progress)
    TextView progress;
    @BindView(R.id.info)
    TextView info;





    CustomCountDownTimer customCountDownTimer;//计时器 timer
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_sugar);
        ButterKnife.bind(this);
        subscribe();

        final float alltime=5*60*1000f;//五分钟 Five Minutes
        customCountDownTimer=   new CustomCountDownTimer((long) alltime, 1000, new CustomCountDownTimer.TimerTickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(final long millisLeft) {
                if(null!=progress){
                    BloodsugarActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            float baifenbi=(alltime-millisLeft)/alltime*100f;
                            if(baifenbi>99){
                                baifenbi=100.0f;
                            }
                            //Log.e("sdnbamndamdn","ssdsds");
                            if(baifenbi-Float.valueOf(baifenbi).intValue()==0){
                                progress.setText(Float.valueOf(baifenbi).intValue()+"%");
                                BleManager.getInstance().writeValue(BleSDK.ppgWithMode(4,(int) baifenbi));
                            }
                        }
                    });
                }
            }
            @Override
            public void onFinish() {
                this.onCancel();
                BleManager.getInstance().writeValue(BleSDK.ppgWithMode(5,0));
                Toast.makeText(BloodsugarActivity.this,Arrays.toString(q1.toArray()),Toast.LENGTH_SHORT).show();
                /**
                 * 用这段数据上传到服务器才能 解析血糖结果
                 * This data is used to upload to the server in order to parse the blood sugar results.
                 */
                Log.e("tag", Arrays.toString(q1.toArray()));
            }
            @Override
            public void onCancel() { }
        }) {};
    }

    @OnClick({R.id.start,R.id.suspend,R.id.Stop})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.start:
                customCountDownTimer.start();
                BleManager.getInstance().writeValue(BleSDK.ppgWithMode(1,2));
                break;
            case R.id.suspend:
                customCountDownTimer.pause();
                BleManager.getInstance().writeValue(BleSDK.ppgWithMode(3,0));
                break;
            case R.id.Stop:
                customCountDownTimer.cancel();
                BleManager.getInstance().writeValue(BleSDK.ppgWithMode(5,0));
               // finish();
                break;
        }
    }

    List<PPI_Info> q1=new ArrayList<>();
    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        Log.e("dataCallback",maps.toString());
        switch (dataType){
            case BleConst.realtimePPIData:
                Map<String,Object> DD= getDataObject(maps);
                PPI_Info ppiInfo=new PPI_Info();
                ppiInfo.setTimestamp((String) DD.get(DeviceKey.KPPGTime));
                ppiInfo.setPpi((List<Integer>) DD.get(DeviceKey.KPPGData));
                q1.add(ppiInfo);
                Log.e("jdnandand",ppiInfo.toString());
            /*    String[] realtimePPIData = DD.get(DeviceKey.KPPGData).split(",");

                for ( int i=0;i<realtimePPIData.length;i++){
                    Log.e("jdnandand",realtimePPIData[i]);



                    q1.add(Double.valueOf(realtimePPIData[i])) ;

                }*/

                break;
            case BleConst.ppgStartSucessed:
            case BleConst.ppgResult:
            case BleConst.ppgStop:
            case BleConst.ppgMeasurementProgress:
            case BleConst.ppgQuit:
            case BleConst.ppgStartFailed:
                if(null!=info){
                    info.setText(maps.toString());
                }
                break;
        }}








}
