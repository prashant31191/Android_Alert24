package com.kremski.alert24.serwer.command;

import com.kremski.alert24.serwer.requests.GetRequest;

public class DownloadEventCategoriesRequest extends GetRequest {

	public DownloadEventCategoriesRequest() {
		action = "/categories?format=json"; 
	}

}
