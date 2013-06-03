package com.kremski.alert34.resolvers;

import com.kremski.alert24.domain.EventCategory;
import com.kremski.alert24.views.image.ImageUrlResolver;

public class EventCategoryToImageUrlResolver implements ImageUrlResolver<EventCategory> {

	@Override
	public String resolve(EventCategory eventCategory) {
		return eventCategory.getIconUrl();
	}
}
