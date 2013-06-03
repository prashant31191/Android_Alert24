package com.kremski.alert24.serwer.requests;

public class RetriveDeviceKeyRequest extends GetRequest{

	public RetriveDeviceKeyRequest(String registrationID) {
		action = "/device/key/" + registrationID;
	}
}
