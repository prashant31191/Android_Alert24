package com.kremski.alert24.notifications;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.kremski.alert24.R;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.screens.events_map.EventsMapScreen;

public class NotificationFactory {

	private Context context;
	public final static String NOTIFICATION_TYPE_KEY = "NOTIFICATION_TYPE_KEY";
	public final static String NOTIFICATION_ABOUT_NEW_EVENT_RECEIVED_FROM_SERVER = "NOTIFICATION_ABOUT_NEW_EVENT_RECEIVED_FROM_SERVER";
	public static final String NOTIFICATION_BROADCAST = "NOTIFICATION_BROADCAST";

	public NotificationFactory(Context context) {
		this.context = context;
	}

	public Notification createNotificationAboutNewEventReceivedFromServer(Event eventWhichWasReceived, ArrayList<Event> alreadyDisplayedEvents) {
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
		.setContentTitle("Zarejestrowano nowe zdarzenie")
		.setContentText(eventWhichWasReceived.getName());

		Intent intent = new Intent(context, EventsMapScreen.class);
		intent.putExtra(NOTIFICATION_TYPE_KEY, NOTIFICATION_ABOUT_NEW_EVENT_RECEIVED_FROM_SERVER);
		intent.putExtra(Event.class.toString(), eventWhichWasReceived);
		intent.putParcelableArrayListExtra(List.class.toString(), alreadyDisplayedEvents);
		intent.setAction(NOTIFICATION_BROADCAST);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		notificationBuilder.setContentIntent(pIntent);
		return notificationBuilder.build();
	}
}
