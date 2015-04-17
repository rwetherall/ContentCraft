package org.alfresco.contentcraft.events.messaging;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.alfresco.events.types.Event;
import org.alfresco.listener.message.EventMessageListener;
import org.springframework.beans.factory.InitializingBean;

import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Listens for a message sent via JMS, turns it back into a Java Event Object.
 * 
 * @author Gethin James
 *
 */
public class JMSMessageListener implements MessageListener, InitializingBean {

	protected final Logger logger = Logger.getLogger(JMSMessageListener.class.getName());
	
    public JMSMessageListener() {
		super();
		// TODO Auto-generated constructor stub
	}

	Marshaller marshaller = new Marshaller();
	
    EventMessageListener listener;
    
	public void setListener(EventMessageListener listener) {
		this.listener = listener;
	}

	@Override
	public void onMessage(Message message) {
		
		if (logger.isLoggable(Level.FINE))
		{
	        StringBuilder builder = new StringBuilder();
	        try
            {
                builder.append("JMSMessage received [JMSMessageID=").append(message.getJMSMessageID())
                .append(", JMSRedelivered=").append(message.getJMSRedelivered())
                .append(", JMSCorrelationID=").append(message.getJMSCorrelationID())
                .append(", JMSDestination=").append(message.getJMSDestination())
                .append(", JMSPriority=").append(message.getJMSPriority());
                
                for (Enumeration<?> e = message.getPropertyNames(); e.hasMoreElements();)
                {
                    String key = e.nextElement().toString();
                    builder.append(", ").append(key+":").append(message.getObjectProperty(key));          
                }

                builder.append("]");
                logger.fine(builder.toString());
            }
            catch (JMSException error)
            {
                logger.fine("Unable to debug the jmsmessage due to "+error.getMessage());
            }
		}
		
        if (message instanceof TextMessage)
        {
        	Event event = null;
        	
			try {
				String body = ((TextMessage) message).getText();
				Object b =  marshaller.unmarshal(body);
				if (b instanceof Event)
				{
					event = (Event) b;
				}
				else
				{
					error(message, "Unable to convert the class to an event: "+b, null);			
				}
			} catch(JsonMappingException jme) {
	        	error(message, "Unable to convert the message to JSON", jme);
			} catch (Exception e) {
	        	error(message, "Unable to parse and convert the message", e);
			}
			
			if (event!=null)
			{
				//process the event
				listener.onEvent(event);			
			}

        }
        else
        {
        	error(message, "Invalid message, its not a text message", null);
        }

	}

    private void error(Message message, String text, Throwable t) {
    	logger.info(text);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("Using Listener: "+listener.getClass().getName());
	}

	protected EventMessageListener getListener() {
		return listener;
	}

}
