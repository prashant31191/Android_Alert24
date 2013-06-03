package com.kremski.alert24.screens.events_map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.kremski.alert24.CommonUtilities;
import com.kremski.alert24.screens.OptionsManager;
import com.kremski.alert24.serwer.ServerUtilities;
import com.kremski.alert24.serwer.service.DefaultWorkerResultHandler;
import com.kremski.alert24.serwer.service.WorkerResult;

class DeviceRegisteredHandler extends DefaultWorkerResultHandler{

	private EventsMapScreen screenWithEventsMap;
	private OptionsManager optionsManager;

	public DeviceRegisteredHandler(EventsMapScreen screenWithEventsMap, OptionsManager optionsManager) {
		super(screenWithEventsMap);
		this.screenWithEventsMap = screenWithEventsMap;
		this.optionsManager = optionsManager;
	}

	@Override
	public void onSuccess(WorkerResult workerResult,
			Intent intentWithResult) {
		JSONObject serverResponseAsJson;
		try {
			serverResponseAsJson = new JSONObject(workerResult.getResult());
			GCMRegistrar.setRegisteredOnServer(alert24Screen, true);
			String deviceKey = serverResponseAsJson.getString(optionsManager.getDeviceKeyPreferenceName());
			optionsManager.setDeviceKey(deviceKey);
			Log.i(LOG_TAG, "Device key: " + deviceKey);
			screenWithEventsMap.startDownloadingEvents();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
