package com.jstyle.test2208.ble;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.jstyle.blesdk2208.callback.OnScanResults;
import com.jstyle.blesdk2208.model.Device;

import java.util.List;
import java.util.Set;

/**
 * 蓝牙管理类 Bluetooth management class
 */

public class BleManager {
    private static BleManager ourInstance;
    private String address;
    private BleService bleService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {// TODO Auto-generated method stub
            bleService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleService = ((BleService.LocalBinder) service).getService();
            if(!TextUtils.isEmpty(address)){
                bleService.initBluetoothDevice(address,context);
            }
        }
    };
    private Intent serviceIntent;
    BluetoothAdapter bluetoothAdapter;
    Context context;

    private BleManager(Context context) {
        this.context = context;
        if (serviceIntent == null) {
            serviceIntent = new Intent(context, BleService.class);
            context.bindService(serviceIntent, serviceConnection,
                    Service.BIND_AUTO_CREATE);
        }
        BluetoothManager bluetoothManager = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter=bluetoothManager.getAdapter();
    }

    public static void init(Context context){
        if (ourInstance == null) {
            synchronized (BleManager.class) {
                if (ourInstance == null) {
                    ourInstance = new BleManager(context);
                }
            }
        }
    }

    public  boolean isBleEnable(){
        return bluetoothAdapter.enable();
    }
    public static BleManager getInstance() {
        return ourInstance;
    }


    /**
     * 连接蓝牙设备
     * Connect Bluetooth device
     * @param address
     */

    public void connectDevice(String address){
        if(!bluetoothAdapter.isEnabled()|| TextUtils.isEmpty(address)||isConnected())return;

        if(bleService==null){
            this.address=address;
        }else{
            bleService.initBluetoothDevice(address,this.context);
        }
    }


    /**
     * 关闭蓝牙服务通知
     * Turn off Bluetooth service notification
     */
    public void enableNotifaction(){
        if(bleService==null)return;
        bleService.setCharacteristicNotification(true);
    }


    /**
     * 写入指令到蓝牙设备
     * Write command to Bluetooth device
     */
    public void writeValue(byte[]value){
        if(bleService==null||ourInstance==null||!isConnected())return;
        bleService.writeValue(value);
    }


    /**
     * 多条指令同时添加后发送到设备
     * Multiple instructions are added at the same time and sent to the device
     * @param data
     */
    public void offerValue(byte[]data){
        if(bleService==null)return;
        bleService.offerValue(data);
    }


    /**
     * 写入指令到蓝牙设备
     * Write command to Bluetooth device
     */
    public void writeValue(){
        if(bleService==null)return;
        bleService.nextQueue();
    }


    /**
     * 断开设备  Disconnect the device
     */
    public void disconnectDevice(){
        if(bleService==null)return;
        bleService.disconnect();
    }


    /**
     * 查询设备是否已经连接
     * Query whether the device is connected
     * @return
     */
    public boolean isConnected(){
        if(bleService==null)return false;
        return bleService.isConnected();
    }


    /**
     * 取消扫描设备
     * Cancel scanning device
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void StopDeviceScan() {
        isinScan = false;
        if (null !=bluetoothAdapter && null != bluetoothAdapter.getBluetoothLeScanner()) {
            bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        }
    }


    /**
     * 扫描蓝牙设备,用于开始绑定设备
     * Scan Bluetooth device to start binding device
     */
    private String[] DevicesName = null;
    private OnScanResults onScanResult = null;
    private boolean isinScan = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void DeviceScanResults(@NonNull String []Devicesname, @NonNull OnScanResults onScanResults) {
        if (!isinScan && ourInstance.isBleEnable()) {//如果蓝牙开关是开启的就进行扫描
            isinScan = true;
            DevicesName = Devicesname;
            onScanResult = onScanResults;
            //返回已配对设备
            //获得已配对的远程蓝牙设备的集合
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            if (devices.size() > 0) {
                for (BluetoothDevice bluetoothDevice : devices) {
                    if (Canctians(bluetoothDevice.getName().toLowerCase())) {
                        Device device = new Device();
                        device.setBluetoothDevice(bluetoothDevice);
                        device.setIsconted(false);
                        device.setPaired(true);
                        device.setIsdfu(bluetoothDevice.getName().toLowerCase().contains("dfu"));
                        device.setName(bluetoothDevice.getName());
                        device.setMac(bluetoothDevice.getAddress());
                        onScanResult.Success(device);
                    }
                }
            }
            bluetoothAdapter.getBluetoothLeScanner().startScan(null, new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(), scanCallback);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            String deviceName = result.getDevice().getName();
            if (isinScan && !TextUtils.isEmpty(deviceName) && Canctians(deviceName.toLowerCase().trim())) {
                Device device = new Device();
                device.setBluetoothDevice(result.getDevice());
                device.setIsconted(false);
                device.setPaired(false);
                device.setIsdfu(deviceName.toLowerCase().contains("dfu"));
                device.setName(result.getDevice().getName());
                device.setMac(result.getDevice().getAddress());
                device.setRiss(result.getRssi());
                onScanResult.Success(device);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            onScanResult.Fail(errorCode);
        }
    };
    private boolean Canctians(@NonNull String name){
        boolean catian=false;
        if(null==DevicesName||DevicesName.length==0){
            return false;
        }else{
                for (String devicesname: DevicesName){
                    if(name.toLowerCase().contains(devicesname.toLowerCase())){
                        catian=true;
                        break;
                    }}
        }
        return catian;
    }
}
