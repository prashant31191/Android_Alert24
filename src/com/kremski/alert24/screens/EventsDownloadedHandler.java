package com.kremski.alert24.screens;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;

import com.google.common.collect.Lists;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.domain.Event.EventBuilder;
import com.kremski.alert24.domain.EventCategory;
import com.kremski.alert24.screens.events_map.EventsMapScreen;
import com.kremski.alert24.serwer.service.DefaultWorkerResultHandler;
import com.kremski.alert24.serwer.service.WorkerResult;

public class EventsDownloadedHandler extends DefaultWorkerResultHandler {
	private Alert24Screen alert24Screen;

	public EventsDownloadedHandler(Alert24Screen screenWithEventsMap) {
		super(screenWithEventsMap);
		this.alert24Screen = screenWithEventsMap;
	}

	@Override
	public void onSuccess(WorkerResult workerResult,
			Intent intentWithResult) {
		String serverResponse = workerResult.getResult();
		List<Event> downloadedEvents = new LinkedList<Event>();
		try {
			JSONObject respone = new JSONObject(serverResponse);
			JSONArray arrayOfEvents = respone.getJSONArray("Events");
			for(int i = 0; i < arrayOfEvents.length(); ++i) {
				JSONObject eventData = arrayOfEvents.getJSONObject(i);

				//TODO GSON zamiast JSONObject
				String name = eventData.getString("Name");
				String description = eventData.getString("Description");
				String photoUrl = eventData.getString("Photos");
				JSONArray photos = eventData.getJSONArray("Photos");
				List<String> photoUrls = Lists.newLinkedList();
				for (int j = 0 ; j < photos.length(); ++j ) {
					photoUrls.add(photos.getJSONObject(j).getString("Url"));
				}
				String eventId = eventData.getString("Id");
				String categoryName = eventData.getString("CategoryName");
				String categoryServerID = eventData.getString("CategoryId");
				String categoryIconUrl = eventData.getString("CategoryIconUrl");
				double latitude = Double.parseDouble(eventData.getString("Latitude").replace(",", "."));
				double longitude = Double.parseDouble(eventData.getString("Longitude").replace(",", "."));

				Event event = new EventBuilder() 
						.serverId(eventId)
						.name(name)
						.category(new EventCategory(categoryName, categoryIconUrl, -1, -1, categoryServerID))
						.descritpion(description)
						.photoUrls(photoUrls)
						.latitude(latitude)
						.longitude(longitude)
						.build();
				downloadedEvents.add(event);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		alert24Screen.onEventsDownloaded(downloadedEvents);

		return;

	}
}