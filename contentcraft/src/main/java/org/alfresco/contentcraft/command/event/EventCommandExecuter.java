
package org.alfresco.contentcraft.command.event;

import java.util.Arrays;
import java.util.Map;

import org.alfresco.contentcraft.ContentCraftPlugin;
import org.alfresco.contentcraft.command.BaseCommandExecuter;
import org.alfresco.contentcraft.command.CommandUsageException;
import org.alfresco.contentcraft.events.Listener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Event command.
 * 
 * @author Gethin James
 */
public class EventCommandExecuter extends BaseCommandExecuter 
{
	Listener listener;
	
	public EventCommandExecuter(String name, Map<String, Object> properties) 
	{
		super(name, properties);
		listener = new Listener(ContentCraftPlugin.logger);
		
	}

	public void onCommandImpl(CommandSender sender, Command command, String label, String[] args) throws CommandUsageException
	{			
		if (args == null || args.length < 1 || args[0] == null)
		{
			throw new CommandUsageException("Usage: connect [topicname] | disconnect");
		}
		String action = args[0];
		switch (action) {
		case "connect":
			listener.connect("tcp://localhost:61616", args[1]);				
			break;

		default:
			sender.sendMessage("Unrecognised "+Arrays.toString(args));			
			break;
		}

	}	
}
