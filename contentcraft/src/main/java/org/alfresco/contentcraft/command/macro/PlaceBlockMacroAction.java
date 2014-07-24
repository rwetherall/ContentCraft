package org.alfresco.contentcraft.command.macro;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;

/*package*/ class PlaceBlockMacroAction extends MacroAction 
{
	private Material material;
	private byte data;
	
	@SuppressWarnings("deprecation")
	public static PlaceBlockMacroAction create(Block block, Location startLocation)
	{
		Location location = block.getLocation();
		Vector vector = location.toVector().clone().subtract(startLocation.toVector());
		BlockState blockState = block.getState();
		MaterialData materialData = blockState.getData();					
		return new PlaceBlockMacroAction(vector, materialData.getItemType(), materialData.getData());		
	}
	
	public PlaceBlockMacroAction() 
	{
		super();
	}
	
	public PlaceBlockMacroAction(Vector vector, Material material, byte data) 
	{
		super(vector);
		this.data = data;
		this.material = material;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void execute(Location startLocation, MacroCallback callback) 
	{
		Location location = getRelativeLocation(startLocation);
		Block block = location.getBlock();
		block.setType(material);
		block.setData(data);	
		block.getState().update();
		
		if (callback != null)
		{
			callback.placeBlock(block);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON() 
	{
		JSONObject jsonAction = super.toJSON();		
		jsonAction.put("material", material.toString());
		jsonAction.put("data", Byte.toString(data));		
		return jsonAction;
	}
	
	@Override
	public void fromJSON(JSONObject jsonAction) 
	{
		super.fromJSON(jsonAction);
		
		material = Material.valueOf((String)jsonAction.get("material"));
		data = Byte.parseByte((String)jsonAction.get("data"));
	}

}
