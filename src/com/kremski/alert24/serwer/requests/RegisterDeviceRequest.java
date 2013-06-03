package com.kremski.alert24.serwer.requests;

import org.json.JSONException;
import org.json.JSONStringer;

public class RegisterDeviceRequest extends PostRequest{

	public RegisterDeviceRequest(String registrationID) throws JSONException {
		content = new JSONStringer()
			.object()
				.key("RegistrationId").value(registrationID)
				.key("Type").value("Android")
			.endObject().toString();

		action = "/device/register";
	}

}
