package com.kremski.alert24.tasks;

import com.kremski.alert24.screens.Alert24Screen;

import android.os.AsyncTask;

public abstract class DefaultTask<Params,Progress, Result> extends AsyncTask<Params, Progress, Result>{
	
	protected final Alert24Screen activity;
	protected final String LOG_TAG = DefaultTask.this.getClass().toString();
	
	public DefaultTask(Alert24Screen activity) {
		this.activity = activity;
	}

	@Override
	protected Result doInBackground(Params... arg0) {
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				activity.addOperationInProgress(DefaultTask.this.getClass().toString());
			}
		});
		return execute();
	}
	
	protected abstract Result execute();
	protected abstract void handleResult(Result result);
	
	@Override
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);
		handleResult(result);
		activity.removeOperationInProgress(DefaultTask.this.getClass().toString());
	}


}
