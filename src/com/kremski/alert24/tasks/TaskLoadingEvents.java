package com.kremski.alert24.tasks;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;

import com.kremski.alert24.R;
import com.kremski.alert24.database.EventsTable;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.sdcard.SDCardHelper;

public class TaskLoadingEvents extends DefaultTask<Void, Void, List<Event>>{

	private EventsTable eventsTableHelper;
	private OnEventsLoadedListener eventsLoadedListener;
	private boolean loadNotSentEvents;
	private SDCardHelper sdCardHelper = new SDCardHelper();
	private Bitmap noPhotoBitmap;

	public TaskLoadingEvents(Alert24Screen acitivity, EventsTable eventsTableHelper, OnEventsLoadedListener eventsLoadedListener, boolean loadNotSentEvents) {
		super(acitivity);
		this.eventsTableHelper = eventsTableHelper;
		this.eventsLoadedListener = eventsLoadedListener;
		this.loadNotSentEvents = loadNotSentEvents;
	}

	@Override
	protected List<Event> execute() {
		this.noPhotoBitmap = ((BitmapDrawable)activity.getResources().getDrawable(R.drawable.no_photo)).getBitmap();
		Log.i(LOG_TAG, "Selecting events from database");
		List<Event> events = loadEvents();
		Log.i(LOG_TAG, "Events have been selected : " + events.toString());
		return events; 
	}

	private List<Event> loadEvents() {
		List<Event> events = loadNotSentEvents ? eventsTableHelper.getAllNotSentEvents() : eventsTableHelper.getAllSentEvents();
		
		for(Event e : events) {
			e.setPhotoBitmap(loadEventBitmap(e));
		}
		return events;
	}
	
	private Bitmap loadEventBitmap(Event event) {
		Bitmap eventPhoto = null;
		try {
			eventPhoto = sdCardHelper.loadEventPhotoBitmap(event, 100, 100);
		} catch (IOException e) {
			Log.i(LOG_TAG, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return eventPhoto == null ? noPhotoBitmap : eventPhoto;
	}

	@Override
	protected void handleResult(List<Event> result) {
		eventsLoadedListener.onEventsLoaded(result);
	}

	public interface OnEventsLoadedListener {
		public void onEventsLoaded(List<Event> events);
	}
}
