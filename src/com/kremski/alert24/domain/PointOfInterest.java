package com.kremski.alert24.domain;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.kremski.alert24.database.TableRecord;

public class PointOfInterest implements TableRecord, Parcelable{

	private String name;
	private LatLng latLngLocation;
	private String address;
	private long databaseId;
	private int circleColor;

	public static final Parcelable.Creator<PointOfInterest> CREATOR = new Parcelable.Creator<PointOfInterest>() {
		public PointOfInterest createFromParcel(Parcel in) {
			return new PointOfInterest(in);
		}

		public PointOfInterest[] newArray(int size) {
			return new PointOfInterest[size];
		}
	};

	public PointOfInterest(long databaseId, String name, LatLng latLngLocation, String address, int circleColor) {
		super();
		this.databaseId = databaseId;
		this.name = name;
		this.latLngLocation = latLngLocation;
		this.address = address;
		this.circleColor = circleColor;
	}

	public PointOfInterest(Parcel in) {
		name = in.readString();
		databaseId = in.readLong();
		double latitude = in.readDouble();
		double longitude = in.readDouble();
		latLngLocation = new LatLng(latitude, longitude);
		address = in.readString();
		circleColor = in.readInt();
	}	
	
	public String getAddress() {
		return address;
	}
	
	public int getCircleColor() {
		return circleColor;
	}

	public double getLatitude() {
		return latLngLocation.latitude;
	}

	public double getLongitude() {
		return latLngLocation.longitude;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LatLng getLatLngLocation() {
		return latLngLocation;
	}

	public void setLatLngLocation(LatLng latLngLocation) {
		this.latLngLocation = latLngLocation;
	}

	@Override
	public long getDatabaseId() {
		return databaseId;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeString(name);
		parcel.writeLong(databaseId);
		parcel.writeDouble(latLngLocation.latitude);
		parcel.writeDouble(latLngLocation.longitude);
		parcel.writeString(address);
		parcel.writeInt(circleColor);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setDatabaseId(long databaseId) {
		this.databaseId = databaseId;
	};	
}
