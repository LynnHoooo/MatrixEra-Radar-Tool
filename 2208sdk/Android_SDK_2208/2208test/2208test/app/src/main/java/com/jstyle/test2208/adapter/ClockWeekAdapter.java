package com.jstyle.test2208.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.jstyle.test2208.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/27.
 */

public class ClockWeekAdapter extends RecyclerView.Adapter {
    String[]arrays;
    int[]positions;
    public ClockWeekAdapter(String[]arrays,int[]position) {
        this.arrays=arrays;
        this.positions=position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clock_week, parent, false);
        return new ViewHolder(view);
    }

    private static final String TAG = "ClockWeekAdapter";
    int[]checked=new int[7];
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.checkbox_clock_week.setText(arrays[position]);
        viewHolder.checkbox_clock_week.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                positions[position]=isChecked?1:0;
            }
        });
        viewHolder.checkbox_clock_week.setChecked(positions[position]==1);
    }

    public int[] getCheckWeek(){
        return positions;
    }
    @Override
    public int getItemCount() {
        return arrays==null?0:arrays.length;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.checkbox_clock_week)
        CheckBox checkbox_clock_week;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
