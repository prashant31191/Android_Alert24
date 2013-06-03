package com.kremski.alert24.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.kremski.alert24.R;
import com.kremski.alert24.adapters.EventInfoWindowsAdapter;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.screens.event_screens.EventScreen;
import com.kremski.alert24.screens.events_map.EventsMapScreen;
import com.kremski.alert24.serwer.service.DefaultWorkerResultHandler;
import com.kremski.alert24.serwer.service.WorkerResult;
import com.kremski.alert24.serwer.service.WorkerRetrivingAddress;
import com.kremski.alert24.views.image.ImageContainer;

public class EventInfoWindow extends LinearLayout implements ImageContainer{

	private final static int LAYOUT_ID = R.layout.event_info_window;

	private Marker eventMarkerOnMap;
	private EventInfoWindowsAdapter eventInfoWindowsAdapter;
	private TextView eventCategoryLabel;
	private ImageView eventCategoryPhotoLabel;
	private TextView eventNameLabel;
	private TextView eventAddressLabel;
	private EventsMapScreen alert24Screen;
	private ProgressBar eventPhotoLoadingBar;
	private Event displayedEvent;

	public static final String EVEN_PHOTO_CATEGORY_IMAGE_KEY = "EVEN_PHOTO_CATEGORY_IMAGE_KEY ";

	public EventInfoWindow(EventsMapScreen alert24screen, EventInfoWindowsAdapter eventInfoWindowsAdapter ) {
		super(alert24screen);
		this.alert24Screen = alert24screen;
		this.eventInfoWindowsAdapter = eventInfoWindowsAdapter ;
		initialize();
	}

	private void initialize() {
		inflateLayout();
		eventNameLabel = (TextView)findViewById(R.id.name);
		eventAddressLabel = (TextView)findViewById(R.id.address);
		eventCategoryPhotoLabel = (ImageView)findViewById(R.id.category_photo);
		eventPhotoLoadingBar = (ProgressBar)findViewById(R.id.photo_loading_bar);
		eventCategoryLabel = (TextView)findViewById(R.id.category);
	}

	private void inflateLayout() {
		String service = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(service);	
		inflater.inflate(LAYOUT_ID, this, true);
	}

	public void setEventDetails(Event event, Marker eventMarkerOnMap) {
		this.eventMarkerOnMap = eventMarkerOnMap;
		displayedEvent = event;
		eventNameLabel.setText(event.getName());
		eventCategoryLabel.setText(event.getCategory().getName());
		eventAddressLabel.setText(event.getLatitude() + ", " + event.getLongitude());
	}

	private class AddressRetrivedResultHandler extends DefaultWorkerResultHandler{

		public AddressRetrivedResultHandler(Alert24Screen alert24Screen) {
			super(alert24Screen);
		}

		@Override
		public void onSuccess(WorkerResult workerResult, Intent intentWithResult) {
			super.onSuccess(workerResult, intentWithResult);
			LatLng sourcePoint = intentWithResult.getParcelableExtra(LatLng.class.toString());
			LatLng displayedEventLocation = new LatLng(displayedEvent.getLatitude(), displayedEvent.getLongitude());
			if (sourcePoint.equals(displayedEventLocation)) {
				Address address = intentWithResult.getParcelableExtra(Address.class.toString());
				eventAddressLabel.setText(address.toString());
			}
		}

	}

	@Override
	public ImageView getImageView(String imageKey) {
		if (imageKey.equals(EVEN_PHOTO_CATEGORY_IMAGE_KEY)) {
			return eventCategoryPhotoLabel;
		} else {
			throw new IllegalArgumentException("Incorrect imagekey");
		}
	}

	@Override
	public void showProgressOfLoadingImage() {
		eventCategoryPhotoLabel.setVisibility(View.GONE);
	}

	@Override
	public void showLoadedImage(String imageKey, Bitmap bitmap) {
		if (imageKey.equals(EVEN_PHOTO_CATEGORY_IMAGE_KEY)) {
			if (eventInfoWindowsAdapter.isImageLoading()) {
				eventCategoryPhotoLabel.setVisibility(View.VISIBLE);
				eventCategoryPhotoLabel.setImageBitmap(bitmap);
				eventMarkerOnMap.hideInfoWindow();
				eventMarkerOnMap.showInfoWindow();
				eventInfoWindowsAdapter.setImageLoading(false);
			}
		} else {
			throw new IllegalArgumentException("Incorrect imagekey");
		}
	}
}
