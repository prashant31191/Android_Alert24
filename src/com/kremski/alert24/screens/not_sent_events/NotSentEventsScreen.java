package com.kremski.alert24.screens.not_sent_events;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.google.common.base.Preconditions;
import com.kremski.alert24.R;
import com.kremski.alert24.adapters.EventsAdapter;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.screens.event_screens.EditEventScreen;
import com.kremski.alert24.screens.event_screens.EventScreen;
import com.kremski.alert24.serwer.service.WorkerRemovingEvent;
import com.kremski.alert24.serwer.service.WorkerRemovingSelectedEvents;
import com.kremski.alert24.serwer.service.WorkerSendingEvent;
import com.kremski.alert24.serwer.service.WorkerSendingEvent.OnEventSentListener;
import com.kremski.alert24.serwer.service.WorkerUpdatingEvent;
import com.kremski.alert24.tasks.TaskLoadingEvents;
import com.kremski.alert24.tasks.TaskLoadingEvents.OnEventsLoadedListener;
import com.kremski.alert24.views.ActionBarOption;
import com.kremski.alert24.views.EventRowView;
import com.kremski.alert24.views.EventsListFragment;

public class NotSentEventsScreen extends Alert24Screen implements OnEventsLoadedListener {

	private EventsAdapter adapterWithEvents = null;
	private EventsListFragment eventsListFragment;

	public static final int SELECT_PHOTO_REQUEST_CODE = 1;
	public static final int SELECT_LOCATION_REQUEST_CODE = 2;

	@Override
	protected void prepareActionBarOptions(
			LinkedList<ActionBarOption> actionbarChoosableOptions) {
		super.prepareActionBarOptions(actionbarChoosableOptions);
		ActionBarOption removeNotSentEvents = new ActionBarOption(R.drawable.delete, 
				R.string.remove_not_sent_events_action, createListenerForRemoveNotSentEventsAction());
		actionbarChoosableOptions.add(removeNotSentEvents);
	}

	private OnClickListener createListenerForRemoveNotSentEventsAction() {
		return new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (adapterWithEvents.getCount() > 0) {
					WorkerRemovingSelectedEvents worker = new WorkerRemovingSelectedEvents(NotSentEventsScreen.this, 
							getEventsTableHelper(), adapterWithEvents.getDisplayedEvents());
					executeWorker(worker);
				}
			}
		};
	}

	@Override
	protected int getContentViewId() {
		return R.layout.not_sent_events_screen;
	}
	
	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		eventsListFragment = (EventsListFragment) getSupportFragmentManager().findFragmentById(R.id.events);
		eventsListFragment.setListAdapter(adapterWithEvents = new EventsAdapter(this, getEventCategoryImageManager()));
		registerForContextMenu(eventsListFragment.getListView());

		setActionBarTitle(R.string.not_sents_events_screen_title);

		
		registerWorkerResultHandler(WorkerRemovingSelectedEvents.class.toString(), 
				new EventsRemovedHandler(this));
		registerWorkerResultHandler(WorkerRemovingEvent.class.toString(), 
				new SingleEventRemovedHandler(this));
		registerWorkerResultHandler(WorkerSendingEvent.class.toString(), 
				new SentEventHandler(this));
	}
	
	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();

		new TaskLoadingEvents(this, getEventsTableHelper(), this, true).execute(new Void[]{});
	}

	@Override
	public void onEventsLoaded(final List<Event> loadedEvents) {
		if (loadedEvents.size() > 0 ) {
			adapterWithEvents.setDisplayedEvents(loadedEvents);
		} else {
			eventsListFragment.setEmptyText(getResources().getString(R.string.zero_not_sent_events));
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.events_list_fragment_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		Event clickedEvent = getEventFromView(info.targetView);

		if (item.getItemId() == R.id.send_event) {
			if (isOperationInProgress(WorkerSendingEvent.class.toString())) {
				dialogManager.showInfoDialog(R.string.operation_in_progress);
			} else {
				sendEventToServer(clickedEvent);
			}
		} else if (item.getItemId() == R.id.delete_event) {
			removeEventFromDevice(clickedEvent);
		} else if (item.getItemId() == R.id.edit_event) {
			startActivityToEditEvent(clickedEvent);
		} else if (item.getItemId() == R.id.show_event) {
			startActivityToShowEvent(clickedEvent);
		}

		return super.onContextItemSelected(item);
	}

	private Event getEventFromView(View clickedView) {
		Preconditions.checkArgument(clickedView instanceof EventRowView , "Not EventRowView");
		Event clickedEvent = ((EventRowView)clickedView).getEvent();
		return clickedEvent;
	}

	private void sendEventToServer(final Event eventToSend) {
		String deviceKey = optionsManager.getDeviceKey();
		eventToSend.setDeviceKey(deviceKey);
		
		OnEventSentListener onEventSentListener = new OnEventSentListener() {
			
			@Override
			public void onEventSent(Event sentEvent) {
				new WorkerUpdatingEvent(NotSentEventsScreen.this, getEventsTableHelper(), sentEvent, false).execute();
			}
		};
		
		WorkerSendingEvent sendingWorker = new WorkerSendingEvent(this, eventToSend, serverUtilities, onEventSentListener);
		executeWorker(sendingWorker, R.string.sending_event_to_server);
	}
	

	private void removeEventFromDevice(Event eventToRemove) {
		WorkerRemovingEvent workerRemovingEventFromDevice = new WorkerRemovingEvent(NotSentEventsScreen.this, eventTableHelper, eventToRemove);
		executeWorker(workerRemovingEventFromDevice);
	}

	private void startActivityToEditEvent(Event eventToEdit) {
		Intent intent = new Intent(this, EditEventScreen.class);
		intent.putExtra(Event.class.toString(), eventToEdit);
		startActivityForResult(intent, EditEventScreen.EDIT_EVENT_REQUEST);
	}

	private void startActivityToShowEvent(Event eventToShow) {
		Intent intentWithClickedEvent = new Intent(this, EventScreen.class);
		intentWithClickedEvent.putExtra(Event.class.toString(), eventToShow);
		startActivity(intentWithClickedEvent);
	}

	public void removeEvent(long removedEventId) {
		adapterWithEvents.removeEvent(removedEventId);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == EditEventScreen.EDIT_EVENT_REQUEST) {
				Event editedEvent = intent.getParcelableExtra(Event.class.toString());
				adapterWithEvents.updateEvent(editedEvent);
			} 
		}
	}


}
