package com.kremski.alert24.screens;

import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.Lists;
import com.kremski.alert24.CommonUtilities;
import com.kremski.alert24.R;
import com.kremski.alert24.domain.PointOfInterest;
import com.kremski.alert24.views.ActionBarOption;

public class PointOfInterestPreviewScreen extends Alert24Screen {

	private SupportMapFragment mapFragment;
	private GoogleMap googleMap;
	private CircleOptions currentCircleOnMapOptions;
	private List<PointOfInterest> allPoints;
	private int chosenPointIndex;

	public static final int TAKE_PHOTO_REQUEST_CODE = 0;
	public static final String MAP_TYPE_KEY = "MAP_TYPE";
	public static final String CHOSEN_POINT_INDEX = "CHOSEN_NOTIFICATION_INDEX";

	@Override
	protected void prepareActionBarOptions(
			LinkedList<ActionBarOption> actionbarChoosableOptions) {
		super.prepareActionBarOptions(actionbarChoosableOptions);
	}

	protected View getActionBarView() {
		View actionBarView = LayoutInflater.from(this).inflate(R.layout.point_preview_actionbar, null);
		View nextPointOption = actionBarView.findViewById(R.id.next_point);
		nextPointOption.setOnClickListener(createNextPointOptionListener());
		View previousPointOption = actionBarView.findViewById(R.id.previous_point);
		previousPointOption.setOnClickListener(createPreviousPointOptionListener());
		return actionBarView;
	}

	private View.OnClickListener createNextPointOptionListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (chosenPointIndex == allPoints.size()-1) {
					chosenPointIndex = 0;
				} else {
					chosenPointIndex++;
				}
				moveCameraToPoint(allPoints.get(chosenPointIndex));
			}
		};
	}

	private void moveCameraToPoint(PointOfInterest point) {
		googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(
				new CameraPosition(point.getLatLngLocation(), 13, 0, 0)));
	}

	private View.OnClickListener createPreviousPointOptionListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (chosenPointIndex == 0) {
					chosenPointIndex = allPoints.size()-1;
				} else {
					chosenPointIndex--;
				}
				moveCameraToPoint(allPoints.get(chosenPointIndex));
			}
		};

	}

	@Override
	protected int getContentViewId() {
		return R.layout.point_preview_screen;
	}

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		Intent intent = getIntent();
		if (intent != null) {
			PointOfInterest clickedPoint = (PointOfInterest)intent.getParcelableExtra(PointOfInterest.class.toString());
			chosenPointIndex = intent.getIntExtra(CHOSEN_POINT_INDEX, 0);
			allPoints = intent.getParcelableArrayListExtra(List.class.toString());
			setUpMap(clickedPoint, allPoints);
		}

	}

	private void setUpMap(PointOfInterest pointToShow, List<PointOfInterest> allPoints) {
		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_of_points); 
		googleMap = mapFragment.getMap();
		moveCameraToPoint(pointToShow);
		googleMap.setMapType(optionsManager.getMapTypeKey());
		drawPointsOnMap(allPoints);
		setActionBarTitle(R.string.point_of_interest_screen_title);
	}

	@Override
	protected void handleReceivedNotification(String notificationName,
			Intent intent) {
		// TODO Auto-generated method stub

	}


	// TODO the same code in EventsMapScreen	
	private void drawPointsOnMap(List<PointOfInterest> points) {
		int refreshRadius = optionsManager.getRefreshRadius();

		for(PointOfInterest p : points) {
			currentCircleOnMapOptions = new CircleOptions().center(p.getLatLngLocation())
					.fillColor(p.getCircleColor()).strokeWidth(0)
					.radius(refreshRadius); 	
			googleMap.addCircle(currentCircleOnMapOptions);
			googleMap.addMarker(new MarkerOptions().title(p.getName() + "\n" + p.getAddress()).position(p.getLatLngLocation()));
		}
	}
}
