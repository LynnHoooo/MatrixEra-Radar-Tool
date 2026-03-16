package com.jstyle.test2208.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.test2208.R;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * 恢复出厂设置
 * Restore factory settings
 */
public class ExfactoryActivity extends BaseActivity {
    @BindView(R.id.info)
    TextView info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factory);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.set})
    public void onViewClicked(View view) {
     switch (view.getId()){
         case R.id.set://恢复出厂设置 Restore factory settings
             showResetDialog();
             break;
     }
    }

    private void showResetDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.Restore_factory))
                .setMessage(getString(R.string.Restore_factory_tips))
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendValue(BleSDK.Reset());
                    }
                })
                .setNegativeButton("Cancel", null)
                .create().show();
    }


    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        Log.e("info",maps.toString());
        String dataType = getDataType(maps);
        switch (dataType) {
            case BleConst.CMD_Reset://发送恢复出厂设置后返回的字段Send the fields returned after restoring the factory settings
                if(null!=info){
                    info.setText(maps.toString());
                }
                break;
        }
    }

}
