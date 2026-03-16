package com.jstyle.test2208.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jstyle.test2208.R;
import com.jstyle.test2208.activity.EcghistoryActivity;
import com.jstyle.test2208.model.EcgHistoryData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/26.
 */

public class ECGDataAdapter extends RecyclerView.Adapter {
    List<EcgHistoryData> list = new ArrayList<>();

    public void setData(List<EcgHistoryData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void Notify() {
        notifyDataSetChanged();
    }
    public void Clear() {
        list=new ArrayList<>();
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activitymodedataer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(0<list.size()){
            final ViewHolder viewHolder = (ViewHolder) holder;
            final EcgHistoryData ecgHistoryData=list.get(position);
            String map = ecgHistoryData.toString();
            viewHolder.textTotalDate.setText(map);
            viewHolder.read_ecg_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(viewHolder.itemView.getContext(), EcghistoryActivity.class);
                    intent.putExtra("time",ecgHistoryData.getTime());
                    intent.putExtra("address",ecgHistoryData.getAddress());
                    viewHolder.itemView.getContext().startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_totalDate)
        TextView textTotalDate;
        @BindView(R.id.read_ecg_Button)
        Button read_ecg_Button;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
