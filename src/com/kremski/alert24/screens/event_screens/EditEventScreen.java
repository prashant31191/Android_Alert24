package com.kremski.alert24.screens.event_screens;

import android.content.Intent;
import android.os.Bundle;

import com.kremski.alert24.R;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.serwer.service.DefaultWorkerResultHandler;
import com.kremski.alert24.serwer.service.WorkerRetrivingAddress;
import com.kremski.alert24.serwer.service.WorkerUpdatingEvent;
import com.kremski.alert24.views.EditEventFormFragment;

public class EditEventScreen extends Alert24Screen {

	private EditEventFormFragment editEventFormFragment;
	public static final int EDIT_EVENT_REQUEST = 0;

	@Override
	protected int getContentViewId() {
		return R.layout.edit_event_screen;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();

		if (intent != null) {
			Event eventToEdit = intent.getParcelableExtra(Event.class.toString());
			editEventFormFragment = (EditEventFormFragment)getSupportFragmentManager().findFragmentById(R.id.edit_event_form);
			editEventFormFragment.setActivity(this);
			editEventFormFragment.setupFormLabels(eventToEdit);
			setActionBarTitle(R.string.edit_event_screen_title);
			
			registerWorkerResultHandler(WorkerRetrivingAddress.class.toString(), new AddressRetrivedHandler(this, editEventFormFragment));
			registerWorkerResultHandler(WorkerUpdatingEvent.class.toString(), new DefaultWorkerResultHandler(this));
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
}
