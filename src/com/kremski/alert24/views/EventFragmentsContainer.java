package com.kremski.alert24.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kremski.alert24.R;
import com.kremski.alert24.adapters.EventFragmentsPagerAdapter;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.screens.Alert24Screen;
import com.viewpagerindicator.TitlePageIndicator;

public class EventFragmentsContainer extends Fragment {

	private TitlePageIndicator titlePageIndicator;
	private ViewPager viewPager;
	private EventFragmentsPagerAdapter pagerAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.event_fragments_container, null);
		titlePageIndicator = (TitlePageIndicator)view.findViewById(R.id.event_fragments_titles);
		titlePageIndicator.setSelectedColor(getResources().getColor(android.R.color.black));
		viewPager = (ViewPager)view.findViewById(R.id.event_fragments);
		return view;
	}
	
	public void initialize(Alert24Screen activity, FragmentManager manager, Event event) {
		if (pagerAdapter == null) {	
			viewPager.setAdapter(pagerAdapter = new EventFragmentsPagerAdapter(activity, manager, event));
			titlePageIndicator.setViewPager(viewPager);
		} else {
			pagerAdapter.notifyDataSetChanged();
		}
	}	

	
}

