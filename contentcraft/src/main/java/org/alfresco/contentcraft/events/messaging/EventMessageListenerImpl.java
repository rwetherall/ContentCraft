package org.alfresco.contentcraft.events.messaging;

import java.util.logging.Logger;

import org.alfresco.events.types.Event;
import org.alfresco.listener.message.EventMessageListener;
import org.springframework.stereotype.Component;

/**
 * EventMessageListener implementation
 * 
 * @author Gethin James
 *
 */
public class EventMessageListenerImpl implements EventMessageListener {

	protected final Logger logger = Logger.getLogger(EventMessageListenerImpl.class.getName());

	@Override
	public void onEvent(Event event) {
		logger.info(event.toString());
	}

}
