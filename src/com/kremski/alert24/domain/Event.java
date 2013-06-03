package com.kremski.alert24.domain;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.Lists;
import com.kremski.alert24.database.TableRecord;

public class Event implements TableRecord, Parcelable{

	private String name;
	private String description;
	private Bitmap photoBitmap;
	private String deviceKey;
	private double longitude;
	private double latitude;
	private final List<String> photoUrls;
	private final String serverId;
	private final String encodedId;
	private long databaseId;
	private String photos;
	private boolean sent = false;
	private EventCategory eventCategory;
	private String address;

	public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
		public Event createFromParcel(Parcel in) {
			return new Event(in);
		}

		public Event[] newArray(int size) {
			return new Event[size];
		}
	};

	public static class EventBuilder {

		private String name;
		private String description;
		private Bitmap photoBitmap;
		private String deviceKey;
		private double latitude;
		private double longitude;
		private List<String> photoUrls = Lists.newLinkedList();
		private String serverId;
		private String encodedId;
		private String photoPath;
		private long databaseId;
		private EventCategory eventCategory;
		private boolean sent;
		private String address;
		
		public EventBuilder address(String address) {
			this.address = address;
			return this;
		}

		public EventBuilder sent(boolean sent) {
			this.sent = sent;
			return this;
		}

		public EventBuilder photoPath(String photoPath) {
			this.photoPath = photoPath;
			return this;
		}

		public EventBuilder photoUrls(List<String> photoUrls) {
			this.photoUrls = photoUrls;
			return this;
		}

		public EventBuilder latitude(double latitude) {
			this.latitude = latitude;
			return this;
		}

		public EventBuilder longitude(double longitude) {
			this.longitude = longitude;
			return this;
		}

		public EventBuilder deviceKey(String deviceKey) {
			this.deviceKey = deviceKey;
			return this;
		}

		public EventBuilder name(String name) {
			this.name = name;
			return this;
		}

		public EventBuilder descritpion(String description) {
			this.description = description;
			return this;
		}

		public EventBuilder photoBitmap(Bitmap photoBitmap) {
			this.photoBitmap = photoBitmap;
			return this;
		}

		public EventBuilder serverId(String serverId) {
			this.serverId = serverId;
			return this;
		}

		public EventBuilder encodedId(String encodedId) {
			this.encodedId = encodedId;
			return this;
		}

		public EventBuilder databaseId(long databaseId) {
			this.databaseId = databaseId;
			return this;
		}

		public Event build() {
			return new Event(name, sent, description, address, eventCategory, photoBitmap, photoPath, deviceKey, latitude, longitude, photoUrls, serverId, encodedId, databaseId);
		}

		public EventBuilder category(EventCategory eventCategory) {
			this.eventCategory = eventCategory;
			return this;
		}
	}

	private Event(String name, boolean sent, String description, String address, EventCategory eventCategory, Bitmap photoBitmap, String photoPath, String deviceId, double latitude, double longitude, List<String> photoUrl, String id, String encodedId, long databaseId) {
		this.name = name;
		this.sent = sent;
		this.description = description;
		this.address = address;
		this.eventCategory = eventCategory;
		this.photoBitmap = photoBitmap;
		this.deviceKey = deviceId;
		this.latitude = latitude;
		this.longitude = longitude;
		this.photoUrls = Lists.newLinkedList(photoUrl);
		this.serverId = id;
		this.photos = photoPath;
		this.encodedId = encodedId;
		this.databaseId = databaseId;
	}

	private Event(Parcel parcel) {
		name = parcel.readString();
		sent = parcel.readByte() == 1;  
		description = parcel.readString();
		address = parcel.readString();
		eventCategory = parcel.readParcelable(EventCategory.class.getClassLoader());
		deviceKey = parcel.readString();
		longitude = parcel.readDouble();
		latitude = parcel.readDouble();
		photoUrls = Lists.newLinkedList();
		parcel.readList(photoUrls, List.class.getClassLoader());
		serverId = parcel.readString();
		encodedId = parcel.readString();
		databaseId = parcel.readLong();
		photos = parcel.readString();
	}	

	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public Bitmap getPhotoBitmap() {
		return photoBitmap;
	}

	public void setPhotoBitmap(Bitmap bitmap) {
		this.photoBitmap = bitmap;
	}

	public String getDeviceKey() {
		return deviceKey;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public List<String> getPhotoUrls() {
		return photoUrls;
	}

	public String getServerId() {
		return serverId;
	}

	public String getEncodedId() {
		return encodedId;
	}
	
	public void setEventCategory(EventCategory eventCategory) {
		this.eventCategory = eventCategory;
	}
	
	public EventCategory getCategory() {
		return eventCategory;
	}

	public long getDatabaseId() {
		return databaseId;
	}

	public String getPhotoPath() {
		return photos;
	}

	public void setDeviceKey(String key) {
		this.deviceKey = key;
	}

	public void setPhotoPath(String photoPath) {
		this.photos = photoPath;
	}

	public void setDatabaseId(long databaseId) {
		this.databaseId = databaseId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeString(name);
		parcel.writeByte((byte) (sent ? 1 : 0));
		parcel.writeString(description);
		parcel.writeString(address);
		parcel.writeParcelable(eventCategory, PARCELABLE_WRITE_RETURN_VALUE);
		parcel.writeString(deviceKey);
		parcel.writeDouble(longitude);
		parcel.writeDouble(latitude);
		parcel.writeList(photoUrls);
		parcel.writeString(serverId);
		parcel.writeString(encodedId);
		parcel.writeLong(databaseId);
		parcel.writeString(photos);
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}
}
