/**
 * 
 */
package org.alfresco.contentcraft.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Example command that can be used to show plugin working!
 * 
 * @author Roy Wetherall
 */
public class BuildCommandExecuter extends BaseCommandExecuter
{
	public static final String NAME = "build";
	
	@Override
	public String getName() 
	{
		return NAME;
	}

	public boolean onCommandImpl(CommandSender sender, Command command, String label, String[] args) 
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
