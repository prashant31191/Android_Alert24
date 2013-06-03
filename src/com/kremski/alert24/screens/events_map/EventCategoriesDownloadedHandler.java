package com.kremski.alert24.screens.events_map;

import java.util.List;

import android.content.Intent;

import com.kremski.alert24.domain.Event.EventBuilder;
import com.kremski.alert24.domain.EventCategory;
import com.kremski.alert24.screens.OptionsManager;
import com.kremski.alert24.serwer.service.DefaultWorkerResultHandler;
import com.kremski.alert24.serwer.service.WorkerResult;

public class EventCategoriesDownloadedHandler extends DefaultWorkerResultHandler {
	private EventsMapScreen screenWithEventsMap;
	private OptionsManager optionsManager;

	public EventCategoriesDownloadedHandler(EventsMapScreen screenWithEventsMap, OptionsManager optionsManager) {
		super(screenWithEventsMap);
		this.screenWithEventsMap = screenWithEventsMap;
		this.optionsManager = optionsManager;
	}

	@Override
	public void onSuccess(WorkerResult workerResult,
			Intent intentWithResult) {
		final List<EventCategory> downloadedEventCategories = intentWithResult.getParcelableArrayListExtra(List.class.toString());
	
		screenWithEventsMap.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				for (EventCategory eventCategory : downloadedEventCategories) {
					screenWithEventsMap.getEventCategoryImageManager().loadImage(eventCategory);
				}
				optionsManager.setEventCategoriesDownloadedStatus(true);
				screenWithEventsMap.startDownloadingEvents();
			}
		});

		return;

	}
}