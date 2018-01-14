package org.alfresco.contentcraft.repository;

import java.util.List;
import java.util.logging.Level;

import org.alfresco.contentcraft.ContentCraftPlugin;
import org.alfresco.contentcraft.cmis.CMIS;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;

public class BookListener implements Listener
{
    @EventHandler
    public void playerEditBookEvent(PlayerEditBookEvent playerEditBookEvent)
    {
		ContentCraftPlugin.logger.log(Level.INFO, "onPlayerEditBookEvent");
	
	    // get the book meta-data
		BookMeta book = playerEditBookEvent.getPreviousBookMeta();
		BookMeta newBook = playerEditBookEvent.getNewBookMeta();
	
		// get the name and id of the document
        String id = book.getLocalizedName();
        String name = book.getDisplayName();      
        String author = book.getAuthor();
        
        ContentCraftPlugin.logger.info("Got document meta-data - " + id + "," + name + "," + author);
        
        // get the pages from the book
        List<String> pages = newBook.getPages();            
        String pagesAsString = StringUtils.join(pages, "");
        
        ContentCraftPlugin.logger.info("Sending updated pages .. " + pagesAsString);
        
        // update the document with the new text
        Document document = (Document)CMIS.getSession().getObject(id);            
        ContentStream contentStream = new ContentStreamImpl(name, "text/plain", pagesAsString);            
        document.setContentStream(contentStream, true);
                
        newBook.setLocalizedName(id);
        newBook.setDisplayName(name);
        newBook.setAuthor(author);
        
        playerEditBookEvent.setNewBookMeta(newBook);

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
