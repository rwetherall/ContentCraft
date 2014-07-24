package org.alfresco.contentcraft.command.macro;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/*package*/ class BreakBlockMacroAction extends MacroAction 
{
	public BreakBlockMacroAction() 
	{
		super();
	}
	
	public BreakBlockMacroAction(Vector vector)
	{
		super(vector);
	}
	
	public static BreakBlockMacroAction create(Block block, Location startLocation)
	{
		Location location = block.getLocation();
		Vector vector = location.toVector().clone().subtract(startLocation.toVector());					
		return new BreakBlockMacroAction(vector);		
	}
	
	@Override
	public void execute(Location startLocation, MacroCallback callback) 
	{
		Location location = getRelativeLocation(startLocation);
		Block block = location.getBlock();
		block.setType(Material.AIR);	
		
		if (callback != null)
		{
			callback.breakBlock(block);
		}
	}
}
