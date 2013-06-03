package com.kremski.alert24.serwer.command;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerCommandFactory {
	
	public static final String ACTION_JSON_OBJECT_KEY = "Action";
	
	public static final String ADD_EVENT_ACTION = "Add_event";
	public static final String ADD_MESSAGE_ACTION = "NewMessage";
	
	public ServerCommand createCommand(ServerCommandHandler serverCommandHandler, JSONObject serverResponse) throws JSONException {
		if (serverResponse.getString(ServerCommandFactory.ACTION_JSON_OBJECT_KEY).equals(ADD_EVENT_ACTION)) {
			return new AddEventCommand(serverCommandHandler, serverResponse);
		}
		else if (serverResponse.getString(ServerCommandFactory.ACTION_JSON_OBJECT_KEY).equals(ADD_MESSAGE_ACTION)) {
			return new AddMessageCommand(serverCommandHandler, serverResponse);
		}
		throw new IllegalArgumentException("Incorrect key : " + ACTION_JSON_OBJECT_KEY);
	}
	
	public boolean isServerCommand(JSONObject serverResponse) {
		return serverResponse.has(ServerCommandFactory.ACTION_JSON_OBJECT_KEY);
	}
}
