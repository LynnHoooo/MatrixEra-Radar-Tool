package com.jstyle.test2208.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.jstyle.blesdk2208.Util.BleSDK;
import com.jstyle.blesdk2208.constant.BleConst;
import com.jstyle.test2208.R;
import com.jstyle.test2208.adapter.WorkOutTypeAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SportModeActivity extends BaseActivity {
    @BindView(R.id.RecyclerView_mode)
    RecyclerView RecyclerViewMode;
    @BindArray(R.array.sport)
    String[] modeName;
    private WorkOutTypeAdapter activityModeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);
        ButterKnife.bind(this);
        init();

    }

    private void init() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        List<String> list = Arrays.asList(modeName);
        activityModeAdapter = new WorkOutTypeAdapter(list);
        RecyclerViewMode.setLayoutManager(layoutManager);
        RecyclerViewMode.setAdapter(activityModeAdapter);
    }


    @OnClick({R.id.set_1, R.id.Get_1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.set_1:
                List<Integer> integerList = activityModeAdapter.getSelectedList();
                if (integerList.size() != 0) {
                    sendValue(BleSDK.SportMode(integerList));
                } else {
                    showToast("Select at least one sport mode");
                }
                break;
            case R.id.Get_1:
                sendValue(BleSDK.GetSportMode());
                break;
        }

    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType = getDataType(maps);
        Log.e("info",maps.toString());
        switch (dataType) {
            case BleConst.SportMode:
                showDialogInfo(maps.toString());
                break;
            case BleConst.GetSportMode:
                showDialogInfo(maps.toString());
                break;

        }
    }
}
