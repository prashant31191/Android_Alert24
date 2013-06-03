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

public class TaskLoadingEventImage extends DefaultTask<Void, Void, Bitmap>{

	private SDCardHelper sdCardHelper = new SDCardHelper();
	private Event event;
	private OnEventBitmapLoadedListener eventBitmapLoadedListener;

	public TaskLoadingEventImage(Alert24Screen acitivity, Event event, OnEventBitmapLoadedListener eventBitmapLoadedListener) {
		super(acitivity);
		this.event = event;
		this.eventBitmapLoadedListener = eventBitmapLoadedListener;
	}

	@Override
	protected Bitmap execute() {
		Log.i(LOG_TAG, "Loading event image from database");
		Bitmap eventBitmap = null;
		try {
			eventBitmap = sdCardHelper.loadEventPhotoBitmap(event, 320, 240);
			Log.i(LOG_TAG, "Event image have been laoded"); 
		} catch (IOException e) {
			// TODO HERE
			e.printStackTrace();
		}
		return eventBitmap;
	}

	@Override
	protected void handleResult(Bitmap eventBitmap) {
		eventBitmapLoadedListener.onEventBitmapLoaded(eventBitmap);
	}

	public interface OnEventBitmapLoadedListener {
		public void onEventBitmapLoaded(Bitmap eventBitmap);
	}
}
