package com.kremski.alert24.serwer.requests;

import java.util.List;

import org.json.JSONException;
import org.json.JSONStringer;

import com.google.android.gms.maps.model.LatLng;

public class DownloadEventsRequest extends GetRequest {

	public DownloadEventsRequest(int radius, List<LatLng> coords) throws JSONException {
		action = "/events?";
		
		JSONStringer jsonStringer = new JSONStringer();
		jsonStringer.object().key("Coords").array();
		for (LatLng c : coords) {
			jsonStringer.object().key("Latitude").value(c.latitude).key("Longitude").value(c.longitude).endObject();
		}
		action += jsonStringer.endArray().endObject().toString();
		
		action += "&Radius=" + radius;
	}
}
