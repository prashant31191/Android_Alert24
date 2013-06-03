package com.kremski.alert24.screens;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.kremski.alert24.R;
import com.kremski.alert24.views.OptionsFragment;
import com.kremski.alert24.views.OptionsFragment.OnOptionsChangeListener;

public class OptionsScreen extends Alert24Screen implements OnOptionsChangeListener{


	private OptionsFragment optionsFragment;

	@Override
	protected int getContentViewId() {
		return R.layout.options_screen;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarTitle(R.string.options_screen_title);
		optionsFragment = (OptionsFragment) getSupportFragmentManager().findFragmentById(R.id.options_fragment);
		optionsFragment.setOptionsManager(optionsManager);
		optionsFragment.setActivity(this);
		optionsFragment.setOnOptionsChangeListener(this);
	}

	@Override
	protected void handleReceivedNotification(String notificationName,
			Intent intent) {

	}

	@Override
	public void onRefreshRadiusChange(int newRadius) {
		if (isAlert24ServiceBounded()) {
			getAlert24Service().setRefreshRadius(newRadius);
		}
	}

	@Override
	public void onRefreshIntervalChange(int newRefreshInterval) {
		if (isAlert24ServiceBounded()) {
			getAlert24Service().setRefreshInterval(newRefreshInterval);
		}
	}
}
