package com.kremski.alert24.screens.event_screens;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

public interface AddressContainer {
	public void setAddress(Address address, LatLng latLngSource);
	public boolean isAdded();
}
