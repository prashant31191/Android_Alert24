package com.kremski.alert24.screens.sent_events;

import android.content.Intent;

import com.kremski.alert24.serwer.service.DefaultWorkerResultHandler;
import com.kremski.alert24.serwer.service.WorkerRemovingEvent;
import com.kremski.alert24.serwer.service.WorkerRemovingSelectedEvents;
import com.kremski.alert24.serwer.service.WorkerResult;


class EventsRemovedHandler extends DefaultWorkerResultHandler{

	private SentEventsScreen alert24Screen;

	EventsRemovedHandler(SentEventsScreen alert24Screen) {
		super(alert24Screen);
		this.alert24Screen = alert24Screen;
	}

	@Override
	public void onSuccess(WorkerResult workerResult, final Intent intentWithResult) {
		alert24Screen.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				long removedEventId = intentWithResult.getLongExtra(WorkerRemovingSelectedEvents.REMOVED_EVENT_ID_INTENT_KEY, -1);
				alert24Screen.removeEvent(removedEventId);
				
				boolean isLastEvent = intentWithResult.getBooleanExtra(WorkerRemovingSelectedEvents.LAST_EVENT_INTENT_KEY, false);
				if (isLastEvent) {
					alert24Screen.removeOperationInProgress(WorkerRemovingSelectedEvents.class.toString());
				}
			};
		});
	}
}
