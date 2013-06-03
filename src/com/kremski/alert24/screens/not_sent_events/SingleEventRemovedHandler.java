package com.kremski.alert24.screens.not_sent_events;

import android.content.Intent;

import com.kremski.alert24.serwer.service.DefaultWorkerResultHandler;
import com.kremski.alert24.serwer.service.WorkerRemovingEvent;
import com.kremski.alert24.serwer.service.WorkerResult;


class SingleEventRemovedHandler extends DefaultWorkerResultHandler{

	private NotSentEventsScreen alert24Screen;

	SingleEventRemovedHandler(NotSentEventsScreen alert24Screen) {
		super(alert24Screen);
		this.alert24Screen = alert24Screen;
	}

	@Override
	public void onSuccess(WorkerResult workerResult, final Intent intentWithResult) {
		alert24Screen.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				long removedEventId = intentWithResult.getLongExtra(WorkerRemovingEvent.REMOVED_EVENT_ID_INTENT_KEY, -1);
				alert24Screen.removeEvent(removedEventId);
			};
		});
	}
}
