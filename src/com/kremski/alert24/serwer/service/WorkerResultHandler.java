package com.kremski.alert24.serwer.service;

import android.content.Intent;

public interface WorkerResultHandler {
	public void handleWorkerResult(WorkerResult workerResult, Intent intentWithResult);
	public void onSuccess(WorkerResult workerResult, Intent intentWithResult); // Successful - without thrown exception
}
