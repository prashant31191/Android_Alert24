package com.kremski.alert24.serwer.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;

import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.serwer.ServerUtilities;

public class DefaultWorkerResultHandler implements WorkerResultHandler{

	public Alert24Screen alert24Screen;
	protected final String LOG_TAG = this.getClass().toString();

	public DefaultWorkerResultHandler(Alert24Screen alert24Screen) {
		this.alert24Screen = alert24Screen;
	}

	@Override
	public void handleWorkerResult(WorkerResult workerResult, Intent intentWithResult) {
		if (workerResult.isExceptionThrown()) {
			onException(workerResult, intentWithResult);
		} else if (!ServerUtilities.isResponseSuccessfull(workerResult.getResult())){ 
			onFailure(workerResult, intentWithResult);
		} else {
			onSuccess(workerResult, intentWithResult);
		}
		alert24Screen.removeOperationInProgress((intentWithResult.getStringExtra(Alert24Service.BROADCASTING_WORKER_NAME)));
	}

	public void onException(WorkerResult workerResult, Intent intentWithResult) {
			alert24Screen.showErrorDialog(workerResult.getErrorMessageToShowUponError());
	}

	public void onFailure(WorkerResult workerResult, Intent intentWithResult) {
		try {
			String errorMessage = new JSONObject(workerResult.getResult())
				.getJSONObject(ServerUtilities.SERWER_RESPONSE_STATUS_KEY)
				.getString(ServerUtilities.SERWER_RESPONSE_MESSAGE_KEY);
			
			alert24Screen.showErrorDialog(errorMessage);
		} catch (JSONException e) {
			Log.i(LOG_TAG, e.getLocalizedMessage());
		}
	}

	@Override
	public void onSuccess(WorkerResult workerResult,
			Intent intentWithResult) {

	}
}
