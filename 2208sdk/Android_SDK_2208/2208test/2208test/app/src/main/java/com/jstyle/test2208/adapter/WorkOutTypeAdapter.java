package com.jstyle.test2208.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.jstyle.test2208.R;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/6/27.
 */

public class WorkOutTypeAdapter extends RecyclerViewBaseAdapter<String> {
    private List<Integer> selectedList=new ArrayList<>();

    public WorkOutTypeAdapter(List<String> mDatas) {
        super(mDatas);
    }

    @Override
    protected void bindData(BaseViewHolder holder, final int position) {
        Context context = holder.itemView.getContext();
        WorkOutTypeViewHolder workOutTypeViewHolder=(WorkOutTypeViewHolder)holder;
        workOutTypeViewHolder.btExerciseMode.setText(mDataList.get(position));
        ConstraintLayout constraintLayout = (ConstraintLayout) workOutTypeViewHolder.btExerciseMode.getParent();
        ViewGroup.LayoutParams lp = constraintLayout.getLayoutParams();
        if (lp instanceof FlexboxLayoutManager.LayoutParams) {
            FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) lp;
            flexboxLp.setFlexBasisPercent(0.25f);
        }
        workOutTypeViewHolder.btExerciseMode.setTextColor(selectedList.contains(position)?Color.RED :Color.BLACK);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedList.contains(position)){
                    selectedList.remove(Integer.valueOf(position));
                }else{
                    if(selectedList.size()!=5){
                        selectedList.add(Integer.valueOf(position));
                    }
                }
                notifyDataSetChanged();
            }
        });
    }
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WorkOutTypeViewHolder(getInflaterView(parent, viewType));
    }
    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_workout_type;
    }

    class WorkOutTypeViewHolder extends BaseViewHolder {
        @BindView(R.id.bt_exercise_mode)
        TextView btExerciseMode;


        public WorkOutTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public List<Integer> getSelectedList(){
        return this.selectedList;
    }
    public void setSelectedList(List<Integer> list){
        this.selectedList=list;
        notifyDataSetChanged();
    }
}
