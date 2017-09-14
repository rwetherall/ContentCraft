package org.alfresco.contentcraft.command.macro;

import org.bukkit.block.Block;

/**
 * 
 * 
 * @author Roy Wetherall
 * @since 1.0
 */
public abstract class MacroCallback
{
	public abstract void callback(String macroAction, Block block);
}
