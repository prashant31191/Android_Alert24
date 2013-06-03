package com.kremski.alert24.serwer.command;

import com.kremski.alert24.serwer.command.AddEventCommand.AddEventCommandHandler;
import com.kremski.alert24.serwer.command.AddMessageCommand.AddMessageCommandHandler;

public interface ServerCommandHandler extends AddEventCommandHandler, AddMessageCommandHandler {

}
