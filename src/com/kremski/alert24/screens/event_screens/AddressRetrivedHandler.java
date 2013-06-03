package com.kremski.alert24.screens.event_screens;

import android.content.Intent;
import android.location.Address;

import com.google.android.gms.maps.model.LatLng;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.screens.not_sent_events.NotSentEventsScreen;
import com.kremski.alert24.serwer.service.DefaultWorkerResultHandler;
import com.kremski.alert24.serwer.service.WorkerResult;


public class AddressRetrivedHandler extends DefaultWorkerResultHandler{
	
	private Alert24Screen alert24Screen;
	private AddressContainer addressContainer;
	
	public AddressRetrivedHandler(Alert24Screen alert24Screen, AddressContainer addressContainer) {
		super(alert24Screen);
		this.alert24Screen = alert24Screen;
		this.addressContainer = addressContainer;
	}

	@Override
	public void onSuccess(WorkerResult workerResult, final Intent intentWithResult) {
		alert24Screen.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Address address = intentWithResult.getParcelableExtra(Address.class.toString());
				LatLng latLngSource = intentWithResult.getParcelableExtra(LatLng.class.toString());
				if (addressContainer.isAdded()) {
					addressContainer.setAddress(address, latLngSource);
				}
			}
		});
	}
}
