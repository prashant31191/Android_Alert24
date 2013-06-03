package com.kremski.alert24.serwer.service;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kremski.alert24.R;
import com.kremski.alert24.database.EventsTable;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.sdcard.SDCardHelper;

public class WorkerRemovingSelectedEvents extends DefaultWorker{

	public static final String LAST_EVENT_INTENT_KEY = "LAST_EVENT_KEY";
	public static final String REMOVED_EVENT_ID_INTENT_KEY = "REMOVED_EVENT_ID_INTENT_KEY";
	private final EventsTable eventsTableHelper;
	private final List<Event> eventsForRemoval;
	private final SDCardHelper sdCardHelper = new SDCardHelper();

	public WorkerRemovingSelectedEvents(Context context, EventsTable eventsTableHelper, List<Event> eventsToRemove) {
		super(context);
		this.eventsTableHelper = eventsTableHelper;
		this.eventsForRemoval = eventsToRemove;
	}

	@Override
	public void execute() {
		for (int i = 0; i < eventsForRemoval.size(); ++i) {
			Event eventToRemove = eventsForRemoval.get(i);
			removeEventFromDatabase(eventToRemove);
			Log.i(LOG_TAG, "Removing event photo from card");
			if(sdCardHelper.deleteEventPhotoFromCard(eventToRemove)) {
				Log.i(LOG_TAG, "Event photo has been removed from from card");
			}
			sendBroadcastWithRemovedEventId(eventToRemove.getDatabaseId(), i == eventsForRemoval.size()-1);
		}
	}

	private void removeEventFromDatabase(Event eventToRemove) {
		Log.i(LOG_TAG, "Removing event " + eventToRemove.toString() + " from database");
		eventsTableHelper.delete(eventToRemove);
		Log.i(LOG_TAG, "Event " + eventToRemove.toString() + " has been removed from database");
	}

	private void sendBroadcastWithRemovedEventId(long removedEventId, boolean isLastEvent) {
		Intent intent = createIntentForBroadcastResult(new WorkerResult("")); 
		intent.putExtra(REMOVED_EVENT_ID_INTENT_KEY, removedEventId);
		intent.putExtra(LAST_EVENT_INTENT_KEY, isLastEvent);
		context.sendBroadcast(intent);
	}
	
	@Override
	protected int getErrorMessageTitleId() {
		return R.string.removing_selected_events;
	}
}
