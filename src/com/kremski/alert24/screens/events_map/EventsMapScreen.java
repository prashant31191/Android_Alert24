package com.kremski.alert24.screens.events_map;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.Lists;
import com.kremski.alert24.R;
import com.kremski.alert24.adapters.EventInfoWindowsAdapter;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.domain.Event.EventBuilder;
import com.kremski.alert24.domain.PointOfInterest;
import com.kremski.alert24.notifications.NotificationFactory;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.screens.EventsDownloadedHandler;
import com.kremski.alert24.screens.event_screens.EventScreen;
import com.kremski.alert24.serwer.service.WorkerDownloadingEventCategories;
import com.kremski.alert24.serwer.service.WorkerDownloadingEvents;
import com.kremski.alert24.serwer.service.WorkerRegisteringDevice;
import com.kremski.alert24.tasks.TaskLoadingPointsOfInterest;
import com.kremski.alert24.tasks.TaskLoadingPointsOfInterest.OnPointsLoadedListener;
import com.kremski.alert24.views.ActionBarOption;

public class EventsMapScreen extends Alert24Screen implements OnMapClickListener, OnMyLocationChangeListener, OnPointsLoadedListener{

	private SupportMapFragment mapFragment;
	private LatLng currentLocation = new LatLng(50.061382, 19.937439);
	private GoogleMap googleMap;
	private Circle userPositionCircle;
	private List<Marker> displayedMarkers = Lists.newLinkedList();
	EventBuilder eventBuilder = new EventBuilder();
	private EventInfoWindowsAdapter eventsAdapter;
	private OnGoogleMapDrawer onGoogleMapDrawer = new OnGoogleMapDrawer();

	public static final int TAKE_PHOTO_REQUEST_CODE = 0;
	private final static String PREFERENCES_EVENT_CATEGORIES_STATUS_KEY = "event_categories_status_key";
	private Marker lastClickedMarker;

	@Override
	protected int getContentViewId() {
		return R.layout.events_map_screen;
	}

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setUpMap();

