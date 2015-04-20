package org.alfresco.contentcraft.events.messaging;

import java.util.logging.Logger;

import org.alfresco.events.types.ContentEvent;

/**
 * Handles folder events
 * @author Gethin James
 *
 */
public class FolderEventHandler implements EventHandler<ContentEvent> {

	protected final Logger logger = Logger.getLogger(FolderEventHandler.class.getName());

	@Override
	public void handle(ContentEvent event) {
		switch (event.getType()) {
		case FOLDER_CREATE:
			logger.info("Folder created for node "+event.getNodeId());	
			break;
		case FOLDER_DELETED:
			logger.info("Folder deleted for node "+event.getNodeId());				
			break;
		default:
			logger.info("Perhaps I can't handle it after all: "+event.toString());			
			break;
		}
	}

}
