package com.jstyle.test2208.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.Util.ResolveUtil;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.constant.DeviceConst;
import com.jstyle.blesdk2208.constant.DeviceKey;
import com.jstyle.test2208.R;
import com.jstyle.test2208.Util.ChartDataUtil;
import com.jstyle.test2208.Util.PermissionsUtil;
import com.neurosky.AlgoSdk.NskAlgoDataType;
import com.neurosky.AlgoSdk.NskAlgoECGValueType;
import com.neurosky.AlgoSdk.NskAlgoProfile;
import com.neurosky.AlgoSdk.NskAlgoSampleRate;
import com.neurosky.AlgoSdk.NskAlgoSdk;
import com.neurosky.AlgoSdk.NskAlgoType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import lecho.lib.hellocharts.view.LineChartView;
/**
 * ECG PPG 测量 （ECG PPG measurement）
 */
public class EcgActivity extends BaseActivity {
    private static final String TAG = "EcgActivity";
    @BindView(R.id.heartValue)
    TextView heartValue;
    @BindView(R.id.hrvValue)
    TextView hrvValue;
    @BindView(R.id.Quality)
    TextView Quality;
    @BindView(R.id.info)
    TextView info;
    @BindView(R.id.radioGroup_mian)
    RadioGroup radioGroup_mian;
    @BindView(R.id.lineChartView_ecg)
    LineChartView lineChartView_ecg;
    List<Double> queueEcg = new ArrayList<>();
    List<Float> queuePpg = new ArrayList<>();
    private int index = 0;
    int raw_data_index = 0;
    private int handStatus;
    Disposable ppgDisposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);
        ButterKnife.bind(this);
        /**
         * 请求读写权限 Request read and write permission
         */
        PermissionsUtil.requestPermissions( EcgActivity.this, new PermissionsUtil.PermissionListener() {
            @Override
            public void granted(String name) {
                sendValue(BleSDK.GetDeviceInfo());
            }
            @Override
            public void NeverAskAgain() { }
            @Override
            public void disallow(String name) { }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        radioGroup_mian.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_1:
                        MeasureTimes = 90;
                        break;
                    case R.id.radio_2:
                        MeasureTimes = 300;
                        break;
                    case R.id.radio_3:
                        MeasureTimes = 400;
                        break;
                }
            }
        });


    }
    private NskAlgoSdk nskAlgoSdk;
    private void init() {
        queueEcg.clear();
        queuePpg.clear();
        nskAlgoSdk = new NskAlgoSdk();
        nskAlgoSdk.NskAlgoUninit();
        nskAlgoSdk.setOnECGAlgoIndexListener(new NskAlgoSdk.OnECGAlgoIndexListener() {
            @Override
            public void onECGAlgoIndex(int type, final int value) {
                switch (type) {
                    case NskAlgoECGValueType.NSK_ALGO_ECG_VALUE_TYPE_SMOOTH:
                        queueEcg.add((double) value);
                        int size = queueEcg.size();
                        if (size >= 1536) {
                            index++;
                        }
                        Log.e("sdadasd","onECGAlgoIndex");
                        if (size % 79 == 0) {
                            lineChartView_ecg.setLineChartData(ChartDataUtil.getEcgLineChartData(EcgActivity.this, queueEcg, Color.RED, 4, index));
                        }
                        break;
                    case NskAlgoECGValueType.NSK_ALGO_ECG_VALUE_TYPE_ROBUST_HR:
                        break;
                    case NskAlgoECGValueType.NSK_ALGO_ECG_VALUE_TYPE_HRV:
                        break;
                }
            }
        });

        /**
         * 设置ecg ppg 波形过滤数据 Set ECG and PPG waveform filtering data
         */
        loadsetup();
        setAlgos();
        nskAlgoSdk.NskAlgoStart(false);
         ChartDataUtil.initDataChartView(lineChartView_ecg, 0, 8000, 1536, -8000);
        lineChartView_ecg.setLineChartData(ChartDataUtil.getEcgLineChartData(EcgActivity.this, queueEcg, Color.RED, 4, index));

    }

    private int MeasureTimes = 90;

    @OnClick({R.id.open,R.id.close})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.open://开启ecg测量 Start ECG measurement
                sendValue(BleSDK.OpenECGPPG(4,MeasureTimes));
                break;
            case R.id.close://关闭ecg测量 Turn off ECG measurement
                sendValue(BleSDK.stopEcgPPg());
                break;
        }

    }


    @Override
    public void dataCallback(byte[] value) {
        switch (value[0]) {
            case DeviceConst.CMD_ECGDATA://ecg 实时数据返回 ECG real-time data return
                if (raw_data_index == 0 || raw_data_index % 200 == 0) {
                    // send the good signal for every half second
                    short pqValue[] = {(short) 200};
                    nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_ECG_PQ, pqValue, 1);
                }
                Log.e("jdjdjdj", value.length+"");
                for (int i = 0; i < value.length / 2 - 1; i++) {
                    int ecgValueAction = ResolveUtil.getValue(value[i * 2 + 1], 1) + ResolveUtil.getValue(value[i * 2 + 2], 0);
                    if (ecgValueAction >= 32768) ecgValueAction = ecgValueAction - 65536;
                    raw_data_index++;
                    short[] ecgData = new short[]{(short) -ecgValueAction};
                    nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_ECG, ecgData, 1);//this
                }
                break;
            case  DeviceConst.CMD_PPGGDATA://PPG 实时数据返回 PPG real-time data return
                double maxPPG = 0;
                double minPPg = 33000;
                for (int i = 0; i < value.length / 2 - 1; i++) {
                    float ppgValue = ResolveUtil.getValue(value[i * 2 + 1], 1) + ResolveUtil.getValue(value[i * 2 + 2], 0);
                    if (ppgValue >= 32768) ppgValue = ppgValue - 65536;
                    if (queuePpg.size() > 600) queuePpg.remove(0);
                    ppgValue=ppgValue*(handStatus*2-1);
                    queuePpg.add(ppgValue);
                    maxPPG = Math.max(maxPPG, ppgValue);
                    minPPg = Math.min(minPPg, ppgValue);
                }
                break;
            case DeviceConst.CMD_Get_DeviceInfo:
                 handStatus = ResolveUtil.getValue(value[4], 0);//1左手，0右手
                 init();
                break;

        }
    }
    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType = getDataType(maps);
        switch (dataType) {
            case BleConst.EcgppG:
                Map<String, Object> mapsa=(Map<String, Object>)maps.get(DeviceKey.Data);
                heartValue.setText("heartValue: "+mapsa.get(DeviceKey.heartValue).toString());//心率值 Heart rate value
                hrvValue.setText("hrvValue: "+mapsa.get(DeviceKey.hrvValue).toString());//心率变异性 Heart rate variability
                Quality.setText("Quality: "+mapsa.get(DeviceKey.Quality).toString());//信号质量 Signal quality
                break;
            case BleConst.EcgppGstatus:
                Map<String, Object> mp=(Map<String, Object>)maps.get(DeviceKey.Data);
                info.setText(mp.get(DeviceKey.EcgStatus).toString()+"");
               /* status为0,表示打开ECG+PPG实时上传成功  If the status is 0, it means that the ECG + PPG real-time upload is successful
                status为1,表示已经处于该模式,    Status is 1, indicating that it is already in this mode
                status为2,表示当前处于运动模式而无法打开。  Status is 2, which means it is currently in motion mode and cannot be opened.
                status为3，表示处于充电界面无法打开。  Status 3 indicates that the charging interface cannot be opened
                status 为4，表示处于SOS模式无法打开。 Status is 4, which means it is in SOS mode and cannot be opened.
                status为5，表示当前在UI升级模式无法打开  status为5，表示当前在UI升级模式无法打开
                */
            case BleConst.ECG:
                info.setText(maps.toString());
                break;
            case  BleConst.GetEcgPpgStatus:
                info.setText(maps.toString());
                break;
        }

    }




    private String license = "";
    private void loadsetup() {
        AssetManager assetManager = this.getAssets();
        InputStream inputStream = null;

        try {
            String prefix = "license key=\"";
            String suffix = "\"";
            String pattern = prefix + "(.+?)" + suffix;
            Pattern p = Pattern.compile(pattern);

            inputStream = assetManager.open("license.txt");
            ArrayList<String> data = new ArrayList<String>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                while (true) {
                    String line = reader.readLine();
                    if (line == null || line.isEmpty())
                        break;
                    Matcher m = p.matcher(line);
                    if (m.find()) {
                        license = line.substring(m.regionStart() + prefix.length(), m.regionEnd() - suffix.length());
                        break;
                    }
                }
            } catch (IOException e) {

            }
            inputStream.close();
        } catch (IOException e) { }
        try {
            inputStream = assetManager.open("setupinfo.txt");
            ArrayList<String> data = new ArrayList<String>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                while (true) {
                    String line = reader.readLine();
                    if (line == null || line.isEmpty()) {
                        break;
                    }
                    data.add(line);
                }
            } catch (IOException e) { }
            inputStream.close();
        } catch (IOException e) { }
    }


    private int activeProfile = -1;
    private int currentSelectedAlgo;
    private void setAlgos() {
        String path = this.getFilesDir().getAbsolutePath();
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_HRVFD;
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_HRVTD;
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_HRV;
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_SMOOTH;
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_HEARTAGE;
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_HEARTRATE;
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_MOOD;
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_RESPIRATORY;
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_STRESS;
        int ret = nskAlgoSdk.NskAlgoInit(currentSelectedAlgo, path, license);
        if (ret == 0) {
            Log.i(TAG, "setAlgos: Algo SDK has been initialized successfully");
        } else {
            Log.i(TAG, "Failed to initialize the SDK, code = " + String.valueOf(ret));
            return;
        }
        boolean b = nskAlgoSdk.setBaudRate(NskAlgoDataType.NSK_ALGO_DATA_TYPE_ECG, NskAlgoSampleRate.NSK_ALGO_SAMPLE_RATE_512);
        if (b != true) {
            Log.i(TAG, "setAlgos: Failed to set the sampling rate");
            return;
        }
        NskAlgoProfile[] profiles = nskAlgoSdk.NskAlgoProfiles();
        if (profiles.length == 0) {
            // create a default profile
            try {
                String dobStr = "1995-1-1";
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date dob = df.parse(dobStr);

                NskAlgoProfile profile = new NskAlgoProfile();
                profile.name = "bob";
                profile.height = 170;
                profile.weight = 80;
                profile.gender = false;
                profile.dob = dob;
                if (nskAlgoSdk.NskAlgoProfileUpdate(profile) == false) { }

                profiles = nskAlgoSdk.NskAlgoProfiles();
                // setup the ECG config
                // assert(nskAlgoSdk.NskAlgoSetECGConfigAfib((float)3.5) == true);
                assert (nskAlgoSdk.NskAlgoSetECGConfigStress(30, 30) == true);
                assert (nskAlgoSdk.NskAlgoSetECGConfigHeartage(30) == true);
                assert (nskAlgoSdk.NskAlgoSetECGConfigHRV(30) == true);
                assert (nskAlgoSdk.NskAlgoSetECGConfigHRVTD(30, 30) == true);
                assert (nskAlgoSdk.NskAlgoSetECGConfigHRVFD(30, 30) == true);

                // nskAlgoSdk.setSignalQualityWatchDog((short)20, (short)5);
                // retrieve the baseline data
                if (profiles.length > 0) {
                    activeProfile = profiles[0].userId;
                    nskAlgoSdk.NskAlgoProfileActive(activeProfile);
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                    String stringArray = settings.getString("ecgbaseline", null);
                    if (stringArray != null) {
                        String[] split = stringArray.substring(1, stringArray.length() - 1).split(", ");
                        byte[] array = new byte[split.length];
                        for (int i = 0; i < split.length; ++i) {
                            array[i] = Byte.parseByte(split[i]);
                        }
                        if (nskAlgoSdk.NskAlgoProfileSetBaseline(activeProfile, NskAlgoType.NSK_ALGO_TYPE_ECG_HEARTRATE, array) != true) {
                        }
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }










    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ppgDisposable != null && !ppgDisposable.isDisposed()) {
            ppgDisposable.dispose();
        }
    }
}
