/**
 * 
 */
package org.alfresco.contentcraft.command.build;

import java.util.Map;

import org.alfresco.contentcraft.command.BaseCommandExecuter;
import org.alfresco.contentcraft.command.CommandUsageException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Example command that can be used to show plugin working!
 * 
 * TODO will be the basis of building the different repository elements?
 * TODO for example .. /build site "my site" .. could build something that represents the site in the repo
 * 
 * @author Roy Wetherall
 */
public class BuildCommandExecuter extends BaseCommandExecuter
{
	public BuildCommandExecuter(String name, Map<String, Object> properties) 
	{
		super(name, properties);
	}

	public boolean onCommandImpl(CommandSender sender, Command command, String label, String[] args) throws CommandUsageException
	{
		boolean result = false;
		
		// check we have at least one arg
		if (args.length == 1)
		{
			String what = args[0];			
			sender.getServer().broadcastMessage("We are going to build a " + what + "!");
		}
		
		return result;
		
	}
}
