/**
 * 
 */
package org.alfresco.contentcraft.util;

import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/**
 * @author Roy Wetherall
 */
public class VectorUtil 
{	
	public static final Vector UP 		= new Vector(0, 1, 0);
	public static final Vector DOWN 	= new Vector(0, -1, 0);
	public static final Vector NORTH 	= new Vector(0, 0, -1);
	public static final Vector SOUTH 	= new Vector(0, 0, 1);
	public static final Vector EAST 	= new Vector(1, 0, 0);
	public static final Vector WEST 	= new Vector(-1, 0, 0);

	public static Vector rotate90(Vector vector)
	{
		double x = vector.getZ()*-1;
		double z = vector.getX();		
		return new Vector(x, vector.getY(), z);
	}
		
	public static Vector round(Vector vector)
	{
		vector.normalize();
		return new Vector(Math.round(vector.getX()),
				          Math.round(vector.getY()),
				          Math.round(vector.getZ()));
	}
	
	public static boolean equals(Vector vector1, Vector vector2)
	{
		return (vector1.getX() == vector2.getX() &&
				vector1.getY() == vector2.getY() &&
				vector1.getZ() == vector2.getZ());
	}
	
	public static BlockFace toBlockFace(Vector vector)
	{
		// clone and round the vector 
		Vector local = round(vector);
	
		BlockFace result = BlockFace.SELF;
		if (equals(local, UP))
		{
			result = BlockFace.UP;
		}
		else if (equals(local, DOWN))
		{
			result = BlockFace.DOWN;
		}
		else if (equals(local, NORTH))
		{
			result = BlockFace.NORTH;
		}
		else if (equals(local, SOUTH))
		{
			result = BlockFace.SOUTH;
		}
		else if (equals(local, EAST))
		{
			result = BlockFace.EAST;
		}
		else if (equals(local, WEST))
		{
			result = BlockFace.WEST;
		} 
		
		return result;
	}
	
}
