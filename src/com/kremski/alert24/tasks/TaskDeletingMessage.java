package com.kremski.alert24.tasks;

import android.util.Log;

import com.kremski.alert24.database.SystemNotificationsTable;
import com.kremski.alert24.domain.SystemNotification;
import com.kremski.alert24.screens.Alert24Screen;

public class TaskDeletingMessage extends DefaultTask<Void, Void, Void>{

	private SystemNotificationsTable messagesTable;
	private OnMessageRemovedListener onMessagesRemovedListener;
	private SystemNotification messageToRemove;

	public TaskDeletingMessage(Alert24Screen activity, SystemNotification messageToRemove, SystemNotificationsTable messagesTable, OnMessageRemovedListener onMessagesRemovedListener) {
		super(activity);
		this.messagesTable = messagesTable;
		this.onMessagesRemovedListener = onMessagesRemovedListener;
		this.messageToRemove = messageToRemove;
	}

	@Override
	protected Void execute() {
		Log.i(LOG_TAG, "Removing message from database");
		messagesTable.delete(messageToRemove);
		Log.i(LOG_TAG, "SystemNotification have been delete");
		return null; 
	}

	@Override
	protected void handleResult(Void result) {
		onMessagesRemovedListener.onMessageRemoved(messageToRemove);

	}

	public interface OnMessageRemovedListener {
		public void onMessageRemoved(SystemNotification removedMessage);
	}
}
