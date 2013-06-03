package com.kremski.alert24.views;

import android.view.View.OnClickListener;

public class ActionBarOption {
	
	private int imageResourceId;
	private int textResourceId;
	private OnClickListener onClickListener;

	public ActionBarOption(int imageResourceId, int textResourceId, OnClickListener onOptionClickListener) {
		super();
		this.imageResourceId = imageResourceId;
		this.textResourceId = textResourceId;
		this.onClickListener = onOptionClickListener;
	}
	
	public int getImageResourceId() {
		return imageResourceId;
	}
	
	public void setImageResourceId(int imageResourceId) {
		this.imageResourceId = imageResourceId;
	}
	
	public int getTextResourceId() {
		return textResourceId;
	}
	
	public void setTextResourceId(int textResourceId) {
		this.textResourceId = textResourceId;
	}
	
	public OnClickListener getOnClickListener() {
		return onClickListener;
	}

}
