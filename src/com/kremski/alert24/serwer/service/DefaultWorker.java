package com.kremski.alert24.serwer.service;

import java.util.TimeZone;

import org.json.JSONException;

import hirondelle.date4j.DateTime;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import com.kremski.alert24.R;
import com.kremski.alert24.domain.MessageIntent;
import com.kremski.alert24.serwer.service.Alert24Service.Worker;

public abstract class DefaultWorker implements Worker{

	protected final String LOG_TAG = this.getClass().toString();
	protected final Context context;

	public DefaultWorker(Context context) {
		this.context = context;
	}

	protected void sendBroadcastWithException(Exception e, int...errorMessageStringResourceIds) {
		Log.i(LOG_TAG, e.getLocalizedMessage());
		String errorMessage = createContactenatedMessageFromStringResources(errorMessageStringResourceIds);
		Intent intentToSend = createIntentForBroadcastResult(new WorkerResult(e, errorMessage));
		prepareIntentWithException(intentToSend);
		context.sendBroadcast(intentToSend);
		try {
			String errorMessageTitle = context.getResources().getString(getErrorMessageTitleId());
			context.sendBroadcast(new MessageIntent(errorMessageTitle, "ANDROID", errorMessage + "\n" + e.getLocalizedMessage(),
					DateTime.now(TimeZone.getDefault()).getMilliseconds(TimeZone.getDefault())));
		} catch (JSONException e1) {
			Log.i(LOG_TAG, e1.getLocalizedMessage());
		}
	}
	
	protected abstract int getErrorMessageTitleId();
	
	protected void prepareIntentWithException(Intent intentToSend){}
	
	private  String createContactenatedMessageFromStringResources(int...stringResourceIds) {
		Resources res = context.getResources();
		StringBuilder messageBuilder = new StringBuilder();
		for (int stringId : stringResourceIds) {
			messageBuilder.append(res.getString(stringId) + " ");
		}
		return messageBuilder.toString();
	}

	protected Intent createIntentForBroadcastResult(WorkerResult workerResult) {
		Intent intent = new Intent();
		intent.setAction(Alert24Service.BROADCAST_FROM_WORKER);
		intent.putExtra(Alert24Service.BROADCASTING_WORKER_NAME, LOG_TAG);
		intent.putExtra(Alert24Service.WORKER_EXECUTION_RESULT, workerResult);
		return intent;
	}
}
