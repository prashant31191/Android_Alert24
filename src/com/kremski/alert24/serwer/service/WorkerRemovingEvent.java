package com.kremski.alert24.serwer.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kremski.alert24.R;
import com.kremski.alert24.database.EventsTable;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.sdcard.SDCardHelper;

public class WorkerRemovingEvent extends DefaultWorker{
	
	private final EventsTable eventsTableHelper;
	private final Event eventToRemove;
	private final SDCardHelper sdCardHelper = new SDCardHelper();
	public static final String REMOVED_EVENT_ID_INTENT_KEY = "removed_event_id";
	public static final String IS_LAST_EVENT_INTENT_KEY = "is_last_event";

	public WorkerRemovingEvent(Context context, EventsTable eventsTableHelper, Event eventToRemove) {
		super(context);
		this.eventsTableHelper = eventsTableHelper;
		this.eventToRemove = eventToRemove;
	}
	
	@Override
	public void execute() {
		removeEventFromDatabase();
		Log.i(LOG_TAG, "Removing event photo from card");
		if(sdCardHelper.deleteEventPhotoFromCard(eventToRemove)) {
			Log.i(LOG_TAG, "Event photo has been removed from from card");
		}
		sendBroadcastWithResult();
	}

	private void removeEventFromDatabase() {
		Log.i(LOG_TAG, "Removing event " + eventToRemove.toString() + " from database");
		eventsTableHelper.delete(eventToRemove);
		Log.i(LOG_TAG, "Event " + eventToRemove.toString() + " has been removed from database");
	}
	
	private void sendBroadcastWithResult() {
		Intent intent = createIntentForBroadcastResult(new WorkerResult("")); 
		intent.putExtra(REMOVED_EVENT_ID_INTENT_KEY, eventToRemove.getDatabaseId());
		context.sendBroadcast(intent);
	}
	
	@Override
	protected int getErrorMessageTitleId() {
		return R.string.removing_event;
	}
}
