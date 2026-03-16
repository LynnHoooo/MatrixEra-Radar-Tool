package com.jstyle.test2208.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/6/11.
 */

public abstract class RecyclerViewBaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    List<T> mDataList;
    private OnItemClickListener mOnItemClickListener;
    int viewTypeSize;
    T t;
    public RecyclerViewBaseAdapter(T t){
        this.t=t;
    }
    public RecyclerViewBaseAdapter(List<T> mDatas) {
        this.mDataList = mDatas;
    }
    public void setViewTypeSize(int viewTypeSize){
        this.viewTypeSize=viewTypeSize;
    }
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(viewType), parent, false);
        BaseViewHolder holder=new BaseViewHolder(view);
        ButterKnife.bind(this, holder.getView());
        return holder ;
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {
        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener==null)return;
                mOnItemClickListener.onItemClick(position);
            }
        });
        bindData(holder, position);
    }

    @Override
    public int getItemCount() {
        return mDataList==null? 0 : mDataList.size();
    }

    protected abstract void bindData(BaseViewHolder holder, int position);
    protected abstract int getLayoutId(int viewType);



    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    protected View getInflaterView(ViewGroup viewGroup, int viewType){
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(getLayoutId(viewType),viewGroup,false);
        return view;
    }

}
