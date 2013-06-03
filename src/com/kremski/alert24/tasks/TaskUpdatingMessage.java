package com.kremski.alert24.tasks;

import android.util.Log;

import com.kremski.alert24.database.SystemNotificationsTable;
import com.kremski.alert24.domain.SystemNotification;
import com.kremski.alert24.screens.Alert24Screen;

public class TaskUpdatingMessage extends DefaultTask<Void, Void, SystemNotification>{

	private SystemNotificationsTable messagesTable;
	private OnMessageUpdatedListener messageUpdatedListener;
	private SystemNotification messageToUpdate;

	public TaskUpdatingMessage(Alert24Screen activity, SystemNotification messageToUpdate, SystemNotificationsTable messagesTable, OnMessageUpdatedListener messageUpdatedListener) {
		super(activity);
		this.messagesTable = messagesTable;
		this.messageUpdatedListener = messageUpdatedListener;
		this.messageToUpdate = messageToUpdate;
	}

	@Override
	protected SystemNotification execute() {
		Log.i(LOG_TAG, "Updating message");
		messagesTable.update(messageToUpdate);
		Log.i(LOG_TAG, "SystemNotification have been updated");
		return messageToUpdate;
	}

	@Override
	protected void handleResult(SystemNotification updatedMessage) {
		messageUpdatedListener.onMessageUpdated(updatedMessage);
	}

	public interface OnMessageUpdatedListener {
		public void onMessageUpdated(SystemNotification updatedMessage);
	}
}
