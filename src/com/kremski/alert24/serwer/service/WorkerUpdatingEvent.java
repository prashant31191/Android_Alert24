package com.kremski.alert24.serwer.service;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kremski.alert24.R;
import com.kremski.alert24.database.EventsTable;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.sdcard.SDCardHelper;

public class WorkerUpdatingEvent extends DefaultWorker{

	private EventsTable eventsTableHelper;
	private Event eventToUpdate;
	private final SDCardHelper sdCardHelper = new SDCardHelper();
	private boolean sendBroadcast = true;

	public WorkerUpdatingEvent(Context context, EventsTable eventsTableHelper, Event eventToUpdate, boolean sendBroadcast) {
		super(context);
		this.eventToUpdate = eventToUpdate;
		this.eventsTableHelper = eventsTableHelper;
	}

	@Override
	public void execute() {
		try {
			if (eventToUpdate.getPhotoBitmap() != null) {
				saveEventPhotoOnCard();
			} else {
				eventToUpdate.setPhotoPath(null);
			}
			updateEventInDatabase();
			if (sendBroadcast) {
				sendBroadcastWithUpdatedEvent();
			}
		} catch (IOException e) {
			sendBroadcastWithException(e, R.string.operation_failed);
		}
	}

	private void saveEventPhotoOnCard() throws IOException {
		Log.i(LOG_TAG, "Saving event photo on disc");
		String filePathOfSavedPhoto = sdCardHelper.saveEventPhotoOnCard(eventToUpdate);
		eventToUpdate.setPhotoPath(filePathOfSavedPhoto);
		Log.i(LOG_TAG, "Event photo has been saved on disc (filePath = " + filePathOfSavedPhoto + ")"); 
	}

	private void updateEventInDatabase() {
		Log.i(LOG_TAG, "Updating event" + eventToUpdate.toString() + " in database");
		eventsTableHelper.update(eventToUpdate);
		Log.i(LOG_TAG, "Event has been updated in database ");
	}

	private void sendBroadcastWithUpdatedEvent() {
		Intent intent = createIntentForBroadcastResult(new WorkerResult(""));
		intent.putExtra(Event.class.toString(), eventToUpdate);
		context.sendBroadcast(intent);
	}
	
	@Override
	protected int getErrorMessageTitleId() {
		return R.string.updating_event_on_device;
	}
}
