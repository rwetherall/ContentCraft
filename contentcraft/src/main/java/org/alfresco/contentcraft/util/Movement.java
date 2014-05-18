/**
 * 
 */
package org.alfresco.contentcraft.util;

import org.bukkit.Location;

/**
 * @author Roy Wetherall
 */
public class Movement
{
	public static Location forward(Location location, Direction direction)
	{
		return forward(location, direction, 1);
	}
	
	public static Location forward(Location location, Direction direction, int count)
	{
		return location.clone().add(direction.toVector().multiply(count));
	} 

}
