package com.kremski.alert24.serwer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.common.collect.Lists;
import com.kremski.alert24.R;
import com.kremski.alert24.database.EventCategoriesTable;
import com.kremski.alert24.domain.EventCategory;
import com.kremski.alert24.serwer.ServerUtilities;
import com.kremski.alert24.serwer.command.DownloadEventCategoriesRequest;
import com.kremski.alert24.views.image.ImageManager;

public class WorkerDownloadingEventCategories extends DefaultWorker{

	private final ServerUtilities serverUtilities;
	private final EventCategoriesTable categoriesTableHelper;
	private final ImageManager<EventCategory> eventCategoryImageManager;

	public WorkerDownloadingEventCategories(ImageManager<EventCategory> eventCategoryImageManager, Context context, ServerUtilities serverUtilities, EventCategoriesTable categoriesTableHelper) {
		super(context);
		this.serverUtilities = serverUtilities; 
		this.categoriesTableHelper = categoriesTableHelper;
		this.eventCategoryImageManager = eventCategoryImageManager;
	}

	public void execute() {
		try {
			Log.i(LOG_TAG, "Downloading event categories");
			String serverResponse = serverUtilities.makeGetRequest(new DownloadEventCategoriesRequest());
			Log.i(LOG_TAG, "Event categories downloaded");
			ArrayList<EventCategory> categories = Lists.newArrayList(insertEventCategoriesIntoDatabase(serverResponse));
			Log.i(LOG_TAG, "Inserting event categories into database");
			sendBroadcastWithResult(categories, new WorkerResult(serverResponse));
			Log.i(LOG_TAG, "Event categories inserted into database");
		} catch (ClientProtocolException e) {
			sendBroadcastWithException(e, R.string.operation_failed, R.string.event_categories_not_downloaded);
		} catch (IOException e) {
			sendBroadcastWithException(e, R.string.operation_failed, R.string.event_categories_not_downloaded);
		} catch (JSONException e) {
			sendBroadcastWithException(e, R.string.operation_failed, R.string.event_categories_not_downloaded);
		} 
	}

	private List<EventCategory> insertEventCategoriesIntoDatabase(String textCategories) throws JSONException {
		JSONArray serverCategories = new JSONArray(textCategories);
		List<EventCategory> downloadedCategories = Lists.newLinkedList(); 

		for(int i = 0; i < serverCategories.length(); ++i) {
			JSONObject parentCatJson = serverCategories.getJSONObject(i);
			EventCategory parent = new EventCategory(parentCatJson.getString("Name"), parentCatJson.getString("Id"));
			long parentId = categoriesTableHelper.insert(parent);
			downloadedCategories.addAll(insertSubCategoriesIntoDatabase(parentCatJson, parentId));
		}
		return downloadedCategories;
	}

	private List<EventCategory> insertSubCategoriesIntoDatabase(JSONObject parentCatJson, long parentId) throws JSONException {
		JSONArray subcategories = parentCatJson.getJSONArray("Subcategories");
		List<EventCategory> downloadedCategories = Lists.newLinkedList(); 
		for(int j = 0; j < subcategories.length(); ++j) {
			JSONObject subcategory = subcategories.getJSONObject(j);

			EventCategory eventCategory = new EventCategory(subcategory.getString("Name"), subcategory.getString("IconUrl"), -1, parentId, subcategory.getString("Id"));
			categoriesTableHelper.insert(eventCategory);
			downloadedCategories.add(eventCategory);
		}
		return downloadedCategories;
	}

	protected void sendBroadcastWithResult(ArrayList<EventCategory> categories, WorkerResult workerResult) {
		Intent intent = createIntentForBroadcastResult(workerResult);
		intent.putParcelableArrayListExtra(List.class.toString(), categories);
		context.sendBroadcast(intent);
	}
	

	@Override
	protected int getErrorMessageTitleId() {
		return R.string.downloading_event_categories;
	}

}
