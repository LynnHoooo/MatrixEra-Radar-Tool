package com.jstyle.test2208.adapter;

import android.content.Context;


import com.jstyle.test2208.R;

import java.util.List;

public class MyWheelAdapter extends  AbstractWheelTextAdapter {

	

	List<String> list;

	public MyWheelAdapter(Context context, List<String> list, int currentItem, int maxsize, int minsize) {
		super(context, R.layout.item_wheel, R.id.tempValue, currentItem, maxsize, minsize);
		this.list = list;
//		this.currentIndex=currentItem;

	}
	public void setTextMaxSize(int size){
		maxsize=size;
	}
	public void setCenterColor(int color){
		colorCenter=color;
	}
	public void setNormalColor(int color){
		colorNormal=color;
	}
//	@Override
//	public View getItem(int index, View cachedView, ViewGroup parent) {
//		View view = super.getItem(index, cachedView, parent);
//		return view;
//	}

	@Override
	public int getItemsCount() {
		return list.size();
	}

	@Override
	public int getCurrentItem() {
		return currentIndex;
	}

	@Override
	public String getItemText(int index) {
		return index==-1?"":list.get(index) + "";
	}

	public String getCurrentItemText() {
		return list.get(currentIndex);
	}


}
