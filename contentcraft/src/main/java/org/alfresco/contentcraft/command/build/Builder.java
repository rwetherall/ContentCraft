package org.alfresco.contentcraft.command.build;

import org.alfresco.contentcraft.command.CommandUsageException;
import org.alfresco.contentcraft.util.Direction;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Builder interface.
 * 
 * @author Roy Wetherall
 */
public interface Builder 
{
	String getName();
	
	void build(Player player, Location start, Direction direction, String ... args) throws CommandUsageException;
}
