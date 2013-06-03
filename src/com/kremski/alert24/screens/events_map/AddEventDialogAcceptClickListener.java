package com.kremski.alert24.screens.events_map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcel;
import android.widget.Toast;

import com.kremski.alert24.R;
import com.kremski.alert24.database.EventsTable;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.domain.Event.EventBuilder;
import com.kremski.alert24.screens.event_screens.NewEventScreen;
import com.kremski.alert24.serwer.ServerUtilities;
import com.kremski.alert24.serwer.service.WorkerInsertingEvent;
import com.kremski.alert24.serwer.service.WorkerUpdatingEvent;
import com.kremski.alert24.serwer.service.WorkerSendingEvent;

class AddEventDialogAcceptClickListener {

	private ServerUtilities serverUtilities;
	private EventBuilder eventBuilder = new EventBuilder();
	private String deviceKey;
	private EventsMapScreen mainScreen;

	AddEventDialogAcceptClickListener(EventsMapScreen mainScreen, ServerUtilities serverUtilities, String deviceKey) {
		this.serverUtilities = serverUtilities;
		this.deviceKey = deviceKey;
		this.mainScreen = mainScreen;
	}

}