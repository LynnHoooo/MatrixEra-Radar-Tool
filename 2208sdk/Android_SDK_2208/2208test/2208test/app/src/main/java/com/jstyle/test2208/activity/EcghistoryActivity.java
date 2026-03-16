package com.jstyle.test2208.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.jstyle.test2208.R;
import com.jstyle.test2208.Util.ChartDataUtil;
import com.jstyle.test2208.daomananger.EcgDataDaoManager;
import com.jstyle.test2208.model.EcgHistoryData;
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
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lecho.lib.hellocharts.view.LineChartView;

public class EcghistoryActivity extends BaseActivity {

    @BindView(R.id.LineChartView_ecgReport)
    LineChartView LineChartView_ecgReport;
    String time="";
    String address="";
    EcgHistoryData ecgHistoryData=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ecg_report);
        ButterKnife.bind(this);
        time=getIntent().getStringExtra("time");
        address=getIntent().getStringExtra("address");
        ecgHistoryData= EcgDataDaoManager.queryEcgHistoryData(address,time);
        if(null==ecgHistoryData){
            showToast("not find");
        }else{
            init();
        }

    }

    int raw_data_index = 0;
    private NskAlgoSdk nskAlgoSdk;
    boolean enddata=false;
     List<Integer> filterEcg;
    @SuppressLint("HandlerLeak")
    private void init()  {
        showProgressDialog("Please wait...");
        String[] ecg=ecgHistoryData.getArrayECGData().split(",");
        List<Integer>  ecgData=new ArrayList<>();
        for (String e:ecg){
            ecgData.add(Integer.parseInt(e));
        }
        raw_data_index = 0;
        enddata=false;
        nskAlgoSdk = new NskAlgoSdk();
        nskAlgoSdk.NskAlgoUninit();
        loadsetup();
        setAlgos();
        nskAlgoSdk.NskAlgoStart(false);
       filterEcg=new ArrayList<>();
        nskAlgoSdk.setOnECGAlgoIndexListener(new NskAlgoSdk.OnECGAlgoIndexListener() {
            @Override
            public void onECGAlgoIndex(int type,  int value) {
                switch (type) {
                    case NskAlgoECGValueType.NSK_ALGO_ECG_VALUE_TYPE_SMOOTH:
                        Log.e("sdadasd",value+"***");
                        if(value>8000){value=8000;}
                        if(value<-8000){value=-8000;}
                        filterEcg.add(value);
                        break;
                    case NskAlgoECGValueType.NSK_ALGO_ECG_VALUE_TYPE_ROBUST_HR:
                        break;
                    case NskAlgoECGValueType.NSK_ALGO_ECG_VALUE_TYPE_HRV:
                        break;
                }
            }
        });

        queues = new LinkedList<>();
        queues.addAll(ecgData);
        uihandlerecg= new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int value= (int) msg.obj;
                if (raw_data_index == 0 || raw_data_index % 200 == 0) {
                    short pqValue[] = {(short) 200};
                    nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_ECG_PQ, pqValue, 1);
                }
                if (value >= 32768) value = value - 65536;
                short[] ecg= new short[]{(short) -value};
                raw_data_index++;
                nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_ECG, ecg, 1);//this
            }
        };


/*    for (int i = 0; i < ecgData.size(); i++) {
        if (i==ecgData.size()-1) {
            Close();
            disMissProgressDialog();
            showEcgChart(filterEcg,8000,-8000);
        }else{
            if (raw_data_index == 0 || raw_data_index % 200 == 0) {
                short pqValue[] = {(short) 200};
                nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_ECG_PQ, pqValue, 1);
            }
            int value=ecgData.get(i);
            if (value >= 32768) value = value - 65536;
            short[] ecg= new short[]{(short) -value};
            raw_data_index++;
            nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_ECG, ecg, 1);//this
        }
        }*/

        Start();

    }






    private ScheduledThreadPoolExecutor timer = null;
    private Handler uihandlerecg=null;
    private Deque queues;
    private  void Start(){
        if(null==timer||timer.isShutdown()){
            timer = new ScheduledThreadPoolExecutor(1);
            timer.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        Deque<Integer> requests = queues;
                        Integer value = requests.pollLast();
                        if (null != value) {
                            Message message = new Message();
                            message.obj=value;
                            uihandlerecg.sendMessage(message);
                        }else{
                            Close();
                            disMissProgressDialog();
                            showEcgChart(filterEcg,8000,-8000);
                        }
                    }catch (Throwable e){ e.printStackTrace(); }
                }
            },0, 1L, TimeUnit.MILLISECONDS);

        }

    }
    private void  Close(){
        try {
            if(null!=timer){
                timer.shutdownNow();
                timer=null;
            }
            if(null!=uihandlerecg){
                uihandlerecg.removeCallbacksAndMessages(null);
                uihandlerecg=null;
            }
        }catch (Exception e) {  }
    }

    private void showEcgChart(List<Integer>data,int top,int bottom){
        ChartDataUtil.initDataSleepChartView(LineChartView_ecgReport, 0, top, data.size(), bottom, 2560, 0);
        LineChartView_ecgReport.setLineChartData(ChartDataUtil.getEcgReportLineChartData(this, data, Color.RED));
    }

    @OnClick({R.id.bt_ecgReport_back,R.id.bt_reviewPDF,R.id.iv_ecgReport_share})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.bt_ecgReport_back:
                finish();
                break;
            case R.id.bt_reviewPDF:
            case R.id.iv_ecgReport_share:
                if(0<filterEcg.size()){
                    Intent intent=    new Intent(this,PDFActivity.class);
                    intent.putIntegerArrayListExtra("ecgData", (ArrayList<Integer>) filterEcg)  ;
                    startActivity(intent);
                }
                break;
        }

    }













    private List<Integer> readFile2(int path) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.getResources().openRawResource(path )));
        List<String> list=new ArrayList<String>();
        try {
            String line = null;
            //因为不知道有几行数据，所以先存入list集合中
            while((line = reader.readLine()) != null){
                if(!"".equals(line)){
                    list.add(line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if(reader != null){ reader.close();
                    reader=null;}
            }catch(Exception e){ }
        }
        String valueString="";
        for (int i=0;i<list.size();i++){
            valueString+=list.get(i).trim();
        }
        String[] dataString = valueString.split(",");
        List<Integer> data = new ArrayList<>();
        for (String value : dataString) {//纯数字写进null字符串?
            // Log.i(TAG, "readFile2: "+value);
            if (TextUtils.isEmpty(value) ||"null".equals(value) ||"-".equals(value)) continue;
            if(value.contains("-")){
                if(value.lastIndexOf("-")==0)  data.add(Integer.parseInt(value));
            }else{
                Integer doubleValue=Integer.parseInt(value);
                /*if(doubleValue>8000)doubleValue=8000d;
                if(doubleValue<-8000)doubleValue=-8000d;*/
                data.add(doubleValue);
            }

        }
        return data;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(null!=timer){
                timer.shutdownNow();
                timer=null;
            }
            if(null!=uihandlerecg){
                uihandlerecg.removeCallbacksAndMessages(null);
                uihandlerecg=null;
            }
        }catch (Exception e) {  }

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
        } else {
            return;
        }
        boolean b = nskAlgoSdk.setBaudRate(NskAlgoDataType.NSK_ALGO_DATA_TYPE_ECG, NskAlgoSampleRate.NSK_ALGO_SAMPLE_RATE_512);
        if (b != true) {
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

}
