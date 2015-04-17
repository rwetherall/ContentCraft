package org.alfresco.contentcraft.events.messaging;

import java.util.logging.Logger;

import org.alfresco.events.types.ContentEvent;

/**
 * Handles doc events
 * @author Gethin James
 *
 */
public class DocumentEventHandler implements EventHandler<ContentEvent> {

	protected final Logger logger = Logger.getLogger(DocumentEventHandler.class.getName());

	@Override
	public void handle(ContentEvent event) {
		switch (event.getType()) {
		case FILE_ADDED:
			logger.info("File created for node "+event.getNodeId());	
			break;
		case FILE_DELETED:
			logger.info("File deleted for node "+event.getNodeId());				
			break;
		default:
			logger.info("Perhaps I can't handle it after all: "+event.toString());			
			break;
		}
	}

}
