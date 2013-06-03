package com.kremski.alert24.serwer.service;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.kremski.alert24.R;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.screens.event_screens.AddressContainer;
import com.kremski.alert24.tasks.TaskResult;

public class WorkerRetrivingAddress extends DefaultWorker {

	private LatLng sourcePoint;
	private Context context;

	public WorkerRetrivingAddress(Context context, LatLng sourcePoint) {
		super(context);
		this.sourcePoint = sourcePoint;
		this.context = context;
	}

	private Address getAddressFromLatLngPoint(LatLng sourcePoint) throws IOException {
		List<Address> addresses = new Geocoder(context).getFromLocation(sourcePoint.latitude, sourcePoint.longitude, 1);
		if (addresses.size() > 0) {
			return addresses.get(0);
		}
		return null;
	}

	@Override
	public void execute() {
		try {
			Address address = getAddressFromLatLngPoint(sourcePoint);
			sendBroadcastWithResult(new WorkerResult(""), address);
		} catch (IOException e) {
			sendBroadcastWithResult(new WorkerResult(""), null);
		}
	}
	
	private void sendBroadcastWithResult(WorkerResult workerResult, Address resultAddress) {
		Intent intent = createIntentForBroadcastResult(workerResult);
		intent.putExtra(Address.class.toString(), resultAddress);
		intent.putExtra(LatLng.class.toString(), sourcePoint);
		context.sendBroadcast(intent);
	}
	
	@Override
	protected int getErrorMessageTitleId() {
		return R.string.retriving_address;
	}
}