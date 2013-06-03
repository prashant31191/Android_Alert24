package com.kremski.alert24.serwer.service;

import hirondelle.date4j.DateTime;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kremski.alert24.R;
import com.kremski.alert24.database.EventsTable;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.domain.MessageIntent;
import com.kremski.alert24.sdcard.SDCardHelper;

public class WorkerInsertingEvent extends DefaultWorker{

	private final EventsTable eventsTableHelper;
	private final Event eventToInsert;
	private final SDCardHelper sdCardHelper = new SDCardHelper();
	private boolean sendBroadcastWithResult = true;

	public WorkerInsertingEvent(Context context, EventsTable eventsTableHelper, Event eventToInsert, boolean sendBroadcastWithResult) {
		super(context);
		this.eventToInsert = eventToInsert;
		this.eventsTableHelper = eventsTableHelper;
		this.sendBroadcastWithResult = sendBroadcastWithResult;
	}

	@Override
	public void execute() {
		try {
			insertEventToDatabase();
			if (eventToInsert.getDatabaseId() > -1 && eventToInsert.getPhotoBitmap() != null) {
				saveEventPhotoOnCard();
			} else {
				eventToInsert.setPhotoPath("");
			}
			updateEventInDatabaseWithPhotoPath();
			if (sendBroadcastWithResult) {
				context.sendBroadcast(createIntentForBroadcastResult(new WorkerResult("")));
			}
		} catch (IOException e) {
			sendBroadcastWithException(e, R.string.operation_failed, R.string.event_not_saved);
		}
	}

	private void insertEventToDatabase() {
		Log.i(LOG_TAG, "Inserting event" + eventToInsert.toString() + " to database");
		long insertedEventDatabaseId = eventsTableHelper.insert(eventToInsert);
		eventToInsert.setDatabaseId(insertedEventDatabaseId);
		Log.i(LOG_TAG, "Event has been inserted to database");
	}

	private void saveEventPhotoOnCard() throws IOException {
		Log.i(LOG_TAG, "Saving event photo on disc");
		String filePathOfSavedPhoto = sdCardHelper.saveEventPhotoOnCard(eventToInsert);
		eventToInsert.setPhotoPath(filePathOfSavedPhoto);
		Log.i(LOG_TAG, "Event photo has been saved on disc (filePath = " + filePathOfSavedPhoto + ")"); 
	}

	private void updateEventInDatabaseWithPhotoPath() {
		Log.i(LOG_TAG, "Updating event" + eventToInsert.toString() + " in database");
		eventsTableHelper.update(eventToInsert);
		Log.i(LOG_TAG, "Event has been updated in database ");
	}
	
	@Override
	protected int getErrorMessageTitleId() {
		return R.string.adding_event_to_database;
	}
}
