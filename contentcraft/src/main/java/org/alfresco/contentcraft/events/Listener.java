package org.alfresco.contentcraft.events;

import java.util.logging.Logger;

/*
 * Listens for events
 * 
 * @author Gethin James 
 */
public class Listener {

	private Logger logger;
	
	public Listener(Logger logger) {
		super();
		this.logger = logger;
	}

	public boolean connect(String url, String topic) {
		logger.info("Connecting to "+url+ " with topic "+topic);
		return true;
	}
	
	public boolean disconnect() {
		logger.info("Ok disconnecting");
		return true;		
	}
}
