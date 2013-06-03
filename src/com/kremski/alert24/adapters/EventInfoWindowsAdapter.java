package com.kremski.alert24.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.google.common.collect.Lists;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.domain.EventCategory;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.screens.events_map.EventsMapScreen;
import com.kremski.alert24.views.EventInfoWindow;
import com.kremski.alert24.views.image.DefaultImageManager;
import com.kremski.alert24.views.image.ImageManager;

public class EventInfoWindowsAdapter implements InfoWindowAdapter{

	private List<Event> allEvents;
	private ImageManager<Event> eventImageManager;
	private ImageManager<EventCategory> eventCategoryImageManager;
	private boolean isImageLoading = false;
	private EventInfoWindow infoWindow = null;

	public EventInfoWindowsAdapter(EventsMapScreen context, ImageManager<Event> eventImageManager, ImageManager<EventCategory> eventCategoryImageManager) {
		this.allEvents = Lists.newArrayList();
		this.eventImageManager = eventImageManager;
		this.eventCategoryImageManager = eventCategoryImageManager;
		infoWindow = new EventInfoWindow(context, this);
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	public void setImageLoading(boolean isLoading) {
		this.isImageLoading = isLoading;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		for (Event e : allEvents) {
			if (e.getServerId().equals(marker.getTitle())) {
				chainEventToInfoWindow(e, marker, infoWindow);
			}
		}
		return infoWindow;
	}
	
	public Event getEventUnderMarker(Marker marker) {
		for (Event e : allEvents) {
			if (e.getServerId().equals(marker.getTitle())) {
				return e;
			}
		}
		return null;
	}
	
	private void chainEventToInfoWindow(Event event, Marker marker, EventInfoWindow infoWindow) {
		infoWindow.setEventDetails(event, marker);
		if (!isImageLoading) {
			isImageLoading = true;
			eventCategoryImageManager.displayImage(EventInfoWindow.EVEN_PHOTO_CATEGORY_IMAGE_KEY, event.getCategory(), infoWindow);
		}
	}

	public boolean isImageLoading() {
		return isImageLoading;
	}
	
	public void addEvent(Event event) {
		allEvents.add(event);
	}

	public List<Event> getEvents() {
		return allEvents;
	}

	public void setEvents(List<Event> newEvents) {
		allEvents = Lists.newLinkedList(newEvents);
	}

}
