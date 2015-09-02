package org.alfresco.contentcraft.repository;

import java.util.List;

import org.alfresco.contentcraft.cmis.CMIS;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;

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
    

//	@EventHandler
//	public void onPlayerInteractBook(PlayerInteractEvent event) 
//	{
//		ItemStack book = event.getItem();
//		
//		if (book != null && book.getType().equals(Material.WRITTEN_BOOK))
//	    {
//		
////		if (event.hasItem()) 
////		{
////			Material m = event.getMaterial();
////			if (m == Material.BOOK || m == Material.BOOK_AND_QUILL) 
////			{
//				ContentCraftPlugin.logger.info("Player [" + event.getPlayer().getPlayerListName() + "] accessing [" + Material.WRITTEN_BOOK + "]");
//
//				
//				BookMeta bookMeta = (BookMeta) book.getItemMeta();
//				
//				ContentCraftPlugin.logger.info("Player [" + event.getPlayer().getPlayerListName() + "] accessing [" + bookMeta.getTitle() + "]");
//
//				String id = bookMeta.getTitle();
//				Document document = (Document)CMIS.getSession().getObject(id);
//				String content = CommonUtil.getContentAsString(document.getContentStream());
//				List<String> pages = CommonUtil.split(content, 16, 265);
//				bookMeta.setPages(pages);
//
//				book.setItemMeta(bookMeta);
//			}
//	//	}
//	}	
}
