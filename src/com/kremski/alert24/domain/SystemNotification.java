package com.kremski.alert24.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.kremski.alert24.database.TableRecord;

public class SystemNotification implements TableRecord, Parcelable{

	private String title;
	private String content;
	private String author;
	private long databaseId;
	private boolean isRead = false;
	private long sendDate;

	public static final Parcelable.Creator<SystemNotification> CREATOR = new Parcelable.Creator<SystemNotification>() {
		public SystemNotification createFromParcel(Parcel in) {
			return new SystemNotification(in);
		}

		public SystemNotification[] newArray(int size) {
			return new SystemNotification[size];
		}
	};
	
	public SystemNotification(String title, String content, String author, boolean isRead, long sendDate) {
		super();
		this.content = content;
		this.author = author;
		this.isRead = isRead;
		this.sendDate = sendDate;
		this.title = title;
	}

	public SystemNotification(long databaseId, String title, String content, String author, boolean isRead, long sendDate) {
		this(title, content, author, isRead, sendDate);
		this.databaseId = databaseId;
	}

	public SystemNotification(Parcel in) {
		databaseId = in.readLong();
		title = in.readString();
		content = in.readString();
		author = in.readString();
		isRead = in.readByte() == 1;
		sendDate = in.readLong();
	}	
	
	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeLong(databaseId);
		parcel.writeString(title);
		parcel.writeString(content);
		parcel.writeString(author);
		parcel.writeByte((byte) (isRead ? 1 : 0));
		parcel.writeLong(sendDate);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String getContent() {
		return content;
	}
	
	public long getSentDate() {
		return sendDate;
	}

	@Override
	public long getDatabaseId() {
		return databaseId;
	};	
	
	public boolean isRead() {
		return isRead;
	};	
	
	public String getAuthor() {
		return author;
	}

	public void setRead(boolean read) {
		this.isRead = read;
	}

	@Override
	public String toString() {
		return "SystemNotification [title=" + title + ", content=" + content + ", author="
				+ author + ", databaseId=" + databaseId + ", isRead=" + isRead
				+ ", sendDate=" + sendDate + "]";
	}

	public String getTitle() {
		return title;
	}
	
}
