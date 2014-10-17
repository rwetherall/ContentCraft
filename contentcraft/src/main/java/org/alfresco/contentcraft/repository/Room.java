/**
 * 
 */
package org.alfresco.contentcraft.repository;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.contentcraft.command.macro.Macro;
import org.alfresco.contentcraft.command.macro.MacroCallback;
import org.alfresco.contentcraft.command.macro.MacroCommandExecuter;
import org.alfresco.contentcraft.util.CommonUtil;
import org.alfresco.contentcraft.util.VectorUtil;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

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
    private List<Chest> chests;
    
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
        
        chests = new ArrayList<Chest>();        
        macro.run(subFolderStart, new MacroCallback() 
        {
            public void callback(String macroAction, Block block) 
            {
                if (Material.CHEST.equals(block.getType()))
                {
                    chests.add((Chest)(block.getState()));
                }               
            }                   
        }); 
        
        // TODO get the next chest with room
        Chest chest = chests.get(0);
        
        int i = 0;
        for (CmisObject cmisObject : folder.getChildren())
        {
            if (cmisObject instanceof Document)
            {
                chest.getInventory().setItem(i, getBook((Document)cmisObject));
                i++;
            }           
        }
    }
	
	/**
     * Get book
     * 
     * @param document
     * @return
     */
    private ItemStack getBook(Document document)
    {   
        ItemStack book = new ItemStack(Material.BOOK_AND_QUILL);
        
        BookMeta bookMeta = (BookMeta)book.getItemMeta();
        
        bookMeta.setLore(Collections.singletonList(document.getName()));
        bookMeta.setAuthor((String)document.getPropertyValue(PropertyIds.CREATED_BY));        
        bookMeta.setTitle(document.getId());
        
//        String content = CommonUtil.getContentAsString(document.getContentStream());  
 //       List<String> pages = CommonUtil.split(content, 16, 265);
  //      bookMeta.setPages(pages);   
        
        book.setItemMeta(bookMeta);
        return book;
    }
    

}
