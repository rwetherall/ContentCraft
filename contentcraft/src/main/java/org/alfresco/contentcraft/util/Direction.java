/**
 * 
 */
package org.alfresco.contentcraft.util;

import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/**
 * Helper to easily determine the direction based someone is looking.
 * <p>
 * This can be then used as a single unit directional vector or converted into
 * a BlockFace.
 * 
 * @author Roy Wetherall
 */
public class Direction 
{
	public static final Direction UP 	= new Direction(new Vector(0, 1, 0));
	public static final Direction DOWN 	= new Direction(new Vector(0, -1, 0));
	public static final Direction NORTH = new Direction(new Vector(0, 0, -1));
	public static final Direction SOUTH = new Direction(new Vector(0, 0, 1));
	public static final Direction EAST 	= new Direction(new Vector(1, 0, 0));
	public static final Direction WEST 	= new Direction(new Vector(-1, 0, 0));
	
	private Vector vector;
	
	public Direction(Vector vector) 
	{
		this.vector = round(vector);
	}
	
	private Vector round(Vector vector)
	{
		vector.normalize();
		return new Vector(Math.round(vector.getX()),
				          Math.round(vector.getY()),
				          Math.round(vector.getZ()));
	}
	
	public void flaten()
	{
		vector.setY(0);
	}
	
	public Vector toVector()
	{
		return vector.clone();
	}
	
	public BlockFace toBlockFace()
	{
		BlockFace result = BlockFace.SELF;
		if (vector.equals(UP))
		{
			result = BlockFace.UP;
		}
		else if (vector.equals(DOWN))
		{
			result = BlockFace.DOWN;
		}
		else if (vector.equals(NORTH))
		{
			result = BlockFace.NORTH;
		}
		else if (vector.equals(SOUTH))
		{
			result = BlockFace.SOUTH;
		}
		else if (vector.equals(EAST))
		{
			result = BlockFace.EAST;
		}
		else if (vector.equals(WEST))
		{
			result = BlockFace.WEST;
		} 
		
		return result;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		Direction that = (Direction)obj;
		
		return (vector.getX() == that.vector.getX() &&
				vector.getY() == that.vector.getY() &&
				vector.getZ() == that.vector.getZ());	
	}
	
	@Override
	public int hashCode() 
	{
		return vector.hashCode();
	}

}
