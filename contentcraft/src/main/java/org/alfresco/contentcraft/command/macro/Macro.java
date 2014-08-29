package org.alfresco.contentcraft.command.macro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * 
 * @author Roy Wetherall
 * @since 1.0
 */
public class Macro 
{
	private String name;	
	private boolean transientMacro = false;
	private Location startLocation;
	private boolean recording = false;	
	private boolean runPending = false;
	private boolean runRepeat = false;
	private List<MacroAction> actions = new ArrayList<MacroAction>(27);
	
	/**
	 * Helper method that starts a new macro.
	 * 
	 * @param name				name of the macro
	 * @return {@link Macro} 	newly created and started macro
	 */
	public static final Macro startNew(String name)
	{
		Macro macro = new Macro(name);
		macro.start();
		return macro;
	}
	
	/**
	 * Default constructor.
	 * 
	 * @param name	name of the macro
	 */
	public Macro(String name) 
	{
		this.name = name;
	}
	
	/**
	 * @return	{@link String}	name of the macro
	 */
	public String getName() 
	{
		return name;
	}
	
	public boolean isTransient() 
	{
		return transientMacro;
	}
	
	public List<MacroAction> getActions() 
	{
		return actions;
	}
	
	
	public void runPending(boolean runRepeat)
	{
		if (!runPending)
		{
			if (recording)
			{
				// end recording
				recording = false;
			}
			
			runPending = true;
			this.runRepeat = runRepeat;
		}
	}
	
	public void run(Location location)
	{
		run(location, null);
	}
	
	public void run(Location location, MacroCallback callback)
	{
		//System.out.println("Running macro: " + getName() + " at location " + location.toString());
		
		for (MacroAction action : actions) 
		{
			action.execute(location, callback);
		}		
	}
	
	public void clear()
	{
		actions = new ArrayList<MacroAction>(27);
		startLocation = null;	
	}
	
	public void start()
	{
		if (!recording)
		{
			recording = true;
			clear();
		}
	}
	
	public void stop()
	{
		recording = false;
		runPending = false;
		runRepeat = false;
	}

	/*package*/ void onBlockBreak(BlockBreakEvent event)
	{
		Block brokenBlock = event.getBlock();	
		Location location = brokenBlock.getLocation();
		
		if (recording)
		{
			if (startLocation == null)
			{
				// record start location
				startLocation = location;
			}
					
			actions.add(BreakBlockMacroAction.create(brokenBlock, startLocation));
		}
	}
	
	/*package*/ void onBlockPlace(BlockPlaceEvent event)
	{
		Block placedBlock = event.getBlockPlaced();	
		Location placedLocation = placedBlock.getLocation();
		
		if (recording)
		{
			if (startLocation == null)
			{
				// record start location
				startLocation = placedLocation;
			}
					
			actions.add(PlaceBlockMacroAction.create(placedBlock, startLocation));
			
			if (Material.WOODEN_DOOR.equals(placedBlock.getType()) ||
				Material.IRON_DOOR.equals(placedBlock.getType()))
			{
				System.out.println("Dealing with top of the door");
				// need to record the top part of the door too
				Block topDoor = placedBlock.getRelative(BlockFace.UP);
				actions.add(PlaceBlockMacroAction.create(topDoor, startLocation));
			}
		}
		else if (runPending)
		{
			if (!runRepeat)
			{
				runPending = false;
			}
			run(placedLocation);
		}
	}		
	
	@SuppressWarnings("unchecked")
	/*package*/JSONObject toJSON()
	{
		JSONObject jsonMacro = new JSONObject();			
		jsonMacro.put("name", getName());
		
		JSONArray jsonActions = new JSONArray();			
		for (MacroAction action : getActions()) 
		{
			jsonActions.add(action.toJSON());
		}
		jsonMacro.put("actions", jsonActions);
		return jsonMacro;
	}
	
	@SuppressWarnings("rawtypes")
	/*package*/ static Macro fromJSON(JSONObject jsonMacro)
	{
		Macro macro = new Macro((String)jsonMacro.get("name"));		
		
		if (jsonMacro.containsKey("transient"))
		{
			boolean isTransient = (Boolean)jsonMacro.get("transient");
			macro.transientMacro = isTransient;
		}
		
		JSONArray jsonActions = (JSONArray)jsonMacro.get("actions");		
		try
		{
			Iterator it = jsonActions.iterator();
			while (it.hasNext()) 
			{
				JSONObject jsonAction = (JSONObject)it.next();
				
				String actionType = (String)jsonAction.get("type");
				Class actionClass = Class.forName(actionType);
				MacroAction action = (MacroAction)actionClass.newInstance();
				
				action.fromJSON(jsonAction);
				macro.actions.add(action);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return macro;
	}
}
