/**
 * 
 */
package org.alfresco.contentcraft;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Content craft plugin implementation
 * 
 * @author Roy Wetherall
 */
public class ContentCraftPlugin extends JavaPlugin 
{
	/**
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
	 */
	@Override
	public void onEnable() 
	{
		getLogger().info("onEnable had been invoked");
		
		// get the plugin description file
		PluginDescriptionFile descriptionFile = getDescription();
		
		
		
		// get the command information map
		Map<String, Map<String, Object>> commands = descriptionFile.getCommands();
		for (Map.Entry<String, Map<String, Object>> entry : commands.entrySet()) 
		{
			// look for the executor class
			String className = (String)entry.getValue().get("executor");
			if (className != null)
			{
				try
				{
					// create an instance of the the executor class
					Class<?> clazz = Class.forName(className);
					Constructor<?> ctor = clazz.getConstructor(String.class, Map.class);
					CommandExecutor commandExecutor = (CommandExecutor)ctor.newInstance(entry.getKey(), entry.getValue());
					
					// set the command executor
					getCommand(entry.getKey()).setExecutor(commandExecutor);
					
					// if the command is a listener
					if (commandExecutor instanceof Listener)
					{
						// register events
						getServer().getPluginManager().registerEvents((Listener)commandExecutor, this);
					}
				}
				catch (Exception exception)
				{
					exception.printStackTrace();
				} 
			}
		}
	}
	
	/**
	 * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
	 */
	@Override
	public void onDisable() 
	{
		getLogger().info("onDisable has been invoked");
	}
	
}
