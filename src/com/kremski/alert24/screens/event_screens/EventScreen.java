package com.kremski.alert24.screens.event_screens;

import android.content.Intent;
import android.os.Bundle;

import com.kremski.alert24.R;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.serwer.service.WorkerResult;
import com.kremski.alert24.views.EventInformationsFragment;
import com.kremski.alert24.views.EventFragmentsContainer;

public class EventScreen extends Alert24Screen {

	private Event eventOnScreen;
	private EventFragmentsContainer eventFragmentsContainer;

	@Override
	protected int getContentViewId() {
		return R.layout.event_preview_screen;
	}
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		Intent startIntent = getIntent();

		eventFragmentsContainer = (EventFragmentsContainer) getSupportFragmentManager().findFragmentById(R.id.event_fragments_container);

		if (startIntent != null) {
			eventOnScreen = (Event)startIntent.getParcelableExtra(Event.class.toString());
			
			eventFragmentsContainer.initialize(this, getSupportFragmentManager(), eventOnScreen);
//			eventFragmentsContainer.setPhotoImageManager(eventPhotoImageManager);
//			eventFragmentsContainer.setActivity(this);
//			eventFragmentsContainer.setDisplayedEvent(eventOnScreen);
		}
		
		setActionBarTitle(R.string.event_screen_title);
	}
}
