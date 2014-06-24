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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Build command.
 * 
 * @author Roy Wetherall
 */
public class BuildCommandExecuter extends BaseCommandExecuter
{
	protected Player player;
	
	private Map<String, Builder> builders = new HashMap<String, Builder>();
	
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
		
		// stash the the player
		player = (Player)sender;
		
		// get the start location for the build (3 blocks in the direction the player is facing)
		Location location = player.getLocation();		
		Vector playerDirection = VectorUtil.round(location.getDirection());
		
		location.add(playerDirection.clone().multiply(3));
		
		// execute the builder
		builder.build(player, location, playerDirection, args);		
	}
}
