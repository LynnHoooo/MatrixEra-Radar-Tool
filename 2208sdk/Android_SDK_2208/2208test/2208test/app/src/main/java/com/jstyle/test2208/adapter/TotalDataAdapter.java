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

public class TotalDataAdapter extends RecyclerView.Adapter {
    List<Map<String, String>> list = new ArrayList<>();

    public void setData(List<Map<String, String>> list){
        this.list=list;
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_totaldata, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Map<String, String> map = list.get(position);
        String time=map.get(DeviceKey.ExerciseMinutes);
        String totalStep=map.get(DeviceKey.Step);
        String distance=map.get(DeviceKey.Distance);
        String cal=map.get(DeviceKey.Calories);
        String goal=map.get(DeviceKey.Goal);
        String date=map.get(DeviceKey.Date);
        viewHolder.textTotalTime.setText("Time: "+time+"s");
        viewHolder.textTotalDate.setText(date);
        viewHolder.textTotalTotalStep.setText("TotalStep: "+totalStep);
        viewHolder.textTotalDistance.setText("Distance: "+distance+" Km");
        viewHolder.textTotalCal.setText("Calories: "+cal+" Kcal");
        viewHolder.textTotalStep.setText("Goal: "+goal+"%");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_totalTime)
        TextView textTotalTime;
        @BindView(R.id.text_totalDate)
        TextView textTotalDate;
        @BindView(R.id.text_totalTotalStep)
        TextView textTotalTotalStep;
        @BindView(R.id.text_totalDistance)
        TextView textTotalDistance;
        @BindView(R.id.text_totalCal)
        TextView textTotalCal;
        @BindView(R.id.text_totalGoal)
        TextView textTotalStep;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
