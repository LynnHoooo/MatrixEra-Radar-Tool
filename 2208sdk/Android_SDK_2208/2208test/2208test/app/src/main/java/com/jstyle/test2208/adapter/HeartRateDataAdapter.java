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

public class HeartRateDataAdapter extends RecyclerView.Adapter {
    List<Map<String, String>> list = new ArrayList<>();
        public static final int GET_HEART_DATA=0;
        public static final int GET_ONCE_HEARTDATA=1;
    private final static int View_Type_Empty = 2;
    private final static int View_Type_OnceHeart = 1;
    private final static int View_Type_HistoryHeart = 0;
    private int sendCmdState;

    public void setData(List<Map<String, String>> list, int type) {
        this.list = list;
        this.sendCmdState = type;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case View_Type_Empty:
                View viewEmpty = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty, parent, false);
                return new EmptyViewHolder(viewEmpty);
            case View_Type_OnceHeart:
                View viewSleep = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onceheartdata, parent, false);
                return new OnceHeartViewHolder(viewSleep);
            case View_Type_HistoryHeart:
                View viewStep = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hearthistorydata, parent, false);
                return new ViewHolder(viewStep);
            default:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty, parent, false);
                return new EmptyViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case View_Type_Empty:
                bindEmptyData();
                break;
            case View_Type_OnceHeart:
                bindOnceHeartData(holder,position);
                break;
            case View_Type_HistoryHeart:
                bindHistoryHeartData(holder,position);
                break;
        }
    }

    private void bindHistoryHeartData(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Map<String, String> map = list.get(position);
        String time = map.get(DeviceKey.Date);
        String heartValue = map.get(DeviceKey.ArrayDynamicHR);
        viewHolder.textDetailTime.setText("Time: " + time);
        viewHolder.textHistoryHeartValue.setText("HeartRate: " +heartValue);
    }

    private void bindOnceHeartData(RecyclerView.ViewHolder holder, int position) {
        OnceHeartViewHolder viewHolder = (OnceHeartViewHolder) holder;
        Map<String, String> map = list.get(position);
        String time = map.get(DeviceKey.Date);
        String heartData = map.get(DeviceKey.StaticHR);
        viewHolder.textSleepData.setText(heartData);
        viewHolder.textSleepTime.setText(time);
    }

    private void bindEmptyData() {

    }

    @Override
    public int getItemCount() {
        return list.size() == 0 ? 1 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type = 0;
        if (list.size() == 0) return View_Type_Empty;
        switch (sendCmdState) {
            case GET_HEART_DATA:
                type = View_Type_HistoryHeart;
                break;
            case GET_ONCE_HEARTDATA:
                type = View_Type_OnceHeart;
                break;
        }
        return type;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_historyHeartTime)
        TextView textDetailTime;
        @BindView(R.id.text_historyHeartValue)
        TextView textHistoryHeartValue;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class OnceHeartViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_onceHeartTime)
        TextView textSleepTime;
        @BindView(R.id.item_onceHeartValue)
        TextView textSleepData;


        OnceHeartViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_empty)
        TextView textSleepTime;


        EmptyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
