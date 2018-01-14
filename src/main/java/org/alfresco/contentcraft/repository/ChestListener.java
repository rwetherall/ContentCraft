package org.alfresco.contentcraft.repository;

import java.util.Collections;
import java.util.List;

import org.alfresco.contentcraft.ContentCraftPlugin;
import org.alfresco.contentcraft.cmis.CMIS;
import org.alfresco.contentcraft.metadata.BlockMetaData;
import org.alfresco.contentcraft.util.CommonUtil;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class ChestListener implements Listener
{
	@EventHandler
	public void onInventoryOpenEvent(InventoryOpenEvent event) 
	{
		InventoryType t = event.getInventory().getType();
		if (InventoryType.CHEST.equals(t))
		{
			ContentCraftPlugin.logger.info("Player [" + event.getPlayer().getName() + "] accessing [" + t + "]");
			
			// either side of the chest will provide the required meta-data
			DoubleChest doubleChest = (DoubleChest)event.getInventory().getHolder();
			Chest chest = (Chest)doubleChest.getLeftSide();	
			Block block = chest.getBlock();
			
			if (BlockMetaData.hasMetadata(block, "folderId"))
			{
				// clear inventory
				event.getInventory().clear();
				
				// get the folder
				String folderId = (String)BlockMetaData.getMetadata(block, "folderId");
				Folder folder = (Folder)CMIS.getSession().getObject(folderId);
				
				ContentCraftPlugin.logger.info("Filling chest with documents from folder [" + folderId + "]");
				
				// add the documents to the chest
				int i = 0;
		        for (CmisObject cmisObject : folder.getChildren())
		        {
		            if (cmisObject instanceof Document)
		            {
		            	Document document = (Document)cmisObject;
		                event.getInventory().setItem(i, getBook(document));
		                i++;
		            }           
		        }
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
        
        String id = document.getId();
        String author = (String)document.getPropertyValue(PropertyIds.CREATED_BY);
        String name = document.getName();
        
        ContentCraftPlugin.logger.info("Setting document meta-data - " + id + "," + name + "," + author);
        
        bookMeta.setDisplayName(name);        
        bookMeta.setAuthor(author);        
        bookMeta.setLocalizedName(id);
        
        String content = CommonUtil.getContentAsString(document.getContentStream());  
        List<String> pages = CommonUtil.split(content, 16, 265);
        bookMeta.setPages(pages);   
        
        book.setItemMeta(bookMeta);
        
        return book;
    }

/*
		@EventHandler
		public void onInventoryMoveItem(InventoryMoveItemEvent event) {
			ContentCraftPlugin.logger.info("Player moving item");
			Material m = event.getItem().getType();
			if (m == Material.BOOK || m == Material.BOOK_AND_QUILL) {
				ContentCraftPlugin.logger.info("Player moving book");
				ContentCraftPlugin.logger.info("Moving from: " + event.getSource().getName());
				ContentCraftPlugin.logger.info("Moving to: " + event.getDestination().getName());
				ItemStack book = event.getItem();
				BookMeta bookMeta = (BookMeta) book.getItemMeta();
				List<String> pages = new ArrayList<String>();
				bookMeta.setPages(pages);
				book.setItemMeta(bookMeta);
			}
		}

		@EventHandler
		public void onInventoryPickupItemEvent(InventoryPickupItemEvent e) {
			ContentCraftPlugin.logger.info("Inventory PickupItem Event");
		}

		@EventHandler
		public void onInventoryDragEvent(InventoryDragEvent e) {
			ContentCraftPlugin.logger.info("Inventory Drag Event");
		}

		@EventHandler
		public void onInventoryCloseEvent(InventoryCloseEvent e) {
			ContentCraftPlugin.logger.info("Inventory Close Event");
		}

		@EventHandler
		public void onInventoryClickEvent(InventoryClickEvent e) {
			ContentCraftPlugin.logger.info("Inventory Click Event");
		}

		@EventHandler
		public void onInventoryOpenEvent(InventoryOpenEvent e) {
			InventoryType t = e.getInventory().getType();
			InventoryView v = e.getView();

			ContentCraftPlugin.logger.info("Type is: " + t);
		}
*/
}
