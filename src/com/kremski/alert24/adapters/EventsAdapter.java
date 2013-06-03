package com.kremski.alert24.adapters;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.common.collect.Lists;
import com.kremski.alert24.R;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.domain.EventCategory;
import com.kremski.alert24.screens.event_screens.EventScreen;
import com.kremski.alert24.sdcard.SDCardHelper;
import com.kremski.alert24.views.EventRowView;
import com.kremski.alert24.views.image.DefaultImageManager;
import com.kremski.alert24.views.image.ImageManager;

public class EventsAdapter extends BaseAdapter{

	private Context context;
	private List<Event> displayedEvents = Lists.newLinkedList();
	private SDCardHelper sdCardHelper = new SDCardHelper();
	private ImageManager<EventCategory> categoriesImageManager;

	public EventsAdapter(Context context, ImageManager<EventCategory> imageManager) {
		this.context = context;
		this.categoriesImageManager = imageManager;
	}

	@Override
	public int getCount() {
		return displayedEvents.size();
	}

	@Override
	public Object getItem(int position) {
		return displayedEvents.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Event eventToDisplay = displayedEvents.get(position);

		if (convertView == null) {
			convertView = new EventRowView(context);
		} 
		EventRowView eventRowView = (EventRowView)convertView;
		eventRowView.setEvent(eventToDisplay);
		categoriesImageManager.displayImage("EVENT_CATEGORY_IMAGE", eventToDisplay.getCategory(), eventRowView);
		return eventRowView;
	}

	public void setDisplayedEvents(List<Event> events) {
		this.displayedEvents = Lists.newLinkedList(events);
		notifyDataSetChanged();
	}

	public List<Event> getDisplayedEvents() {
		return Lists.newLinkedList(displayedEvents);
	}

	public synchronized void removeEvent(long removedEventId) {
		for (int position = 0; position < displayedEvents.size(); ++position) {
			if (displayedEvents.get(position).getDatabaseId() == removedEventId) {
				displayedEvents.remove(position);
				notifyDataSetChanged();
				return;
			}
		}
	}

	public void updateEvent(Event updatedEvent) {
		for (int i = 0; i < displayedEvents.size(); ++i) {
			Event eventToChange = displayedEvents.get(i);
			if (eventToChange.getDatabaseId() == updatedEvent.getDatabaseId()) {
				displayedEvents.set(i, updatedEvent);
				notifyDataSetChanged();
				return;
			}
		}
	}

}
