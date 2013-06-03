package com.kremski.alert24.serwer.command;

import org.json.JSONObject;

public abstract class DefaultServerCommand implements ServerCommand{
	
	protected ServerCommandHandler serverCommandHandler;
	protected JSONObject serverCommand;
	
	public DefaultServerCommand(ServerCommandHandler serverCommandHandler, JSONObject serverCommand) {
		this.serverCommandHandler = serverCommandHandler;
		this.serverCommand = serverCommand;
	}
}
