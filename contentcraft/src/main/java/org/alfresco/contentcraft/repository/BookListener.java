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
            String name = playerEditBookEvent.getNewBookMeta().getTitle();
            String id = lore.get(0);
            Document document = (Document)CMIS.getSession().getObject(id);
            List<String> pages = playerEditBookEvent.getNewBookMeta().getPages();
            
            String pagesAsString = StringUtils.join(pages, "");
            ContentStream contentStream = new ContentStreamImpl(name, "text/plain", pagesAsString);            
            document.setContentStream(contentStream, true);
        }
    }
}
