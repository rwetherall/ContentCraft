/**
 * 
 */
package org.alfresco.contentcraft.command;

import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Base command executer implementation.
 * 
 * @author Roy Wetherall
 */
public abstract class BaseCommandExecuter implements CommandExecutor
{
	protected String name;
	
	protected Map<String, Object> properties;
	
	public BaseCommandExecuter(String name, Map<String, Object> properties)
	{
		this.name = name;
		this.properties = properties;
	}
	
	protected boolean isPlayerCommand()
	{
		return true;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		boolean result = true;
		
		try
		{
			// FIXME
			// cross check the name of the command
			
			// FIXME
			// cross check the number of expected arguments
			
			// cross check whether we are expecting this command from a player or not
			if (checkForPlayerCommand(sender) == true)
			{
				// execute the command
				result = onCommandImpl(sender, command, label, args);
			}
		}
		catch (CommandUsageException exception)
		{
			// present usage to user
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param sender
	 * @return
	 */
	protected boolean checkForPlayerCommand(CommandSender sender)
	{
		boolean result = true;
		
		if (isPlayerCommand() == true && 
		   (sender instanceof Player) == false)
		{
			sender.sendMessage("This command can only be run by a player.");
			result = false;
		}
		
		return result;
	}

	/**
	 * 
	 * @param sender
	 * @param command
	 * @param label
	 * @param args
	 * @return
	 */
	public abstract boolean onCommandImpl(CommandSender sender, Command command, String label, String[] args) throws CommandUsageException;

}
