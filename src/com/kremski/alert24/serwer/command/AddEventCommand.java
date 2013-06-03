package com.kremski.alert24.serwer.command;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.domain.Event.EventBuilder;


public class AddEventCommand extends DefaultServerCommand {
	
	public static final String EVENT_JSON_OBJECT_KEY = "Event";
	
	private EventBuilder eventBuilder = new EventBuilder();
	
	public AddEventCommand(ServerCommandHandler serverCommandHandler, JSONObject serverCommand) {
		super(serverCommandHandler, serverCommand);
	}

	@Override
	public void execute() throws JSONException {
//		JSONObject eventData = new JSONObject(serverCommand.getString("Event"));
//		String name = eventData.getString("Name");
//		String description = eventData.getString("Description");
//		String photoUrl = eventData.getString("PhotoUrl");
//		String eventId = eventData.getString("Id");
//		double latitude = Double.parseDouble(eventData.getString("Latitude").replace(",", "."));
//		double longitude = Double.parseDouble(eventData.getString("Longitude").replace(",", "."));
//		
//		Event event = eventBuilder 
//				.serverId(eventId)
//				.name(name)
//				.descritpion(description)
//				.photoUrl(photoUrl)
//				.latitude(latitude)
//				.longitude(longitude)
//				.build();
//		
//		serverCommandHandler.addEvent(event);
	}
	
	public interface AddEventCommandHandler {
		public void addEvent(Event event);
	}
}
