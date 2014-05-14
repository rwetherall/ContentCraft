/**
 * 
 */
package org.alfresco.contentcraft;

import org.alfresco.contentcraft.command.CommandRegistry;
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
		
		// register commands
		CommandRegistry.getInstance().initialise(this);
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
