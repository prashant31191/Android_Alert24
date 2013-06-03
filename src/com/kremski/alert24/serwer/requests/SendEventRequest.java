package com.kremski.alert24.serwer.requests;

import org.json.JSONException;
import org.json.JSONStringer;

import com.kremski.alert24.domain.Event;

public class SendEventRequest extends PostRequest {
	
	public SendEventRequest(Event eventToSend) throws JSONException {
		content = new JSONStringer()
		.object()
			.key("Name").value(eventToSend.getName())
			.key("Description").value(eventToSend.getDescription())
			.key("DeviceKey").value(eventToSend.getDeviceKey())
			.key("Latitude").value(eventToSend.getLatitude())
			.key("Longitude").value(eventToSend.getLongitude())
			.key("CategoryId").value(eventToSend.getCategory().getServerId())
		.endObject().toString();

		action = "/notification/add";
	}
}
