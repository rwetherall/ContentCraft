package org.alfresco.contentcraft.command.api;

import java.util.Map;

import org.alfresco.contentcraft.command.BaseCommandExecuter;
import org.alfresco.contentcraft.command.CommandUsageException;
import org.alfresco.contentcraft.rest.REST;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * REST command.
 * 
 */
public class APICommandExecuter extends BaseCommandExecuter 
{
	public APICommandExecuter(String name, Map<String, Object> properties) 
	{
		super(name, properties);
	}

	public void onCommandImpl(CommandSender sender, Command command, String label, String[] args) throws CommandUsageException
	{
		String result = REST.testConnect();
		sender.sendMessage(result);
	}	
}
