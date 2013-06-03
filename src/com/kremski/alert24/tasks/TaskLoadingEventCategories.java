package com.kremski.alert24.tasks;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kremski.alert24.database.EventCategoriesTable;
import com.kremski.alert24.domain.EventCategory;
import com.kremski.alert24.screens.Alert24Screen;

public class TaskLoadingEventCategories extends DefaultTask<Void, Void, LinkedHashMap<EventCategory, List<EventCategory>>>{
	
	private EventCategoriesTable eventCategoriesTableHelper;
	private OnEventCategoriesLoadedListener onLoadedListener;
		
	private Comparator<EventCategory> byEventCategoryNameComparator = new Comparator<EventCategory>() {

		@Override
		public int compare(EventCategory arg0, EventCategory arg1) {
			return arg0.getName().compareTo(arg1.getName());
		}
	};

	public TaskLoadingEventCategories(Alert24Screen acitivty, EventCategoriesTable eventCategoriesTableHelper, OnEventCategoriesLoadedListener onLoadedListener) {
		super(acitivty);
		this.eventCategoriesTableHelper = eventCategoriesTableHelper;
		this.onLoadedListener = onLoadedListener;
	}

	@Override
	protected LinkedHashMap<EventCategory, List<EventCategory>> execute() {
		Log.i(LOG_TAG, "Selecting event categories from database");
		List<EventCategory> allCategories = eventCategoriesTableHelper.getAll();
		Log.i(LOG_TAG, "Event categories have been selected");
		return groupCategories(allCategories);
	}
	
	private LinkedHashMap<EventCategory, List<EventCategory>> groupCategories(List<EventCategory> allCategories) {
		List<EventCategory> parentCategories = getParentCategories(allCategories);
		allCategories.removeAll(parentCategories);
		return getGroupedCategories(parentCategories, allCategories);
	}
	
	private List<EventCategory> getParentCategories(List<EventCategory> allCategories) {
		List<EventCategory> parentCategories = Lists.newLinkedList();

		for (EventCategory c : allCategories) {
			
			if (c.getDatabaseParentId() == -1) {
				parentCategories.add(c);
			}
		}

		Collections.sort(parentCategories, byEventCategoryNameComparator);

		return parentCategories;
	}

	private LinkedHashMap<EventCategory, List<EventCategory>> getGroupedCategories(List<EventCategory> parentCategories, List<EventCategory> childCategories) {
		LinkedHashMap<EventCategory, List<EventCategory>> groupedCategories = Maps.newLinkedHashMap();
		
		for(EventCategory parent : parentCategories) {
			List<EventCategory> subCategories = Lists.newLinkedList();
			
			for (EventCategory child : childCategories) {
				
				if (parent.getDatabaseId() == child.getDatabaseParentId()) {
					subCategories.add(child);
				}
			}
			
			Collections.sort(subCategories, byEventCategoryNameComparator);

			groupedCategories.put(parent, subCategories);
		}

		return groupedCategories;
	}

	@Override
	protected void handleResult(LinkedHashMap<EventCategory, List<EventCategory>> categories) {
		onLoadedListener.onEventCategoriesLoadedListener(categories);
	}
	
	public interface OnEventCategoriesLoadedListener {
		public void onEventCategoriesLoadedListener(LinkedHashMap<EventCategory, List<EventCategory>> groupedCategories);
	}
	
}
