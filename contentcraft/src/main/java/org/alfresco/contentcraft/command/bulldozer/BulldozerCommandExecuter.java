/**
 * 
 */
package org.alfresco.contentcraft.command.bulldozer;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.contentcraft.command.BaseCommandExecuter;
import org.alfresco.contentcraft.command.CommandUsageException;
import org.alfresco.contentcraft.command.annotation.CommandBean;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Example command to show mode change and event notifications.
 * 
 * @author Roy Wetherall
 */
@CommandBean
(
   name="bulldozer", 
   usage="/bulldozer on|off [size]"
)
public class BulldozerCommandExecuter extends BaseCommandExecuter implements Listener
{
	private Map<Player, Boolean> playerStates = new HashMap<Player, Boolean>(13);
	
	private ThreadLocal<BlockFace> lastBlockFace = new ThreadLocal<BlockFace>();

	public boolean onCommandImpl(CommandSender sender, Command command, String label, String[] args) throws CommandUsageException
	{
		boolean result = true;	
		
		Player player = (Player)sender;	
		playerStates.put(player, getState(args[0]));
		
		player.sendMessage("Setting bulldozer mode " + args[0] + ".");
		
		return result;
		
	}
	
	private boolean getState(String requiredState)
	{
		boolean result = false;		
		if (requiredState.toLowerCase().trim().equals("on") == true)
		{
			result = true;
		}		
		return result;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		lastBlockFace.set(event.getBlockFace());
	}
	
	@EventHandler
	public void blockBreakEvent(BlockBreakEvent event)
	{
		// get the player
		Player player = event.getPlayer();
		
		// determine whether we should do something
		Boolean isOn = playerStates.get(player);
		if (isOn != null && isOn)
		{
			// get the last interacted with face
			BlockFace face = lastBlockFace.get();
			if (face != null)
			{
				//player.sendMessage("Bulldozer mode is on and the last face to be clicked was " + face.toString());
				
				switch (face)
				{
					case NORTH:
						breakBlock(event.getBlock(), BlockFace.EAST, BlockFace.WEST);
						break;
					case EAST:
						breakBlock(event.getBlock(), BlockFace.NORTH, BlockFace.SOUTH);
						break;
					case SOUTH:
						breakBlock(event.getBlock(), BlockFace.EAST, BlockFace.WEST);
						break;
					case WEST:
						breakBlock(event.getBlock(), BlockFace.NORTH, BlockFace.SOUTH);
						break;
					default:
						break;
				
				}
			}
		}
	}
	
	private void breakBlock(Block block, BlockFace ...blockFaces)
	{
		for (BlockFace blockFace : blockFaces) 
		{
			Block toBreak = block.getRelative(blockFace);
			toBreak.breakNaturally();
		}
	}
}
