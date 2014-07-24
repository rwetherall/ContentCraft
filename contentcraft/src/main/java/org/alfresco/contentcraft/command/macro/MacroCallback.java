package org.alfresco.contentcraft.command.macro;

import org.bukkit.block.Block;

public abstract class MacroCallback
{
	public void placeBlock(Block block) {};
	
	public void breakBlock(Block block) {};
}
