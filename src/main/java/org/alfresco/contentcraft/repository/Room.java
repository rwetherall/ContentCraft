/**
 * 
 */
package org.alfresco.contentcraft.repository;

import java.io.Serializable;

import org.alfresco.contentcraft.command.macro.Macro;
import org.alfresco.contentcraft.command.macro.MacroCallback;
import org.alfresco.contentcraft.command.macro.MacroCommandExecuter;
import org.alfresco.contentcraft.metadata.BlockMetaData;
import org.alfresco.contentcraft.util.VectorUtil;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

/**
 * @author Roy Wetherall
 */
public class Room implements Buildable
{
    private static final String SITE_SUBFOLDER_LEFT = "site.subfolder.left";
    private static final String SITE_SUBFOLDER_RIGHT = "site.subfolder.right";
    
    private Folder folder;
    private Location start;
    private RoomType roomType;
    
    /**
     * Default constructor
     * 
     * @param folder
     * @param start
     * @param rootType
     */
    public Room(Folder folder, Location start, RoomType rootType)
    {
        this.folder = folder;
        this.start = start.clone();
        this.roomType = rootType;
    }
    
    /**
     * @see org.alfresco.contentcraft.repository.Buildable#build()
     */
	public void build() 
	{
        Macro macro = null;
        Location subFolderStart = null;
        if (RoomType.ROOM_LEFT.equals(roomType))
        {
            macro = MacroCommandExecuter.getInstance().getMacro(SITE_SUBFOLDER_LEFT);        
            subFolderStart = start.clone().add(VectorUtil.WEST);
        }
        else if (RoomType.ROOM_RIGHT.equals(roomType))
        {
            macro = MacroCommandExecuter.getInstance().getMacro(SITE_SUBFOLDER_RIGHT);   
            subFolderStart = start.clone().add(VectorUtil.EAST.clone().multiply(10));
        }
        else
        {
            throw new RuntimeException("Unsupported folder build type.");
        }
              
        macro.run(subFolderStart, new MacroCallback() 
        {
            public void callback(String macroAction, Block block) 
            {
                if (Material.CHEST.equals(block.getType()))
                {
                	//chest.setMetadata("folderId", new FixedMetadataValue(ContentCraftPlugin.getPlugin(), folder.getId()));
                	BlockMetaData.setMetadata(block, "folderId", (Serializable)folder.getId());
                }               
            }                   
        }); 
    }
}
