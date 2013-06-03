package com.kremski.alert24.tasks;

import java.util.List;

import android.util.Log;

import com.kremski.alert24.database.SystemNotificationsTable;
import com.kremski.alert24.domain.SystemNotification;
import com.kremski.alert24.screens.Alert24Screen;

public class TaskLoadingMessages extends DefaultTask<Void, Void, List<SystemNotification>>{

	private SystemNotificationsTable messagesTable;
	private OnMessagesLoadedListener messagesLoadedListener;

	public TaskLoadingMessages(Alert24Screen activity, SystemNotificationsTable messagesTable, OnMessagesLoadedListener messagesLoadedListener) {
		super(activity);
		this.messagesTable = messagesTable;
		this.messagesLoadedListener = messagesLoadedListener;
	}

	@Override
	protected List<SystemNotification> execute() {
		Log.i(LOG_TAG, "Selecting messages from database");
		List<SystemNotification> messages = messagesTable.getAll();
		Log.i(LOG_TAG, "Messages of have been selected : " + messages.toString());
		return messages; 
	}
	
	@Override
	protected void handleResult(List<SystemNotification> result) {
		messagesLoadedListener.onMessagesLoaded(result);
	}

	public interface OnMessagesLoadedListener {
		public void onMessagesLoaded(List<SystemNotification> messages);
	}
}
