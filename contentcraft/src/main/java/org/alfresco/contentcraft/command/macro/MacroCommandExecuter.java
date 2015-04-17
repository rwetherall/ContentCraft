package org.alfresco.contentcraft.command.macro;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.contentcraft.ContentCraftPlugin;
import org.alfresco.contentcraft.command.BaseCommandExecuter;
import org.alfresco.contentcraft.command.CommandUsageException;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Macro command executer implementation.
 * 
 * @author Roy Wetherall
 * @since 1.0
 */
public class MacroCommandExecuter extends BaseCommandExecuter implements Listener
{
	private static final String MACRO_FOLDER = "./macros";
	
	private static final String CMD_START = "start";
	private static final String CMD_STOP = "stop";
	private static final String CMD_RUN = "run";
	private static final String CMD_LIST = "list";
	private static final String CMD_DELETE = "delete";
	
	/** map of currently available macros */
	private Map<String, Macro> macros = new HashMap<String, Macro>();
	
	/** currently recording macro */
	private Macro recordingMacro;
	
	private static MacroCommandExecuter instance;
	
	public static MacroCommandExecuter getInstance()
	{
	    if (instance == null)
	    {
	        // get the macro command
	        instance = (MacroCommandExecuter)ContentCraftPlugin.getPlugin().getCommand("macro").getExecutor();
	    }
	    
	    return instance;
	}
	
	/**
	 * 
	 * @param name
	 * @param location
	 */
	public static void run(String name, Location location)
	{
	    MacroCommandExecuter.run(name, location, null);
	}
	
	/**
	 * 
	 * @param name
	 * @param location
	 * @param callback
	 */
	public static void run(String name, Location location, MacroCallback callback)
    {
	    MacroCommandExecuter macroCommandExecuter = MacroCommandExecuter.getInstance();
	    
	    Macro macro = macroCommandExecuter.getMacro(name);
	    if (macro == null)
	    {
	        throw new RuntimeException("Can not find macro " + name);
	        
	    }
	    
	    macro.run(location, callback);
    }
	
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
	 * Get macro by name
	 * 
	 * @param  name            macro name
	 * @return {@link Macro}   macro object
	 */
	public Macro getMacro(String name)
	{
		return macros.get(name);
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
		else if (operation.equalsIgnoreCase(CMD_STOP))
		{
			// stop recording the currently recording macro
			if (recordingMacro != null)
			{
				recordingMacro.stop();
				recordingMacro = null;
				save();
			}
			else
			{
			    for (Macro macro : macros.values())
                {
                    macro.stop();
                }
			}
		}
		else
		{
			String name = args[1];
			Macro macro = macros.get(name);
			
			if (operation.equalsIgnoreCase(CMD_START))
			{
				if (recordingMacro == null)
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
					
					// store the currently recording macro
					recordingMacro = macro;
				}
				else
				{
					// a macro is already recording
					sender.sendMessage("Can't start macro, because " + recordingMacro.getName() + " is currently being recorded.");
				}
			}
			else if (operation.equalsIgnoreCase(CMD_RUN))
			{
				if (macro != null)
				{
					boolean runRepeat = false;
					
					if (args.length == 3 && "-R".equals(args[2]))
					{
						runRepeat = true;
					}
					
					macro.runPending(runRepeat);
				}
			}
			else if (operation.equalsIgnoreCase(CMD_DELETE))
			{
				if (macro != null)
				{
					macros.remove(name);
					save();
				}
			}
		}
	}
	
	/**
	 * On block break event handler
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		for (Macro macro : macros.values()) 
		{
			macro.onBlockBreak(event);
		}		
	}
	
	/**
	 * On block place event handler
	 */
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
	
	private void save()
	{		
		try 
		{			
			File macroFolder = new File(MACRO_FOLDER);
			if (!macroFolder.exists()) 
			{
				macroFolder.mkdir();
			}
						
			for (Macro macro : macros.values()) 
			{
				if (!macro.isTransient())
				{					
					// write JSON to config file
					File file = new File(MACRO_FOLDER + "/" + macro.getName() + ".json");
		            FileWriter fileWriter = new FileWriter(file);
		            try
		            {
		            	fileWriter.write(macro.toJSON().toJSONString());
		            }
		            finally
		            {
		            	// close file
		            	fileWriter.close();
		            }
				}
			}			
		} 
		catch (IOException e) 
		{
			// deal with exception
			e.printStackTrace();
		} 
	}
	
	/**
	 * 
	 */
	private void load()
	{
		File macroFolder = new File(MACRO_FOLDER);
		if (macroFolder.exists()) 
		{
			File[] files = macroFolder.listFiles();
			for (File file : files) 
			{
				try
				{
					FileReader fileReader = new FileReader(file);
					try
					{					
						load(fileReader);
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
			}
		}
	
	}
	
	public void load(Reader reader)
	{
		try
		{
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonMacro = (JSONObject)jsonParser.parse(reader);
			Macro macro = Macro.fromJSON(jsonMacro);
			macros.put(macro.getName(), macro);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
