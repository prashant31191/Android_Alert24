package com.kremski.alert24.views;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.screens.OptionsManager;
import com.kremski.alert24.serwer.service.DefaultWorkerResultHandler;
import com.kremski.alert24.serwer.service.WorkerResult;

class DeviceRegisteredHandler extends DefaultWorkerResultHandler{

	private OptionsManager optionsManager;
	private OptionsFragment optionsFragment;

	public DeviceRegisteredHandler(Alert24Screen activity, OptionsManager optionsManager, OptionsFragment optionsFragment) {
		super(activity);
		this.optionsManager = optionsManager;
		this.optionsFragment = optionsFragment;
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
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
