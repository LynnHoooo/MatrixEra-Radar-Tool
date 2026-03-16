package com.jstyle.test2208.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jstyle.blesdk2208.constant.DeviceKey;
import com.jstyle.test2208.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/26.
 */

public class ActivityModeDataAdapter extends RecyclerView.Adapter {
    List<Map<String, String>> list = new ArrayList<>();
    String[]modeNames;
    public ActivityModeDataAdapter(String[]modeNames) {
       this.modeNames=modeNames;
    }

    public void setData(List<Map<String, String>> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activitymodedata, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Map<String, String> map = list.get(position);
        String date = map.get(DeviceKey.Date);
        String time = map.get(DeviceKey.ActiveMinutes);
        String totalStep = map.get(DeviceKey.Step);
        String distance = map.get(DeviceKey.Distance);
        String cal = map.get(DeviceKey.Calories);
        String heartRate = map.get(DeviceKey.HeartRate);
        String pace = map.get(DeviceKey.Pace);
        String mode = map.get(DeviceKey.ActivityMode);
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("mode: "+modeNames[mode.equals("6")?3:Integer.valueOf(mode)]).append("\n")
                .append("Time: "+time+" s").append("\n")
                .append("TotalStep: " + totalStep).append("\n")
                .append("Distance: " + distance + " Km").append("\n")
                .append("Calories: " + cal + " Kcal").append("\n")
                .append("HeartRate: "+heartRate+" Bpm").append("\n")
                .append("Pace: "+pace);

        viewHolder.textTotalDate.setText(date);
        viewHolder.textActivityModeData.setText(stringBuffer.toString());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_totalDate)
        TextView textTotalDate;
        @BindView(R.id.text_activityModeData)
        TextView textActivityModeData;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
