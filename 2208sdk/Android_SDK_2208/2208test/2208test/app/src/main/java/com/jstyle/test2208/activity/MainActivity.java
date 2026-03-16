package com.jstyle.test2208.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.constant.DeviceKey;
import com.jstyle.test2208.R;
import com.jstyle.test2208.Util.BleData;
import com.jstyle.test2208.Util.PermissionsUtil;
import com.jstyle.test2208.Util.RxBus;
import com.jstyle.test2208.Util.SDUtil;
import com.jstyle.test2208.adapter.MainAdapter;
import com.jstyle.test2208.ble.BleManager;
import com.jstyle.test2208.ble.BleService;

import java.io.File;
import java.util.Map;
import java.util.Objects;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements MainAdapter.onItemClickListener {
    private static final String TAG = "MainActivity";
    @BindView(R.id.main_recyclerview)
    RecyclerView mainRecyclerview;
    @BindArray(R.array.item_options)
    String[] options;
    @BindView(R.id.BT_CONNECT)
    Button btConnect;


    private ProgressDialog progressDialog;
    private Disposable subscription;
    private String address;
    boolean isStartReal;
    public static int phoneDataLength = 200;//手机一个包能发送的最多数据
    private ListenerReceiver receiver;
    protected static  final  int MY_PERMISSIONS_REQUEST_CALL_PHONE=321;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        connectDevice();
        registerReceiver();
        //创建文件 createFile
        requestPermission(MainActivity.this);

    }

    private void requestPermission(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                SDUtil.createFile("log");
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                startActivityForResult(intent, 0);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 先判断有没有权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                SDUtil.createFile("log");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }else{
            SDUtil.createFile("log");
        }
    }

    /**
     * 蓝牙状态监听
     *Bluetooth status monitoring
     */
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        receiver = new ListenerReceiver();
        registerReceiver(receiver, filter);
    }

    protected  class ListenerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction()).equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_ON://Bluetooth on
                        if (TextUtils.isEmpty(address)) {
                            Log.i(TAG, "onCreate: address null ");
                            return;
                        }
                        BleManager.getInstance().connectDevice(address);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        if(null!=mainAdapter){
                            mainAdapter.setEnable(false);
                        }
                        if(null!=btConnect){
                        btConnect.setEnabled(true);}
                        BleManager.getInstance().disconnectDevice();
                        break;

                }
            }
        }
    }




    private void connectDevice() {
        address = getIntent().getStringExtra("address");
        if (TextUtils.isEmpty(address)) {
            Log.i(TAG, "onCreate: address null ");
            return;
        }
        Log.i(TAG, "onCreate: ");
        BleManager.getInstance().connectDevice(address);
        showConnectDialog();
    }

    private void showConnectDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.connectting));
        if (!progressDialog.isShowing()) progressDialog.show();

    }

    private void dissMissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }
    MainAdapter mainAdapter;
    private void init() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mainRecyclerview.setLayoutManager(gridLayoutManager);
         mainAdapter = new MainAdapter(options, this);
        mainRecyclerview.setAdapter(mainAdapter);
        subscription = RxBus.getInstance().toObservable(BleData.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<BleData>() {
            @Override
            public void accept(BleData bleData) throws Exception {
                String action = bleData.getAction();
                if (action.equals(BleService.ACTION_GATT_onDescriptorWrite)) {
                    mainAdapter.setEnable(true);
                    btConnect.setEnabled(false);
                    dissMissDialog();
                } else if (action.equals(BleService.ACTION_GATT_DISCONNECTED)) {
                    mainAdapter.setEnable(false);
                    btConnect.setEnabled(true);

                    isStartReal = false;
                    dissMissDialog();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unsubscribe();
        if (BleManager.getInstance().isConnected()) BleManager.getInstance().disconnectDevice();
    }

    private void unsubscribe() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
            Log.i(TAG, "unSubscribe: ");
        }
    }



    @Override
    public void onItemClick(int position) {
        Intent intent;
        switch (position) {
            case 0:
                startActivity(new Intent(MainActivity.this, TimeActivity.class));
                break;
           case 1:
                startActivity(new Intent(MainActivity.this, BasicActivity.class));
                break;
             case 2:
                startActivity(new Intent(MainActivity.this, DeviceInfoActivity.class));
                break;
             case 3:
                startActivity(new Intent(MainActivity.this, ExfactoryActivity.class));
                break;
            case 4:
                startActivity(new Intent(MainActivity.this, BatteryActivity.class));
                break;
            case 5:
                startActivity(new Intent(MainActivity.this, MacActivity.class));
                break;
            case 6:
                startActivity(new Intent(MainActivity.this, VersionActivity.class));
                break;
             case 7:
                startActivity(new Intent(MainActivity.this, MCUActivity.class));
                break;
             case 8:
                startActivity(new Intent(MainActivity.this, MotorVibrationActivity.class));
                break;
            case 9:
                startActivity(new Intent(MainActivity.this, RealTimeStepCountingActivity.class));
                break;
            case 10:
                startActivity(new Intent(MainActivity.this, SetGoalActivity.class));
                break;
            case 11:
                startActivity(new Intent(MainActivity.this, AutoModeSetActivity.class));
                break;
            case 12:
                startActivity(new Intent(MainActivity.this, AlarmListActivity.class));
                break;
            case 13:
                startActivity( new Intent(MainActivity.this, NotifyActivity.class));
                break;
            case 14:
                startActivity( new Intent(MainActivity.this, ActivityAlarmSetActivity.class));
                break;
            case 15:
                startActivity(new Intent(MainActivity.this, TotalDataActivity.class));
                break;
            case 16:
                startActivity(new Intent(MainActivity.this, DetailDataActivity.class));
                break;
            case 17:
                startActivity(new Intent(MainActivity.this, DetailSleepActivity.class));
                break;
            case 18:
                startActivity(new Intent(MainActivity.this, HeartRateInfoActivity.class));
                break;
            case 19:
                startActivity(new Intent(MainActivity.this, HeartRateStaticInfoActivity.class));
                break;
            case 20:
                startActivity(new Intent(MainActivity.this, HrvDataReadActivity.class));
                break;
            case 21:
                startActivity(new Intent(MainActivity.this, ExerciseHistoryDataActivity.class));
                break;
            case 22:
                startActivity(new Intent(MainActivity.this, ActivityModeActivity.class));
                break;
            case 23:
                startActivity(new Intent(MainActivity.this, Manually_test_blood_oxygen_dataActivity.class));
                break;
            case 24:
                startActivity(new Intent(MainActivity.this, Automatically_test_blood_oxygen_dataActivity.class));
                break;
            case 25:
                startActivity(new Intent(MainActivity.this, MeasurementActivity.class));
                break;
           case 26:
                startActivity(new Intent(MainActivity.this, BloodsugarActivity.class));
                break;
            case 27:
                String path=SDUtil.log+"log.txt";
                File F=new File(path);
                if(F.exists()){
                    SDUtil.sharePdfByPhone(MainActivity.this,path);
                }else{
                    showToast("The log file does not exist");
                }
                break;
          /*  case 37:
                sendValue(BleSDK.TestVlue());
                break;*/
            /*case 37:
                startActivity(new Intent(MainActivity.this, PRIActivity.class));
                break;*/
             /*  case 5:
                startActivity(new Intent(MainActivity.this, HeartRateSetActivity.class));
                break;
            case 7:
                startActivity(new Intent(MainActivity.this, ActivityAlarmSetActivity.class));
                break;

            case 9:
                startActivity( new Intent(MainActivity.this, HrvDataReadActivity.class));
                break;

            case 13:
                startActivity(new Intent(MainActivity.this, BloodOxygenActivity.class));
                break;
            case 14:
                startActivity(new Intent(MainActivity.this, SocialDistanceActivity.class));
                break;
            case 15:
                startActivity(new Intent(MainActivity.this, TemperatureHistoryActivity.class));

                break;
            case 16:
                startActivity(new Intent(MainActivity.this, MacActivity.class));
                break;

            case 18:
                startActivity(new Intent(MainActivity.this, EcgPPgStatusActivity.class));
                break;
             case 19:
                 Intent intent1=      new Intent(MainActivity.this, EcgDataActivity.class);
                 intent1.putExtra("address",address) ;
                startActivity(intent1);
                break;
             case 20:
                startActivity(new Intent(MainActivity.this, MeasurementActivity.class));
                break;

            */

            default:
                break;
        }

    }

    @Override
    public void dataCallback(Map<String, Object> map) {
        super.dataCallback(map);
        String dataType = getDataType(map);
        Log.e("info",map.toString());
        switch (dataType) {
            case BleConst.ReadSerialNumber:
                showDialogInfo(map.toString());
                break;

            case BleConst.DeviceSendDataToAPP:
                Map<String,String>dd= getData(map);
                String type = dd.get(DeviceKey.type);
                switch (type){
                    case DeviceKey.HangUp://挂断电话HangUp
                        break;
                    case DeviceKey.Telephone://接听电话 Answer the phone
                        break;
                    case DeviceKey.Photograph://收到拍照指令 Receive photo instruction
                        break;
                    case DeviceKey.CanclePhotograph://退出了设备拍照模式Out of device photographing mode
                        break;
                    case DeviceKey.Suspend://暂停音乐 Pause music
                        break;
                    case DeviceKey.Play://开始播放音乐 Start playing music
                        break;
                    case DeviceKey.LastSong://音乐下一曲 Music next
                        break;
                    case DeviceKey.NextSong://音乐上一曲 Music last song
                        break;
                    case DeviceKey.VolumeReduction://音量- Volume-
                        break;
                   case DeviceKey.VolumeUp://音量+  Volume+
                        break;
                }

                break;
            case BleConst.FindMobilePhoneMode:
                //showDialogInfo(BleConst.FindMobilePhoneMode);
                break;
            case BleConst.RejectTelMode:
                //showDialogInfo(BleConst.RejectTelMode);
                break;
            case BleConst.TelMode:
                //showDialogInfo(BleConst.TelMode);
                break;
            case BleConst.BackHomeView:
                showToast(map.toString());
                break;
            case BleConst.Sos:
                showToast(map.toString());
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                SDUtil.createFile("log");
            }
        }
    }



}
