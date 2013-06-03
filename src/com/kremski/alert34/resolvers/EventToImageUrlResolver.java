package com.kremski.alert34.resolvers;

import com.kremski.alert24.domain.Event;
import com.kremski.alert24.views.image.ImageUrlResolver;

public class EventToImageUrlResolver implements ImageUrlResolver<Event> {
	
	public String resolve(Event event) {
		if (event.getPhotoUrls().isEmpty()) {
			return "";
		} else {
			return event.getPhotoUrls().get(0);
		}
	}
}
