package org.alfresco.contentcraft.events.messaging;

import java.util.Map;
import java.util.logging.Logger;

import org.alfresco.events.types.Event;
import org.alfresco.listener.message.EventMessageListener;

/**
 * EventMessageListener implementation
 * 
 * @author Gethin James
 *
 */
@SuppressWarnings("rawtypes")
public class EventMessageListenerImpl implements EventMessageListener {

	protected final Logger logger = Logger.getLogger(EventMessageListenerImpl.class.getName());

	private Map<String,EventHandler> handlers;
    
	@Override
	public void onEvent(Event event) {
		if (handlers.containsKey(event.getType())){
			EventHandler<Event> aHandler = handlers.get(event.getType());
			aHandler.handle(event);
		} else {
			logger.info("Unhandled event "+event.toString());		
		}
	}

	public void setHandlers(Map<String, EventHandler> handlers) {
		this.handlers = handlers;
	}

}
