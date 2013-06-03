package com.kremski.alert24.tasks;

import org.json.JSONException;

import android.util.Log;

import com.kremski.alert24.database.SystemNotificationsTable;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.serwer.command.ServerCommand;

public class TaskExecutingServerCommand extends DefaultTask<Void, Void, Void>{

	private ServerCommand serverCommand;

	public TaskExecutingServerCommand(Alert24Screen activity, ServerCommand serverCommand) {
		super(activity);
		this.serverCommand = serverCommand;
	}

	@Override
	protected Void execute() {
		try {
			serverCommand.execute();
		} catch (JSONException e) {
			Log.i(LOG_TAG, e.getLocalizedMessage());
		}
		return null; 
	}

	@Override
	protected void handleResult(Void result) {
	}
}
