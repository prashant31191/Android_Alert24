package com.kremski.alert24.serwer.service;

import java.io.IOException;

import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.kremski.alert24.CommonUtilities;
import com.kremski.alert24.R;
import com.kremski.alert24.serwer.ServerUtilities;
import com.kremski.alert24.serwer.requests.RegisterDeviceRequest;

public class WorkerRegisteringDevice extends DefaultWorker{

	private final ServerUtilities serverUtilities;

	public WorkerRegisteringDevice(Context context, ServerUtilities serverUtilities) {
		super(context);
		this.serverUtilities = serverUtilities;
	}

	public void execute() {
		GCMRegistrar.checkDevice(context);
		GCMRegistrar.checkManifest(context);
		String onGcmServerDeviceId = GCMRegistrar.getRegistrationId(context);
		try {
			handleGCMRegistrarResponse(onGcmServerDeviceId);
		} catch (JSONException e) {
			sendBroadcastWithException(e, R.string.operation_failed, R.string.device_not_registered);
		} catch (IOException e) {
			sendBroadcastWithException(e, R.string.operation_failed, R.string.device_not_registered);
		}
	}

	private void handleGCMRegistrarResponse(String onGcmServerDeviceId) throws JSONException, IOException {
		if (onGcmServerDeviceId.equals("")) {
			Log.i(LOG_TAG, "Registering device on Google Cloud Messaging server");
			GCMRegistrar.register(context, ServerUtilities.SENDER_ID);
		} else {
			String retrivingDeviceKeyResult = serverUtilities.makePostRequest(new RegisterDeviceRequest(onGcmServerDeviceId));
			sendBroadcastWithResult(new WorkerResult(retrivingDeviceKeyResult));
		}	
	}

	private void sendBroadcastWithResult(WorkerResult workerResult) {
		context.sendBroadcast(createIntentForBroadcastResult(workerResult));
	}
	
	@Override
	protected int getErrorMessageTitleId() {
		return R.string.registering_device;
	}
}
