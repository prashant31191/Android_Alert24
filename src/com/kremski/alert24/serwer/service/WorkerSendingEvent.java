package com.kremski.alert24.serwer.service;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.kremski.alert24.CommonUtilities;
import com.kremski.alert24.R;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.sdcard.SDCardHelper;
import com.kremski.alert24.serwer.ServerUtilities;
import com.kremski.alert24.serwer.requests.SendEventRequest;
import com.kremski.alert24.serwer.service.Alert24Service.Worker;

public class WorkerSendingEvent extends DefaultWorker{

	private final Event eventToSend;
	private final ServerUtilities serverUtilities;
	private final SDCardHelper sdCardHelper = new SDCardHelper();
	private OnEventSentListener onEventSentListener;
	public static final String INTENT_KEY_OF_SENT_EVENT_ID = "sent_event_id";

	public WorkerSendingEvent(Alert24Screen screen, Event eventToSend, ServerUtilities serverUtilities, OnEventSentListener onEventSentListener) {
		super(screen);
		this.eventToSend = eventToSend;
		this.serverUtilities = serverUtilities; 
		this.onEventSentListener = onEventSentListener;
	}

	public void execute() {
		try {
			String serverResponse = sendEvent();

			if (ServerUtilities.isResponseSuccessfull(serverResponse) && onEventSentListener != null) {
				eventToSend.setSent(true);
				onEventSentListener.onEventSent(eventToSend);
			}

			sendBroadcastWithResult(new WorkerResult(serverResponse));
		} catch (ClientProtocolException e) {
			sendBroadcastWithException(e, R.string.operation_failed, R.string.event_not_snet);
		} catch (IOException e) {
			sendBroadcastWithException(e, R.string.operation_failed, R.string.event_not_snet);
		} catch (JSONException e) {
			sendBroadcastWithException(e, R.string.operation_failed, R.string.event_not_snet);
		}
	}

	private String sendEvent() throws ClientProtocolException, IOException, JSONException {
		String serverResponse = sendEventTextData();

		if (ServerUtilities.isResponseSuccessfull(serverResponse)) {
			serverResponse = sendEventPhoto(new JSONObject(serverResponse).getString("ContentUrl"));
		}

		return serverResponse;
	}

	private String sendEventTextData() throws ClientProtocolException, IOException, JSONException {
		Log.i(LOG_TAG, "Uploading event text data " + eventToSend.toString());
		String serverResponse = serverUtilities.makePostRequest(new SendEventRequest(eventToSend));
		Log.i(LOG_TAG, "Event text data upload response : " + serverResponse);
		return serverResponse;
	}

	private String sendEventPhoto(String urlToUploadPhoto) throws ClientProtocolException, IOException, JSONException {
		String serverResponse = null;
		if (eventToSend.getPhotoBitmap() == null) { // Bitmap can be on disc 
			serverResponse = sendPhotoStoredOnSdCard(urlToUploadPhoto);
		} else {
			serverResponse = sendPhotoBitmap(urlToUploadPhoto, eventToSend.getPhotoBitmap());
		}
		return serverResponse;
	}

	private String sendPhotoStoredOnSdCard(String urlToUploadPhoto) throws ClientProtocolException, IOException, JSONException {
		Bitmap eventPhotoBitmap = sdCardHelper.loadEventPhotoBitmap(eventToSend, 320, 240);
		if (eventPhotoBitmap != null) { 
			eventToSend.setPhotoBitmap(eventPhotoBitmap);
			return sendPhotoBitmap(urlToUploadPhoto, eventPhotoBitmap);
		}
		return ""; //TODO handle situation when there is no photo on disc
	}

	private String sendPhotoBitmap(String urlToUploadPhoto, Bitmap photoBitmap) throws ClientProtocolException, IOException, JSONException {
		Log.i(LOG_TAG, "Uploading photo data");
		String serverResponse = serverUtilities.sendPhotoBitmap(urlToUploadPhoto, photoBitmap);
		Log.i(LOG_TAG, "Photo uploading response : " + serverResponse);
		return serverResponse;
	}
	
	private void sendBroadcastWithResult(WorkerResult workerResult) {
		Intent intent = createIntentForBroadcastResult(workerResult);
		intent.putExtra(INTENT_KEY_OF_SENT_EVENT_ID, eventToSend.getDatabaseId());
		context.sendBroadcast(intent);
	}
	
	@Override
	protected int getErrorMessageTitleId() {
		return R.string.sending_event_to_server;
	}

	public interface OnEventSentListener {
		public void onEventSent(Event sentEvent);
	}
}
