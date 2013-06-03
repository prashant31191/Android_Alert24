package com.kremski.alert24.tasks;

import android.util.Log;

import com.kremski.alert24.database.SystemNotificationsTable;
import com.kremski.alert24.screens.Alert24Screen;

public class TaskDeletingAllMessages extends DefaultTask<Void, Void, Void>{

	private SystemNotificationsTable messagesTable;
	private OnAllMessagesRemovedListener onMessagesRemovedListener;

	public TaskDeletingAllMessages(Alert24Screen activity, SystemNotificationsTable messagesTable, OnAllMessagesRemovedListener onMessagesRemovedListener) {
		super(activity);
		this.messagesTable = messagesTable;
		this.onMessagesRemovedListener = onMessagesRemovedListener;
	}

	@Override
	protected Void execute() {
		Log.i(LOG_TAG, "Removing messages from database");
		messagesTable.deleteAll();
		Log.i(LOG_TAG, "Messagess have been delete");
		return null; 
	}

	@Override
	protected void handleResult(Void result) {
		onMessagesRemovedListener.onAllMessagesRemoved();
	}

	public interface OnAllMessagesRemovedListener {
		public void onAllMessagesRemoved();
	}
}
