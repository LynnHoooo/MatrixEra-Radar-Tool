package com.jstyle.test2208.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.callback.DataListener2025;
import com.jstyle.blesdk2208.constant.DeviceKey;
import com.jstyle.test2208.R;
import com.jstyle.test2208.Util.BleData;
import com.jstyle.test2208.Util.RxBus;
import com.jstyle.test2208.ble.BleManager;
import com.jstyle.test2208.ble.BleService;


import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/4/26.
 */

public  class BaseActivity extends AppCompatActivity implements DataListener2025 {

    private Disposable subscription;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscribe();
    }

    protected void subscribe(){
        subscription = RxBus.getInstance().toObservable(BleData.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<BleData>() {
            @Override
            public void accept(BleData bleData) throws Exception {
                String action = bleData.getAction();
                if (action.equals(BleService.ACTION_DATA_AVAILABLE)) {
                    byte[]value=bleData.getValue();
                    if((byte)0x3b==value[0]){
                        on3bBind(value);
                    }else{
                        BleSDK.DataParsingWithData(value,BaseActivity.this);
                    }
                }

            }
        });
    }
    protected void on3bBind( byte[]value) { }
    protected void unSubscribe(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unSubscribe(subscription);
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {

    }

    @Override
    public void dataCallback(byte[] value) {

    }


    /**
     * 当蓝牙设备是连接的状态，发送指令给设备
     * When the Bluetooth device is connected, send instructions to the device
     * @param value
     */
    protected void sendValue(byte[]value){
        if(!BleManager.getInstance().isConnected()){
            showToast(getString(R.string.pair_device));
            return;
        }
        if(value==null)return;
       BleManager.getInstance().writeValue(value);
    }
    protected void showToast(String text){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }

    AlertDialog alertDialog=null;
    protected void showDialogInfo(String message){
        if(null==alertDialog){
            alertDialog=  new AlertDialog.Builder(this)
                    .setMessage(message).setPositiveButton("Ok",null).create();
            alertDialog.show();
        }else{
            alertDialog.dismiss();
            alertDialog=null;
            alertDialog=  new AlertDialog.Builder(this)
                    .setMessage(message).setPositiveButton("Ok",null).create();
            alertDialog.show();
        }

    }
    protected void showSetSuccessfulDialogInfo(String message){
        new AlertDialog.Builder(this)
                .setMessage(message+" Successful").setPositiveButton("Ok",null).create().show();
    }

    protected String getDataType(Map<String, Object> maps){
        if(null==maps){
            return "";
        }
        return (String) maps.get(DeviceKey.DataType);
    }
    protected boolean getEnd(Map<String, Object> maps){
        return (boolean) maps.get(DeviceKey.End);
    }
    protected Map<String, String> getData(Map<String, Object> maps){
        return (Map<String, String>) maps.get(DeviceKey.Data);
    }
    protected Map<String, Object> getDataObject(Map<String, Object> maps){
        return (Map<String, Object>) maps.get(DeviceKey.Data);
    }

    protected void offerData(byte[]value){
        BleManager.getInstance().offerValue(value);
    }
    protected void offerData(){

        BleManager.getInstance().writeValue();
    }
    protected void showProgressDialog(String message){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage(message);
        }
        if(!progressDialog.isShowing())progressDialog.show();
    }
    protected void disMissProgressDialog(){
        if(progressDialog!=null&&progressDialog.isShowing())progressDialog.dismiss();
    }
}
