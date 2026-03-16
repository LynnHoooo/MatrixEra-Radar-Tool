package com.jstyle.test2208.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jstyle.blesdk2208.model.ExerciseMode;
import com.jstyle.test2208.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/11/19.
 */

public class ActivityModeAdapter extends RecyclerView.Adapter {
    String[] modeNames;


    public ActivityModeAdapter(String[] modeNames) {
        this.modeNames = modeNames;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selectactivitymode, parent, false);
        return new ActivityModeAdapter.ViewHolder(view);
    }

    int selectPosition=-1;
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ActivityModeAdapter.ViewHolder viewHolder = (ActivityModeAdapter.ViewHolder) holder;
        viewHolder.radioButton.setText(modeNames[position]);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPosition=position;
                notifyDataSetChanged();
            }
        });
        viewHolder.radioButton.setTextColor(selectPosition==position? Color.RED:Color.GRAY);
    }

    public int getSelectPosition(){
        if(selectPosition==-1)return -1;
        return ExerciseMode.modes[selectPosition];
    }
    @Override
    public int getItemCount() {
        return modeNames.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.radioButton)
        TextView radioButton;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
