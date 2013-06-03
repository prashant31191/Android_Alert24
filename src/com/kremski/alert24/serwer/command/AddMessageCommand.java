package com.kremski.alert24.serwer.command;

import java.util.TimeZone;

import hirondelle.date4j.DateTime;

import org.json.JSONException;
import org.json.JSONObject;

import com.kremski.alert24.domain.Event.EventBuilder;
import com.kremski.alert24.domain.SystemNotification;


public class AddMessageCommand extends DefaultServerCommand {

	public static final String MESSAGE_JSON_OBJECT_KEY = "SystemNotification";
	public static final String MESSAGE_CONTENT_KEY = "Content";
	public static final String MESSAGE_TITLE_KEY = "Title";
	public static final String MESSAGE_AUTHOR_KEY = "Author";
	public static final String MESSAGE_CREATED_ON_KEY = "CreatedOn";

	public AddMessageCommand(ServerCommandHandler serverCommandHandler, JSONObject serverCommand) {
		super(serverCommandHandler, serverCommand);
	}

	@Override
	public void execute() throws JSONException {
		JSONObject serverMessage = serverCommand.getJSONObject(MESSAGE_JSON_OBJECT_KEY);
		String messageContent = serverMessage.getString(MESSAGE_CONTENT_KEY); 
		String messageAuthor = serverMessage.getString(MESSAGE_AUTHOR_KEY); 
		String messageCreatedOn = serverMessage.getString(MESSAGE_CREATED_ON_KEY); 
		//TODO change date
		String messageTitle = serverMessage.getString(MESSAGE_TITLE_KEY); 
		serverCommandHandler.addMessage(new SystemNotification(messageTitle, messageContent, messageAuthor, false,
				DateTime.now(TimeZone.getDefault()).getMilliseconds(TimeZone.getDefault())));

	}

	public interface AddMessageCommandHandler {
		public void addMessage(SystemNotification message);
	}
}
