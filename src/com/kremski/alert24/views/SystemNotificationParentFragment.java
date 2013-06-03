package com.kremski.alert24.views;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kremski.alert24.R;
import com.kremski.alert24.adapters.SystemNotificationsPagerAdapter;
import com.kremski.alert24.domain.SystemNotification;
import com.viewpagerindicator.TitlePageIndicator;

public class SystemNotificationParentFragment extends Fragment{

	private TitlePageIndicator titlePageIndicator;
	private ViewPager viewPager;
	private SystemNotificationsPagerAdapter pagerAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.system_notification_parent_fragment, null);
		titlePageIndicator = (TitlePageIndicator)view.findViewById(R.id.system_notifications_titles);
		titlePageIndicator.setSelectedColor(getResources().getColor(android.R.color.black));
		viewPager = (ViewPager)view.findViewById(R.id.system_notifications);
		return view;
	}
	
	public void setNotifications(List<SystemNotification> notifications, FragmentManager manager) {
		if (pagerAdapter == null) {	
			viewPager.setAdapter(pagerAdapter = new SystemNotificationsPagerAdapter(manager, notifications));
			titlePageIndicator.setViewPager(viewPager);
		} else {
			pagerAdapter.notifyDataSetChanged();
		}
	}

	public void setCurrentItem(int position) {
		viewPager.setCurrentItem(position);
	}	
}
