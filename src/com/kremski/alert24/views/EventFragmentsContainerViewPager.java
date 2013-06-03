package com.kremski.alert24.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class EventFragmentsContainerViewPager extends ViewPager{
	
	public EventFragmentsContainerViewPager(Context context) {
		super(context);
	}
	
	public EventFragmentsContainerViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
	   if(v != this && v instanceof ViewPager) {
	      return true;
	   }
	   return super.canScroll(v, checkV, dx, x, y);
	}	

}