		registerWorkerResultHandler(WorkerRegisteringDevice.class.toString(), 
				new DeviceRegisteredHandler(this, optionsManager));
		registerWorkerResultHandler(WorkerDownloadingEventCategories.class.toString(), 
				new EventCategoriesDownloadedHandler(this, optionsManager));

	}

	private void setUpMap() {
		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_of_events); 
		googleMap = mapFragment.getMap();
		googleMap.setOnMapClickListener(this);
		googleMap.setMapType(optionsManager.getMapTypeKey());
		eventsAdapter = new EventInfoWindowsAdapter(this, eventImageManager, eventCategoryImageManager);
		googleMap.setInfoWindowAdapter(eventsAdapter);
		googleMap.setMyLocationEnabled(true);
		googleMap.setOnMyLocationChangeListener(this);
		googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker marker) {
				Event eventUnderMarker = eventsAdapter.getEventUnderMarker(marker);
				Intent intent = new Intent(EventsMapScreen.this, EventScreen.class);
				intent.putExtra(Event.class.toString(), eventUnderMarker);
				startActivity(intent);
			}
		});
		setActionBarTitle(R.string.app_name);
	}

	@Override
	protected void onStart() {
		super.onStart();
		changeMapType(optionsManager.getMapTypeName());
		List<Event> lastDownloadedEvents = getDownloadedEvents();
		eventsAdapter.setEvents(lastDownloadedEvents);
		onGoogleMapDrawer.drawEventsOnMap(lastDownloadedEvents);
	}

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();

		if (!optionsManager.isEventCategoriesDownloaded()) {
			WorkerDownloadingEventCategories worker = new WorkerDownloadingEventCategories(eventCategoryImageManager, this, serverUtilities, eventCategoriesTableHelper);
			executeWorker(worker);
		} 

		if (optionsManager.getDeviceKey().equals("")) {
			WorkerRegisteringDevice workerRegisteringDevice = new WorkerRegisteringDevice(this, serverUtilities);
			executeWorker(workerRegisteringDevice);
		} else {
			startDownloadingEvents();
		}

		new TaskLoadingPointsOfInterest(this, pointsOfInterestTable, this).execute(new Void[]{});
	}

	public void startDownloadingEvents() {
		if (!getAlert24Service().isDownloadingEvents()) {
			List<PointOfInterest> points = pointsOfInterestTable.getAll();
			List<LatLng> coords = Lists.newLinkedList();
			for (PointOfInterest p : points) {
				coords.add(new LatLng(p.getLatitude(), p.getLongitude()));
			}
			getAlert24Service().startDownloadingEvents(this, coords, serverUtilities, 
					optionsManager.getRefreshRadius(),  optionsManager.getRefreshInterval());
		}
	}

	public void onInfoWindowVisibilityChanged(Event visibleEvent, boolean isVisible) {
	}

	private void changeMapType(String mapType) {
		int currentMapType = optionsManager.getMapTypeKey();
		if (googleMap.getMapType() != currentMapType) {
			googleMap.setMapType(currentMapType);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void addEvent(Event event) {
		super.addEvent(event);
		eventsAdapter.addEvent(event);
		onGoogleMapDrawer.drawEventMarkerOnMap(event);
		ArrayList<Event> displayedEvents = Lists.newArrayList(eventsAdapter.getEvents()); 
		fireNotification(notificationFactory.createNotificationAboutNewEventReceivedFromServer(event, displayedEvents));
	}

	private class OnGoogleMapDrawer {

		private void drawEventsOnMap(final List<Event> events) {
			removeDisplayedMarkersFromMap();
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					for(Event e : events) {
						drawEventMarkerOnMap(e);
					}
				}
			});
		}

		private void removeDisplayedMarkersFromMap() {
			for (Marker marker : displayedMarkers) {
				marker.remove();
			}
			displayedMarkers.clear();
		}

		private void drawEventMarkerOnMap(final Event event) {
			LatLng position = new LatLng(event.getLatitude(), event.getLongitude());
			Marker eventMarker = googleMap.addMarker(new MarkerOptions().title(event.getServerId()).position(position));
			displayedMarkers.add(eventMarker);
		}

		private void drawUserPositionCircle(LatLng userLocation) {
			if (userLocation != null) {

				if (userPositionCircle != null) {
					userPositionCircle.remove();
				}

				userPositionCircle = drawCircleOnMap(userLocation, optionsManager.getCurrentLocationColor());
			}
		}

		private Circle drawCircleOnMap(LatLng circleLocation, int color) {
			int refreshRadius = optionsManager.getRefreshRadius();
			CircleOptions currentCircleOnMapOptions = new CircleOptions().center(circleLocation).fillColor(color).strokeWidth(0)
					.radius(refreshRadius); 	
			return googleMap.addCircle(currentCircleOnMapOptions);
		}
	}

	@Override
	protected void handleReceivedNotification(String notificationName,
			Intent intent) {
		if (NotificationFactory.NOTIFICATION_ABOUT_NEW_EVENT_RECEIVED_FROM_SERVER.equals(notificationName)) {

			Event eventWhichWasReceived = intent.getParcelableExtra(Event.class.toString());
			ArrayList<Event> previouslyDisplayedEvents = intent.getParcelableArrayListExtra(List.class.toString());
			previouslyDisplayedEvents.add(eventWhichWasReceived);
			eventsAdapter.setEvents(previouslyDisplayedEvents);

			LatLng eventLocation = new LatLng(eventWhichWasReceived.getLatitude(), eventWhichWasReceived.getLongitude());
			googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(eventLocation, 13, 0, 0)));
		}
	}
	
	@Override
	public void onEventsDownloaded(List<Event> downloadedEvents) {
		super.onEventsDownloaded(downloadedEvents);
		eventsAdapter.setEvents(downloadedEvents);
		onGoogleMapDrawer.drawEventsOnMap(downloadedEvents);
	}
	
	@Override
	public void onMyLocationChange(Location location) {
		LatLng latLngLocation = new LatLng(location.getLatitude(), location.getLongitude());
		onGoogleMapDrawer.drawUserPositionCircle(latLngLocation);
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLngLocation, 13, 0, 0)));
	}

	@Override
	public void onPointsLoaded(List<PointOfInterest> points) {
		for (PointOfInterest point : points) {
			onGoogleMapDrawer.drawCircleOnMap(point.getLatLngLocation(), point.getCircleColor());
		}
	}

	@Override
	public void onMapClick(LatLng point) {
		// TODO Auto-generated method stub
		
	}

}
