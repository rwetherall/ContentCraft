package org.alfresco.contentcraft.command.macro;

import java.util.Map;

import org.alfresco.contentcraft.command.BaseCommandExecuter;
import org.alfresco.contentcraft.command.CommandUsageException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class MacroCommandExecuter extends BaseCommandExecuter implements Listener
{
	public MacroCommandExecuter(String name, Map<String, Object> properties) 
	{
		super(name, properties);
	}

	@Override
	public void onCommandImpl(CommandSender sender, Command command, String label, String[] args) throws CommandUsageException 
	{
		String operation = args[0];
		String name = args[1];
		
		if (operation.equals("start"))
		{
			System.out.println("Starting macro " + name);
		}
		else if (operation.equals("stop"))
		{
			System.out.println("Stopping macro " + name);
		}
		else if (operation.equals("run"))
		{
			System.out.println("Running macro " + name);
		}
		else
		{
			// error
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		System.out.println("action:" + event.getAction().toString());
		System.out.println("clicked-block:" + event.getClickedBlock().getType().toString());
		System.out.println("item-in-hand:" + event.getPlayer().getItemInHand().getType().toString());
		System.out.println("clicked-face:" + event.getBlockFace().toString());
		System.out.println("location:" + event.getClickedBlock().getLocation().toString());
	}

}
