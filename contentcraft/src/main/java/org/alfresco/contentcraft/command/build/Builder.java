package org.alfresco.contentcraft.command.build;

import org.alfresco.contentcraft.command.CommandUsageException;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public interface Builder 
{
	String getName();
	
	void build(Location start, Vector direction, String ... args) throws CommandUsageException;
}
