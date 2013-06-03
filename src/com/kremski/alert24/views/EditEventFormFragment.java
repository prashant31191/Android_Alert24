package com.kremski.alert24.views;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.kremski.alert24.R;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.screens.event_screens.EditEventScreen;
import com.kremski.alert24.serwer.service.WorkerRetrivingAddress;
import com.kremski.alert24.serwer.service.WorkerUpdatingEvent;
import com.kremski.alert24.tasks.TaskLoadingEventImage;
import com.kremski.alert24.tasks.TaskLoadingEventImage.OnEventBitmapLoadedListener;

public class EditEventFormFragment extends EventFormFragment<EditEventScreen> implements OnEventBitmapLoadedListener{

	private Event eventToEdit;

	@Override
	protected void onAcceptEvent(Event eventWithUpdatedInformations) {
		Event eventToUpdate = getEventToUpdate(eventWithUpdatedInformations);
		Intent intentWithEditedEvent = new Intent();
		intentWithEditedEvent.putExtra(Event.class.toString(), eventToUpdate);
		alert24Screen.executeWorker(new WorkerUpdatingEvent(alert24Screen, alert24Screen.getEventsTableHelper(), eventToUpdate, true), R.string.updating_event_on_device);
		alert24Screen.setResult(Activity.RESULT_OK, intentWithEditedEvent);
		alert24Screen.finish();
	}

	private Event getEventToUpdate(Event eventAcceptedInForm) {
		eventToEdit.setName(eventAcceptedInForm.getName());
		eventToEdit.setDescription(eventAcceptedInForm.getDescription());
		eventToEdit.setPhotoBitmap(eventAcceptedInForm.getPhotoBitmap());
		eventToEdit.setAddress(eventAcceptedInForm.getAddress());
		eventToEdit.setLatitude(eventAcceptedInForm.getLatitude());
		eventToEdit.setLongitude(eventAcceptedInForm.getLongitude());
		eventToEdit.setEventCategory(checkedEventCategory);
		return eventToEdit;
	}

	public void setupFormLabels(Event eventToEdit) {
		this.eventToEdit = eventToEdit;

		eventNameLabel.setText(eventToEdit.getName());
		eventDescriptionLabel.setText(eventToEdit.getDescription());
		
		onTypeAccept(eventToEdit.getCategory());

		WorkerRetrivingAddress worker = new WorkerRetrivingAddress(getActivity(), 
				new LatLng(eventToEdit.getLatitude(), eventToEdit.getLongitude()));
		alert24Screen.executeWorker(worker);
		
		new TaskLoadingEventImage(alert24Screen, eventToEdit, this).execute(new Void[]{});
	}

	@Override
	public void onEventBitmapLoaded(Bitmap eventBitmap) {
		eventPhotoLabel.setImageBitmap(eventBitmap);
		eventPhotoLabel.setVisibility(View.VISIBLE);
	}
}
