package org.alfresco.contentcraft.command.macro;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.contentcraft.command.BaseCommandExecuter;
import org.alfresco.contentcraft.command.CommandUsageException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Macro command executer implementation.
 * 
 * @author Roy Wetherall
 * @since 1.0
 */
public class MacroCommandExecuter extends BaseCommandExecuter implements Listener
{
	private static final String MACRO_CONFIG_FILE = "./macros.json";
	
	private static final String CMD_START = "start";
	private static final String CMD_STOP = "stop";
	private static final String CMD_RUN = "run";
	private static final String CMD_LIST = "list";
	
	/** map of currenlty available macros */
	private Map<String, Macro> macros = new HashMap<String, Macro>();
	
	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param properties
	 */
	public MacroCommandExecuter(String name, Map<String, Object> properties) 
	{
		super(name, properties);
	}

	/**
	 * @see org.alfresco.contentcraft.command.BaseCommandExecuter#onCommandImpl(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public void onCommandImpl(CommandSender sender, Command command, String label, String[] args) throws CommandUsageException 
	{
		String operation = args[0];
		if (operation.equalsIgnoreCase(CMD_LIST))
		{
			StringBuilder builder = new StringBuilder("Available macros:\n");
			for (String name : macros.keySet()) 
			{
				builder.append("  - " + name + "\n");
			}			
			sender.sendMessage(builder.toString());
			
			// temp
			save();
		}
		else
		{
			String name = args[1];
			Macro macro = macros.get(name);
			
			if (operation.equalsIgnoreCase(CMD_START))
			{
				if (macro == null)
				{
					macro = Macro.startNew(name);
					macros.put(name, macro);
				}
				else
				{
					macro.clear();
					macro.start();
				}						
			}
			else if (operation.equalsIgnoreCase(CMD_STOP))
			{
				if (macro != null)
				{
					macro.stop();
					save();
				}				
			}
			else if (operation.equalsIgnoreCase(CMD_RUN))
			{
				if (macro != null)
				{
					macro.run();
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		for (Macro macro : macros.values()) 
		{
			macro.onBlockBreak(event);
		}		
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		for (Macro macro : macros.values()) 
		{
			macro.onBlockPlace(event);
		}
	}
	
	@EventHandler
	public void onPluginEnableEvent(PluginEnableEvent event)
	{
		load();
	}
	
	@EventHandler
	public void onPluginDisableEvent(PluginDisableEvent event)
	{
		save();
	}
	
	@SuppressWarnings("unchecked")
	private void save()
	{		
		try 
		{
			// generate JSON 
			JSONArray jsonMacros = new JSONArray();
			for (Macro macro : macros.values()) 
			{
				jsonMacros.add(macro.toJSON());
			}
			
			// write JSON to config file
			File file = new File(MACRO_CONFIG_FILE);
            FileWriter fileWriter = new FileWriter(file);
            try
            {
            	fileWriter.write(jsonMacros.toJSONString());
            }
            finally
            {
            	// close file
            	fileWriter.close();
            }			
		} 
		catch (IOException e) 
		{
			// deal with exception
			e.printStackTrace();
		} 
	}
	
	@SuppressWarnings("rawtypes")
	private void load()
	{
		File file = new File(MACRO_CONFIG_FILE);
		if (file.exists())
		{
			try
			{
				FileReader fileReader = new FileReader(file);
				try
				{
					JSONParser jsonParser = new JSONParser();
					JSONArray jsonMacros = (JSONArray)jsonParser.parse(fileReader);
					
					Iterator it = jsonMacros.iterator();
					while (it.hasNext()) 
					{
						JSONObject jsonMacro = (JSONObject)it.next();
						Macro macro = Macro.fromJSON(jsonMacro);
						macros.put(macro.getName(), macro);
					}
				}
				finally
				{
					fileReader.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}			
		}
	}
}
