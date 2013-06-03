package com.kremski.alert24.screens;

import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kremski.alert24.R;
import com.kremski.alert24.domain.SystemNotification;
import com.kremski.alert24.domain.PointOfInterest;
import com.kremski.alert24.views.ActionBarOption;
import com.kremski.alert24.views.SystemNotificationParentFragment;

public class SystemNotificationPreviewScreen extends Alert24Screen {

	private List<SystemNotification> systemNotifications;
	private SystemNotificationParentFragment systemNotificationParentFragment;

	public static final String CHOSEN_NOTIFICATION_INDEX = "CHOSEN_NOTIFICATION_INDEX";

	@Override
	protected void prepareActionBarOptions(
			LinkedList<ActionBarOption> actionbarChoosableOptions) {
		super.prepareActionBarOptions(actionbarChoosableOptions);
	}

	@Override
	protected int getContentViewId() {
		return R.layout.system_notification_preview_screen;
	}

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		Intent intent = getIntent();
		if (intent != null) {
			systemNotifications = intent.getParcelableArrayListExtra(List.class.toString());
			systemNotificationParentFragment = (SystemNotificationParentFragment)getSupportFragmentManager().findFragmentById(R.id.system_notification_parent_fragment);
			systemNotificationParentFragment.setNotifications(systemNotifications, getSupportFragmentManager());
			systemNotificationParentFragment.setCurrentItem(intent.getIntExtra(CHOSEN_NOTIFICATION_INDEX, 0));
		}

		setActionBarTitle(R.string.notification_preview_screen_title);

	}


}
