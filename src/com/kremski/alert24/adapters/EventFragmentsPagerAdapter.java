package com.kremski.alert24.adapters;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kremski.alert24.R;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.views.EventGalleryFragment;
import com.kremski.alert24.views.EventInformationsFragment;

public class EventFragmentsPagerAdapter extends FragmentStatePagerAdapter {
	
	private Event event;
	private Alert24Screen activity;
	private String infoPageTitle;
	private String galleryPageTitle;
	
	public EventFragmentsPagerAdapter(Alert24Screen activity, FragmentManager manager, Event event) {
		super(manager);
		this.event = event;
		this.activity = activity;
		infoPageTitle = activity.getResources().getString(R.string.event_info_page_title);
		galleryPageTitle = activity.getResources().getString(R.string.event_gallery_page_title);
	}
	
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE; 
	}
	
	@Override
	public int getCount() {
		return 2;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		if (position == 0) {
			return infoPageTitle;
		} else {
			return galleryPageTitle;
		}
	}

	@Override
	public Fragment getItem(int position) {
		if (position == 0) {
			return EventInformationsFragment.newInstance(event, activity);
		} else {
			return EventGalleryFragment.newInstance(activity.getEventPhotoImageManager(), event);
		}
	}

}
