 package com.jstyle.test2208.activity;

 import android.os.Bundle;
 import android.util.Log;
 import android.view.View;
 import com.jstyle.blesdk2208.Util.BleSDK;
 import com.jstyle.blesdk2208.constant.BleConst;
 import com.jstyle.test2208.R;
 import java.util.Map;
 import butterknife.ButterKnife;
 import butterknife.OnClick;

 /**
  * 蓝牙设备二维码 (Bluetooth device QR code)
  */
 public class QRActivity extends BaseActivity {
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_qr);
         ButterKnife.bind(this);
     }

     @OnClick({R.id.set, R.id.get})
     public void onViewClicked(View view) {
         switch (view.getId()) {
             case R.id.set://解锁二维码 Unlock QR code
                 sendValue(BleSDK.unlockScreen());
                 break;
             case R.id.get://锁定二维码  Lock QR code
                 sendValue(BleSDK.lockScreen());
                 break;
         }
     }



     @Override
     public void dataCallback(Map<String, Object> maps) {
         super.dataCallback(maps);
         Log.e("info",maps.toString());
         String dataType= getDataType(maps);
         switch (dataType){
             case BleConst.ExitQRcode:
             case BleConst.EnterQRcode:
             case BleConst.QRcodebandBack:
                 showSetSuccessfulDialogInfo(maps.toString());
                 break;
         }
     }
 }
