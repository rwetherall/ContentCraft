package org.alfresco.contentcraft.repository;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.contentcraft.cmis.CMIS;
import org.alfresco.contentcraft.util.CommonUtil;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.alfresco.contentcraft.ContentCraftPlugin;

public class BookListener implements Listener
{
    @EventHandler
    public void playerEditBookEvent(PlayerEditBookEvent playerEditBookEvent)
    {
        List<String> lore = playerEditBookEvent.getNewBookMeta().getLore();
        if (!lore.isEmpty())
        {
            String id = playerEditBookEvent.getNewBookMeta().getTitle();
            String name = lore.get(0);
            Document document = (Document)CMIS.getSession().getObject(id);
            List<String> pages = playerEditBookEvent.getNewBookMeta().getPages();
            
            String pagesAsString = StringUtils.join(pages, "");
            ContentStream contentStream = new ContentStreamImpl(name, "text/plain", pagesAsString);            
            document.setContentStream(contentStream, true);
        }
    }

		@EventHandler
		public void onPlayerInteractBook(PlayerInteractEvent event) {
			if (event.hasItem()) {
				Material m = event.getMaterial();
				if (m == Material.BOOK || m == Material.BOOK_AND_QUILL) {
					ContentCraftPlugin.logger.info("Player [" + event.getPlayer().getPlayerListName() + "] accessing [" + m + "]");

					ItemStack book = event.getItem();
					BookMeta bookMeta = (BookMeta) book.getItemMeta();
					
					ContentCraftPlugin.logger.info("Player [" + event.getPlayer().getPlayerListName() + "] accessing [" + bookMeta.getTitle() + "]");

					String id = bookMeta.getTitle();
					Document document = (Document)CMIS.getSession().getObject(id);
					String content = CommonUtil.getContentAsString(document.getContentStream());
					List<String> pages = CommonUtil.split(content, 16, 265);
					bookMeta.setPages(pages);

					book.setItemMeta(bookMeta);
				}
			}
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
