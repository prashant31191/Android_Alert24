package com.kremski.alert24.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.kremski.alert24.R;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.serwer.service.DefaultWorkerResultHandler;
import com.kremski.alert24.serwer.service.WorkerResult;
import com.kremski.alert24.serwer.service.WorkerRetrivingAddress;
import com.kremski.alert24.views.image.ImageContainer;
import com.kremski.alert24.views.image.ImageManager;

public class EventInformationsFragment extends Fragment implements ImageContainer{

	private TextView eventNameLabel;
	private TextView eventAddressLabel;
	private TextView eventDescriptionLabel;
	private TextView eventCategoryLabel;
	private ImageView eventCategoryImageLabel;
	private ProgressBar eventCategoryImageLoadingBar;
	private Alert24Screen alert24Screen;
	private Event displayedEvent;
	
	private static final String EVENT_CATEGORY_IMAGE_KEY = "EVENT_CATEGORY_IMAGE_KEY";
	
	public static EventInformationsFragment newInstance(Event event, Alert24Screen activity) {
		EventInformationsFragment instance = new EventInformationsFragment();
		instance.alert24Screen = activity;
		instance.displayedEvent = event;
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout =  inflater.inflate(R.layout.event_informations_fragment, null);
		eventNameLabel = (TextView)layout.findViewById(R.id.name);
		eventDescriptionLabel = (TextView)layout.findViewById(R.id.description);
		eventAddressLabel = (TextView)layout.findViewById(R.id.address);
		eventCategoryLabel = (TextView)layout.findViewById(R.id.category);
		eventCategoryImageLabel = (ImageView)layout.findViewById(R.id.category_photo);
		eventCategoryImageLoadingBar = (ProgressBar)layout.findViewById(R.id.category_photo_loading_bar);
		
		eventNameLabel.setText(displayedEvent.getName());
		eventCategoryLabel.setText(displayedEvent.getCategory().getName());
		eventDescriptionLabel.setText(displayedEvent.getDescription());
		return layout;
	}


	@Override
	public void onResume() {
		super.onResume();
		alert24Screen.getEventCategoryImageManager().displayImage(EVENT_CATEGORY_IMAGE_KEY, displayedEvent.getCategory(), 
				EventInformationsFragment.this);
		
		alert24Screen.registerWorkerResultHandler(WorkerRetrivingAddress.class.toString(), new DefaultWorkerResultHandler(alert24Screen) {

			@Override
			public void onSuccess(WorkerResult workerResult, final Intent intentWithResult) {
				alert24Screen.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Address address = intentWithResult.getParcelableExtra(Address.class.toString());
						LatLng latLngSource = intentWithResult.getParcelableExtra(LatLng.class.toString());
						if (address != null) {
							String addressText = String.format("%s, %s, %s",
									address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getLocality(), address.getCountryName());
							eventAddressLabel.setText(addressText);
						} else {
							eventAddressLabel.setText(String.format("%s, %s", latLngSource.latitude, latLngSource.longitude));
						}
					}
				});
			}
		});
		alert24Screen.executeWorker(new WorkerRetrivingAddress(alert24Screen, new LatLng(displayedEvent.getLatitude(), displayedEvent.getLongitude())));
	}

	@Override
	public void showProgressOfLoadingImage() {
		eventCategoryImageLabel.setVisibility(View.VISIBLE);
		eventCategoryImageLabel.setVisibility(View.GONE);
	}

	@Override
	public void showLoadedImage(String imageKey, Bitmap bitmap) {
		if (imageKey.equals(EVENT_CATEGORY_IMAGE_KEY)) {
			eventCategoryImageLoadingBar.setVisibility(View.GONE);
			eventCategoryImageLabel.setVisibility(View.VISIBLE);
			eventCategoryImageLabel.setImageBitmap(bitmap);
		}
	}

	@Override
	public ImageView getImageView(String imageKey) {
		if (imageKey.equals(EVENT_CATEGORY_IMAGE_KEY)) {
			return eventCategoryImageLabel;
		}
		return null;
	}
}

