/**
 * 
 */
package org.alfresco.contentcraft.command.build;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.contentcraft.command.BaseCommandExecuter;
import org.alfresco.contentcraft.command.CommandUsageException;
import org.alfresco.contentcraft.util.VectorUtil;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Build command.
 * 
 * @author Roy Wetherall
 * @since  1.0
 */
public class BuildCommandExecuter extends BaseCommandExecuter
{
	/** map of builder */
	private Map<String, Builder> builders = new HashMap<String, Builder>();
	
	/**
	 * Default constructor
	 * 
	 * @param name			name 
	 * @param properties	properties
	 */
	public BuildCommandExecuter(String name, Map<String, Object> properties) 
	{
		super(name, properties);
		
		registerBuilder(new SiteBuilder());
	}
	
	protected void registerBuilder(Builder builder)
	{
		builders.put(builder.getName(), builder);
	}

	public void onCommandImpl(CommandSender sender, Command command, String label, String[] args) throws CommandUsageException
	{
		// determine 'what' we are trying to build
		String what = args[0];			
		if (what == null)
		{
			throw new CommandUsageException("You must tell me what you want to build!");
		}
		
		// get the builder
		Builder builder = builders.get(what);
		if (builder == null)
		{
			throw new CommandUsageException("I don't know how to build a " + what + "!");
		}
		
		Location location = null;
		Player player = null;
		if (sender instanceof Player)
		{
			location = ((Player)sender).getLocation();
			player = (Player)sender;
		}
		else if (sender instanceof BlockCommandSender)
		{
			BlockCommandSender block = (BlockCommandSender)sender;
			location = block.getBlock().getLocation();
		}
		
		// get the direction
		Vector direction = VectorUtil.round(location.getDirection());
		location.add(direction.clone().multiply(3));
		
		// execute the builder
		builder.build(player, location, direction, args);		
	}
	

}
