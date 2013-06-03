package com.kremski.alert24.serwer.service;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.collect.Lists;
import com.kremski.alert24.R;
import com.kremski.alert24.serwer.ServerUtilities;
import com.kremski.alert24.serwer.requests.DownloadEventsRequest;

public class WorkerDownloadingEvents extends DefaultWorker{

	private final ServerUtilities serverUtilities;
	private final int radiusInMeters;
	private final List<LatLng> pointsOfInterestCoords;

	public WorkerDownloadingEvents(Context context, List<LatLng> pointsOfInterestCoords, int radiusInMeters, ServerUtilities serverUtilities) {
		super(context);
		this.serverUtilities = serverUtilities; 
		this.pointsOfInterestCoords = Lists.newLinkedList(pointsOfInterestCoords);
		this.radiusInMeters = radiusInMeters;
	}

	public void execute() {
		try {
			Log.i(LOG_TAG, "Downloading events within radius " + radiusInMeters);
			String serverResponse = serverUtilities.makeGetRequest(new DownloadEventsRequest(radiusInMeters, pointsOfInterestCoords));
			Log.i(LOG_TAG, "Events downloaded " +  serverResponse);
			sendBroadcastWithResult(new WorkerResult(serverResponse));
		} catch (ClientProtocolException e) {
			sendBroadcastWithException(e, R.string.operation_failed, R.string.events_not_downloaded);
		} catch (IOException e) {
			sendBroadcastWithException(e, R.string.operation_failed, R.string.events_not_downloaded);
		} catch (IllegalStateException e) {
			sendBroadcastWithException(e, R.string.operation_failed, R.string.events_not_downloaded);
		} catch (JSONException e) {
			sendBroadcastWithException(e, R.string.operation_failed, R.string.events_not_downloaded);
		} 
	}

	protected void sendBroadcastWithResult(WorkerResult workerResult) {
		context.sendBroadcast(createIntentForBroadcastResult(workerResult));
	}
	
	@Override
	protected int getErrorMessageTitleId() {
		return R.string.downloading_events;
	}

	public Context getContext() {
		return context;
	}

	public ServerUtilities getServerUtilities() {
		return serverUtilities;
	}

	public int getRefreshRadius() {
		return radiusInMeters;
	}

	public List<LatLng> getPointsOfInterestCoords() {
		return pointsOfInterestCoords;
	}

}
