package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.blesdk2208.constant.DeviceKey;
import com.jstyle.test2208.R;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置/获取步数目标 （Set / get step target）
 */
public class SetGoalActivity extends BaseActivity {
    @BindView(R.id.editText_goal)
    EditText editTextGoal;
    @BindView(R.id.button_goal_set)
    Button buttonGoalSet;
    @BindView(R.id.button_goal_get)
    Button buttonGoalGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button_goal_set, R.id.button_goal_get})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_goal_set://设置步数目标 Set step target
                setGoal();
                break;
            case R.id.button_goal_get://获取步数目标 Get step target
                sendValue(BleSDK.GetStepGoal());
                break;
        }
    }

    private void setGoal() {
        String goalString=editTextGoal.getText().toString();
        if(TextUtils.isEmpty(goalString))return;
        int goal=Integer.valueOf(editTextGoal.getText().toString());
        sendValue(BleSDK.SetStepGoal(goal));
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        Log.e("info",maps.toString());
        String dataType= getDataType(maps);
        Map<String,String>data= getData(maps);
        switch (dataType){
            case BleConst.SetStepGoal://设置步数目标结果返回 Set the target number of steps and return the result
                showSetSuccessfulDialogInfo(dataType);
                break;
            case BleConst.GetStepGoal://获取步数目标结果返回 Get step target result return
                String goal=data.get(DeviceKey.StepGoal);
                editTextGoal.setText(goal);
                break;
        }
    }
}
