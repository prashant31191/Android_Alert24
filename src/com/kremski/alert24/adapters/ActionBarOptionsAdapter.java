package com.kremski.alert24.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.common.collect.Lists;
import com.kremski.alert24.views.ActionBarOption;
import com.kremski.alert24.views.ActionBarOptionView;

public class ActionBarOptionsAdapter extends BaseAdapter{

	private List<ActionBarOption> allOptions; 
	private Context context;
	
	public ActionBarOptionsAdapter(Context context, List<ActionBarOption> allOptions) {
		this.allOptions = Lists.newLinkedList(allOptions);
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return allOptions.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ActionBarOption option = allOptions.get(position);
		
		return new ActionBarOptionView(context, option);
	}
}
