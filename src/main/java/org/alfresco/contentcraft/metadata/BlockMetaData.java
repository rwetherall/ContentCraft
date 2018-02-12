package org.alfresco.contentcraft.metadata;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.contentcraft.ContentCraftPlugin;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class BlockMetaData implements Listener
{
	private static final String file = "blockmetadata.bin";
	private static Map<String, Map<String, Serializable>> metadata = new HashMap<String, Map<String,Serializable>>();

	@EventHandler
	public void onDisable(PluginDisableEvent event)
	{
		ContentCraftPlugin.logger.info("Saving block metadata");
		
		try
		{
			FileOutputStream fileOut = null;
			ObjectOutputStream out = null;
			try 
			{
				fileOut = new FileOutputStream(file);
				out = new ObjectOutputStream(fileOut);
				out.writeObject(metadata);
			} 
			finally
			{
				out.close();
				fileOut.close();
				
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings("unchecked")
	@EventHandler
	public void onEnable(PluginEnableEvent event)
	{
		ContentCraftPlugin.logger.info("Initialising block metadata");
		
		try
		{
			InputStream fileIn = null;
			ObjectInputStream in = null;
			try 
			{
				fileIn = getClass().getClassLoader().getResourceAsStream("metadata/" + file);
				if (fileIn != null)
				{
					in = new ObjectInputStream(fileIn);
					metadata = (Map<String, Map<String, Serializable>>)in.readObject();
					
					if (metadata == null)
					{
						metadata = new HashMap<String, Map<String, Serializable>>(10);
					}
				}
			} 
			finally
			{
				if (in != null) { in.close(); }
				if (fileIn != null) { fileIn.close(); }				
			}
		}
		catch (IOException | ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static Serializable getMetadata(Block block, String name)
	{
		Serializable result = null;
		String blockKey = getBlockKey(block);
		if (metadata.containsKey(blockKey))
		{
			Map<String, Serializable> values = metadata.get(blockKey);
			if (values.containsKey(name))
			{
				result = values.get(name);
			}
		}
		return result;
	}
	
	public static void setMetadata(Block block, String name, Serializable value)
	{
		String blockKey = getBlockKey(block);
		
		Map<String, Serializable> values = metadata.get(blockKey);
		if (values == null)
		{
			values = new HashMap<String, Serializable>(5);
			metadata.put(blockKey, values);
		}
		
		values.put(name, value);				
	}
	
	public static boolean hasMetadata(Block block, String name)
	{
		return (getMetadata(block, name) != null);
	}
	
	private static String getBlockKey(Block block)
	{
		return block.getWorld().getName() + block.getX() + block.getY() + block.getZ();
	}
}
