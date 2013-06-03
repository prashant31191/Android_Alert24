package com.kremski.alert24.domain;

import org.json.JSONException;
import org.json.JSONStringer;

import android.content.Intent;

import com.kremski.alert24.screens.Alert24Screen.SystemMessageReceiver;
import com.kremski.alert24.serwer.command.AddMessageCommand;
import com.kremski.alert24.serwer.command.ServerCommandFactory;

public class MessageIntent extends Intent {
	
	public MessageIntent(String title, String author, String content, long createdOn) throws JSONException {
		super(SystemMessageReceiver.SYSTEM_INTENT_ACTION);
		
		JSONStringer stringer = new JSONStringer()
			.object()
				.key(ServerCommandFactory.ACTION_JSON_OBJECT_KEY)
					.value(ServerCommandFactory.ADD_MESSAGE_ACTION)
				.key(AddMessageCommand.MESSAGE_JSON_OBJECT_KEY)
					.object()
						.key(AddMessageCommand.MESSAGE_TITLE_KEY).value(title)
						.key(AddMessageCommand.MESSAGE_AUTHOR_KEY).value(author)
						.key(AddMessageCommand.MESSAGE_CONTENT_KEY).value(content)
						.key(AddMessageCommand.MESSAGE_CREATED_ON_KEY).value(createdOn)
					.endObject()
			.endObject();
		
		putExtra(SystemMessageReceiver.SYSTEM_MESSAGE_OBJECT_KEY, stringer.toString());
	}

}
