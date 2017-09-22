
package org.alfresco.contentcraft.command.api;

import java.util.Map;

import org.alfresco.contentcraft.api.AlfrescoAPI;
import org.alfresco.contentcraft.command.BaseCommandExecuter;
import org.alfresco.contentcraft.command.CommandUsageException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Alfresco V1 REST API Executer
 * 
 * @author Roy Wetherall
 */
public class APICommandExecuter extends BaseCommandExecuter 
{
	public APICommandExecuter(String name, Map<String, Object> properties) 
	{
		super(name, properties);
	}

	public void onCommandImpl(CommandSender sender, Command command, String label, String[] args) throws CommandUsageException
	{
		String result = AlfrescoAPI.testConnect();
		sender.sendMessage(result);
	}	
}
