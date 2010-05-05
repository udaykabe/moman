package net.deuce.moman.controller.command;

import net.deuce.moman.om.EntityService;

import java.util.List;

public interface DynamicCommandBuilder {

  public CommandBuilderResult buildCommand(EntityService service, String commandName, List<String> arguments);
}
