package com.kremski.alert24.adapters;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.common.collect.Lists;
import com.kremski.alert24.domain.SystemNotification;
import com.kremski.alert24.views.SystemNotificationChildFragment;

public class SystemNotificationsPagerAdapter extends FragmentStatePagerAdapter {
	
	private List<SystemNotification> notifications;
	
	public SystemNotificationsPagerAdapter(FragmentManager manager, List<SystemNotification> systemNotifications) {
		super(manager);
		this.notifications = Lists.newLinkedList(systemNotifications);
	}
	
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE; 
	}
	
	@Override
	public int getCount() {
		return notifications.size();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return notifications.get(position).getTitle();
	}

	@Override
	public Fragment getItem(int position) {
		return SystemNotificationChildFragment.newInstance(notifications.get(position));
	}

}
