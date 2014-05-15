package org.alfresco.contentcraft.command;

import java.util.HashSet;
import java.util.Set;

import org.alfresco.contentcraft.command.build.BuildCommandExecuter;
import org.alfresco.contentcraft.command.bulldozer.BulldozerCommandExecuter;
import org.alfresco.contentcraft.command.cmis.CMISCommandExecuter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Roy Wetherall
 */
public class CommandRegistry 
{
	/** static instance of {@link CommandRegistry} */
	private static CommandRegistry instance;
	
	/** set of command executors */
	private Set<BaseCommandExecuter> commandExecutors = new HashSet<BaseCommandExecuter>();
	
	/**
	 * @return	static instance of {@link CommandRegistry}
	 */
	public static CommandRegistry getInstance()
	{
		if (CommandRegistry.instance == null)
		{
			CommandRegistry.instance = new CommandRegistry();
		}		
		return CommandRegistry.instance;
	}
	
	/**
	 * Default constructor
	 */
	public CommandRegistry() 
	{
		// register all the commands
		commandExecutors.add(new BuildCommandExecuter());
		commandExecutors.add(new BulldozerCommandExecuter());
		commandExecutors.add(new CMISCommandExecuter());
	}
	
	/**
	 * Register the command executor with the plugin
	 * 
	 * @param plugin
	 */
	public void initialise(JavaPlugin plugin)
	{
		for (BaseCommandExecuter commandExecutor : commandExecutors) 
		{
			plugin.getCommand(commandExecutor.getName()).setExecutor(commandExecutor);
			if (commandExecutor instanceof Listener)
			{
				plugin.getServer().getPluginManager().registerEvents((Listener)commandExecutor, plugin);
			}
		}
	}
	
}
