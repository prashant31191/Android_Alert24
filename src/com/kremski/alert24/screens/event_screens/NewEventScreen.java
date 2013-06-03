package com.kremski.alert24.screens.event_screens;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.kremski.alert24.R;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.serwer.service.DefaultWorkerResultHandler;
import com.kremski.alert24.serwer.service.WorkerInsertingEvent;
import com.kremski.alert24.serwer.service.WorkerRetrivingAddress;
import com.kremski.alert24.serwer.service.WorkerSendingEvent;
import com.kremski.alert24.views.EventFormFragment;

public class NewEventScreen extends Alert24Screen {

	private EventFormFragment addEventFormFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addEventFormFragment = (EventFormFragment) getSupportFragmentManager().findFragmentById(R.id.add_event_form);
		addEventFormFragment.setActivity(this);
		setActionBarTitle(R.string.new_event_screen_title);
		registerWorkerResultHandler(WorkerRetrivingAddress.class.toString(), new AddressRetrivedHandler(this, addEventFormFragment));
		registerWorkerResultHandler(WorkerSendingEvent.class.toString(), new DefaultWorkerResultHandler(this));
		registerWorkerResultHandler(WorkerInsertingEvent.class.toString(), new DefaultWorkerResultHandler(this));
		
		LatLng lastKnownLocation = getLastKnownLocation();
		if (lastKnownLocation != null) {
			addEventFormFragment.showAddressLoadingProgress();
			executeWorker(new WorkerRetrivingAddress(this, lastKnownLocation));
		}
	}

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
	}

	@Override
	protected int getContentViewId() {
		return R.layout.new_event_screen;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	public String getDeviceKey() {
		return optionsManager.getDeviceKey();
	}
	
}
