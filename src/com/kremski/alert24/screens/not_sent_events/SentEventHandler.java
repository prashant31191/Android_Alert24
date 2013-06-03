package com.kremski.alert24.screens.not_sent_events;

import android.content.Intent;

import com.kremski.alert24.serwer.service.DefaultWorkerResultHandler;
import com.kremski.alert24.serwer.service.WorkerResult;
import com.kremski.alert24.serwer.service.WorkerSendingEvent;

class SentEventHandler extends DefaultWorkerResultHandler {

	private NotSentEventsScreen screen;
	
	SentEventHandler(NotSentEventsScreen alert24Screen) {
		super(alert24Screen);
		this.screen = alert24Screen;
	}

	@Override
	public void onSuccess(WorkerResult workerResult, Intent intentWithResult) {
		screen.removeEvent(intentWithResult.getIntExtra(WorkerSendingEvent.INTENT_KEY_OF_SENT_EVENT_ID, -1));
	}

}


