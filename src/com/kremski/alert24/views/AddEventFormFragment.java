package com.kremski.alert24.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kremski.alert24.R;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.screens.event_screens.NewEventScreen;
import com.kremski.alert24.serwer.service.WorkerInsertingEvent;
import com.kremski.alert24.serwer.service.WorkerSendingEvent;
import com.kremski.alert24.serwer.service.WorkerSendingEvent.OnEventSentListener;

public class AddEventFormFragment extends EventFormFragment<NewEventScreen> {

	private View saveEventButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = super.onCreateView(inflater, container, savedInstanceState);

		saveEventButton = layout.findViewById(R.id.save);
		saveEventButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				WorkerInsertingEvent workerInsertingEventToDatabase = new WorkerInsertingEvent(alert24Screen, 
						alert24Screen.getEventsTableHelper(), createEventFromFields(), true);

				alert24Screen.executeWorker(workerInsertingEventToDatabase, R.string.adding_event_to_database);
			}
		});

		return layout;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.add_event_form;
	}

	protected void onAcceptEvent(Event acceptedEvent) {
		if (alert24Screen.isDeviceOnline()) {
			if (checkedEventCategory == null) {
				alert24Screen.showInfoDialog(R.string.please_check_event_category);
			} else {
				sendEventToServer(acceptedEvent);
			}
		} else {
			WorkerInsertingEvent workerInsertingEventToDatabase = new WorkerInsertingEvent(alert24Screen, 
					alert24Screen.getEventsTableHelper(), acceptedEvent, true);

			alert24Screen.executeWorker(workerInsertingEventToDatabase, R.string.not_connected, R.string.adding_event_to_database);
		}
	}

	private void sendEventToServer(Event event) {
		Event eventToSend = eventBuilder.photoBitmap(event.getPhotoBitmap()).name(event.getName())
				.descritpion(event.getDescription()).deviceKey(alert24Screen.getDeviceKey())
				.latitude(event.getLatitude()).longitude(event.getLongitude())
				.build();

		OnEventSentListener onEventSentListener = new OnEventSentListener() {
			@Override
			public void onEventSent(Event sentEvent) {
				new WorkerInsertingEvent(alert24Screen, alert24Screen.getEventsTableHelper(), sentEvent, false).execute();
			}
		};

		WorkerSendingEvent workerSendingEventToServer = new WorkerSendingEvent(alert24Screen, eventToSend, 
				alert24Screen.getServerUtilities(), onEventSentListener);

		alert24Screen.executeWorker(workerSendingEventToServer, R.string.sending_event_to_server);
	}
}
