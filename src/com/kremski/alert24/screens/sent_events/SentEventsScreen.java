package com.kremski.alert24.screens.sent_events;

import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.kremski.alert24.R;
import com.kremski.alert24.adapters.EventsAdapter;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.screens.not_sent_events.NotSentEventsScreen;
import com.kremski.alert24.serwer.service.WorkerRemovingSelectedEvents;
import com.kremski.alert24.serwer.service.WorkerResult;
import com.kremski.alert24.tasks.TaskLoadingEvents;
import com.kremski.alert24.tasks.TaskLoadingEvents.OnEventsLoadedListener;
import com.kremski.alert24.views.ActionBarOption;
import com.kremski.alert24.views.EventsListFragment;

public class SentEventsScreen extends Alert24Screen implements OnEventsLoadedListener {

	private EventsAdapter adapterWithEvents;
	private EventsListFragment eventsListFragment; 

	@Override
	protected void prepareActionBarOptions(LinkedList<ActionBarOption> actionbarChoosableOptions) {
		super.prepareActionBarOptions(actionbarChoosableOptions);
		ActionBarOption removeNotSentEvents = new ActionBarOption(R.drawable.delete, R.string.remove_sent_events_action, createListenerOptionRemovingNotSentEvents());
		actionbarChoosableOptions.add(removeNotSentEvents);
	}

	private OnClickListener createListenerOptionRemovingNotSentEvents() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (adapterWithEvents.getCount() > 0) {
					WorkerRemovingSelectedEvents worker = new WorkerRemovingSelectedEvents(SentEventsScreen.this, 
							getEventsTableHelper(), adapterWithEvents.getDisplayedEvents());
					executeWorker(worker);
				}
			}
		};
	}

	@Override
	protected int getContentViewId() {
		return R.layout.sent_events_screen;
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);

		eventsListFragment = (EventsListFragment)getSupportFragmentManager().findFragmentById(R.id.events);
		eventsListFragment.setListAdapter(adapterWithEvents = new EventsAdapter(this, getEventCategoryImageManager()));
		registerForContextMenu(eventsListFragment.getListView());

		setActionBarTitle(R.string.sent_events_screen_title);

		registerWorkerResultHandler(WorkerRemovingSelectedEvents.class.toString(), 
				new EventsRemovedHandler(this));
	}

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();

		new TaskLoadingEvents(this, getEventsTableHelper(), this, false).execute(new Void[]{});
	}

	@Override
	public void onEventsLoaded(List<Event> events) {
		if (events.size() > 0 ) {
			adapterWithEvents.setDisplayedEvents(events);
		} else {
			eventsListFragment.setEmptyText(getResources().getString(R.string.zero_sent_events));
		}
	}

	public void removeEvent(long removedEventId) {
		adapterWithEvents.removeEvent(removedEventId);
	}

}
