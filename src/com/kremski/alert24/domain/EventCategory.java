package com.kremski.alert24.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.kremski.alert24.database.TableRecord;

public class EventCategory implements TableRecord, Parcelable{

	private String name;
	private long databaseId;
	private long databaseParentId = -1;
	private String serverId;
	private String iconUrl;

	public static final Parcelable.Creator<EventCategory> CREATOR = new Parcelable.Creator<EventCategory>() {
		public EventCategory createFromParcel(Parcel in) {
			return new EventCategory(in);
		}

		public EventCategory[] newArray(int size) {
			return new EventCategory[size];
		}
	};

	public EventCategory(String name, String serverId) {
		this.name = name;
		this.serverId = serverId;
	}

	public EventCategory(String name, String iconUrl, long databaseId, long databaseParentId,
			String serverId) {
		super();
		this.name = name;
		this.iconUrl = iconUrl;
		this.databaseId = databaseId;
		this.databaseParentId = databaseParentId;
		this.serverId = serverId;
	}

	public EventCategory(Parcel in) {
		name = in.readString();
		iconUrl = in.readString();
		databaseId = in.readLong();
		databaseParentId = in.readLong();
		serverId = in.readString();
	}

	public String getName() {
		return name;
	}
	
	public String getIconUrl() {
		return iconUrl;
	}

	public long getDatabaseId() {
		return databaseId;
	}

	public long getDatabaseParentId() {
		return databaseParentId;
	}

	public String getServerId() {
		return serverId;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof EventCategory)) {
			return false;
		}
		EventCategory category = (EventCategory)o;
		return databaseId == category.databaseId;
	}

	public int hashCode() {
		return (int) databaseId;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeString(name);
		parcel.writeString(iconUrl);
		parcel.writeLong(databaseId);
		parcel.writeLong(databaseParentId);
		parcel.writeString(serverId);
	};

	@Override
	public String toString() {
		return "EventCategory [name=" + name + ", databaseId=" + databaseId
				+ ", databaseParentId=" + databaseParentId + ", serverId="
				+ serverId + ", iconUrl=" + iconUrl + "]";
	}


}
