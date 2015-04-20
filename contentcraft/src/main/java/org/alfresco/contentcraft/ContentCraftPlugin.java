/**
 * 
 */
package org.alfresco.contentcraft;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.logging.Logger;

import org.alfresco.contentcraft.repository.BookListener;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.ApplicationContext;
/**
 * Content craft plugin implementation
 * 
 * @author Roy Wetherall
 */
public class ContentCraftPlugin extends JavaPlugin 
{
	private static ContentCraftPlugin plugin;
	
	public static Logger logger;
	public static ApplicationContext context;
	
	public static ContentCraftPlugin getPlugin()
	{
		return plugin;
	}
	
	/**
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
	 */
	@Override
	public void onEnable() 
	{
		ContentCraftPlugin.logger.info("onEnable had been invoked");
		ContentCraftPlugin.plugin = this;
		
		// get the plugin description file
		PluginDescriptionFile descriptionFile = getDescription();
		
		// get the command information map
		Map<String, Map<String, Object>> commands = descriptionFile.getCommands();
		if (commands != null) {
			for (Map.Entry<String, Map<String, Object>> entry : commands.entrySet()) 
			{
				// look for the executor class
				String className = (String)entry.getValue().get("executor");
				if (className != null)
				{
					try
					{
						// create an instance of the the executor class
						ContentCraftPlugin.logger.info("Class name is: " + className);
						Class<?> clazz = Class.forName(className);
						Constructor<?> ctor = clazz.getConstructor(String.class, Map.class);
						CommandExecutor commandExecutor = (CommandExecutor)ctor.newInstance(entry.getKey(), entry.getValue());
						
						// set the command executor
						getCommand(entry.getKey()).setExecutor(commandExecutor);
 		         ContentCraftPlugin.logger.info("Command is: " + commandExecutor);
						
						// if the command is a listener
						if (commandExecutor instanceof Listener)
						{
							// register events
							getServer().getPluginManager().registerEvents((Listener)commandExecutor, this);
		          ContentCraftPlugin.logger.info("Command registered");
						}
					}
					catch (Exception exception)
					{
						exception.printStackTrace();
					} 
				} else {
					ContentCraftPlugin.logger.info("we got no class");
				}
			}
		} else {
			ContentCraftPlugin.logger.info("Where are the commands?");
		}
		
		// TODO do better
		// register the book listner
		BookListener bookListener = new BookListener();
		getServer().getPluginManager().registerEvents((Listener)bookListener, this);
		
		//Start Spring
		AppBootstrap springApp = new AppBootstrap(ContentCraftPlugin.logger);
		context = springApp.bootstrapSpring();
	}
	
	/**
	 * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
	 */
	@Override
	public void onDisable() 
	{
		ContentCraftPlugin.logger.info("onDisable has been invoked");
	}

	@Override
	public void onLoad()
	{
		logger = getLogger();
	}
	
}
