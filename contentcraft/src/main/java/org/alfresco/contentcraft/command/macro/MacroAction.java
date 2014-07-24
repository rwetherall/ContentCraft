package org.alfresco.contentcraft.command.macro;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;

/**
 * 
 * @author Roy Wetherall
 * @since 1.0
 */
/*package*/ abstract class MacroAction 
{
	private Vector vector;
	
	public MacroAction()
	{
	}
	
	public MacroAction(Vector vector) 
	{
		this.vector = vector;
	}
	
	public Vector getVector()
	{
		return vector;
	}
	
	public void setVector(Vector vector)
	{
		this.vector = vector;
	}
	
	public abstract void execute(Location startLocation, MacroCallback callback);
	
	protected Location getRelativeLocation(Location location)
	{
		Location result = location.clone().add(vector);
		result.setWorld(location.getWorld());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON()
	{
		JSONObject jsonAction = new JSONObject();
		jsonAction.put("type", getClass().getName());
		
		JSONObject jsonVector = new JSONObject();
		jsonVector.put("x", vector.getX());
		jsonVector.put("y", vector.getY());
		jsonVector.put("z", vector.getZ());
		
		jsonAction.put("vector", jsonVector);
		return jsonAction;
	}		
	
	public void fromJSON(JSONObject jsonAction)
	{
		JSONObject jsonVector = (JSONObject)jsonAction.get("vector");
		Double x = (Double)jsonVector.get("x");
		Double y = (Double)jsonVector.get("y");
		Double z = (Double)jsonVector.get("z");		
		vector = new Vector(x, y, z);
	}
}
