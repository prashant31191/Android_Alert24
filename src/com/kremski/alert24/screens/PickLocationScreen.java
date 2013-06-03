package com.kremski.alert24.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.kremski.alert24.R;
import com.kremski.alert24.serwer.service.WorkerResult;

public class PickLocationScreen extends Alert24Screen implements OnMapClickListener {

	private SupportMapFragment mapFragment;
	private GoogleMap googleMap;


	@Override
	protected int getContentViewId() {
		return R.layout.pick_location_screen;
	}

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setUpMap();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Toast.makeText(this, "Wybierz lokalizację", Toast.LENGTH_LONG).show();
	}

	private void setUpMap() {
		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map); 
		googleMap = mapFragment.getMap();
		googleMap.setOnMapClickListener(this);
		LatLng lastKnownLocation = getLastKnownLocation();
		if (lastKnownLocation != null) {
			CameraPosition cameraPosition = new CameraPosition(getLastKnownLocation(), 13, 0, 0);
			googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		}
	}

	@Override
	public void onMapClick(LatLng point) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra(LatLng.class.toString(), point);
		setResult(RESULT_OK,returnIntent);     
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void handleReceivedNotification(String notificationName,
			Intent intent) {
	}
}
