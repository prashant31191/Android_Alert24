package com.kremski.alert24.screens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.kremski.alert24.R;
import com.kremski.alert24.screens.event_screens.NewEventScreen;
import com.kremski.alert24.screens.events_map.EventsMapScreen;
import com.kremski.alert24.screens.not_sent_events.NotSentEventsScreen;
import com.kremski.alert24.screens.sent_events.SentEventsScreen;

class RibbonMenuItemClickHandler {
	
	private final Activity alert24Screen;
	private final Context context;

	public RibbonMenuItemClickHandler(Activity alert24Screen, Context context) {
		this.alert24Screen = alert24Screen;
		this.context = context;
	} 

	public void onClick(int itemId) {
		switch(itemId) {
			case R.id.map_of_events: {
				Intent intent = new Intent(context, EventsMapScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				alert24Screen.startActivity(intent);
				break;
			}
			case R.id.sent_events: {
				alert24Screen.startActivity(new Intent(context, SentEventsScreen.class));
				break;
			}
			case R.id.not_sent_events: {
				alert24Screen.startActivity(new Intent(context, NotSentEventsScreen.class));
				break;
			}
			case R.id.add_new_event: {
				alert24Screen.startActivity(new Intent(context, NewEventScreen.class));
				break;
			}
			case R.id.options: {
				alert24Screen.startActivity(new Intent(context, OptionsScreen.class));
				break;
			}
			case R.id.points_of_interest: {
				alert24Screen.startActivity(new Intent(context, PointsOfInterestScreen.class));
				break;
			}
			case R.id.system_notifications: {
				alert24Screen.startActivity(new Intent(context, SystemNotificationsScreen.class));
				break;
			}
		}
	}
}