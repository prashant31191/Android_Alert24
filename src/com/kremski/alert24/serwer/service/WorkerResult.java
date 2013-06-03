package com.kremski.alert24.serwer.service;

import android.os.Parcel;
import android.os.Parcelable;

public class WorkerResult implements Parcelable {

	private String result;
	private Exception exception;
	private String messageToShowUponError;
	
	public static final Parcelable.Creator<WorkerResult> CREATOR = new Parcelable.Creator<WorkerResult>() {
		public WorkerResult createFromParcel(Parcel in) {
			return new WorkerResult(in);
		}

		public WorkerResult[] newArray(int size) {
			return new WorkerResult[size];
		}
	};
	
	private WorkerResult(Parcel in) {
		result = in.readString();
		exception = (Exception) in.readSerializable();
		messageToShowUponError = in.readString();
	}
	
	public WorkerResult(String result) {
		this.result = result;
	}
	
	public WorkerResult(Exception e, String messageToShowUponError) {
		this.exception = e;
		this.messageToShowUponError = messageToShowUponError;
	}
	
	public boolean isExceptionThrown() {
		return exception != null;
	}
	
	public String getResult() {
		return result;
	}
	
	public Exception getException() {
		return exception;
	}
	
	public String getErrorMessageToShowUponError() {
		return messageToShowUponError;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeString(result);
		parcel.writeSerializable(exception);
		parcel.writeString(messageToShowUponError);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}