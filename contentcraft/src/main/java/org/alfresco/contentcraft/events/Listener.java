package org.alfresco.contentcraft.events;

import java.util.logging.Logger;

import javax.jms.ConnectionFactory;

import org.alfresco.contentcraft.events.messaging.JMSMessageListener;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.util.ErrorHandler;

/*
 * Listens for events
 * 
 * @author Gethin James 
 */
public class Listener {

	private Logger logger = Logger.getLogger(Listener.class.getName());

	public Listener() {
		super();
	}

	private String topic;
	private String clientUniqueId;
	private ConnectionFactory connectionFactory;
	private JMSMessageListener listener;
	private DefaultMessageListenerContainer container;
	
	public void setTopic(String topic) {
		this.topic = topic;
	}

	public void setClientUniqueId(String clientUniqueId) {
		this.clientUniqueId = clientUniqueId;
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public void setListener(JMSMessageListener listener) {
		this.listener = listener;
	}

	public boolean connect() {
		logger.info("Connecting to: "+ topic);
		container = getContainer();
		container.initialize();
		container.start();
		return true;
	}
	
	public boolean disconnect() {
		logger.info("Ok disconnecting");
		return true;		
	}
	
	private DefaultMessageListenerContainer getContainer() {
		DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
		container.setMessageListener(listener);
		container.setConnectionFactory(connectionFactory);

		// set topic
		container.setDestinationName(topic);
		container.setPubSubDomain(true);
		container.setDurableSubscriptionName(clientUniqueId);
		container.setClientId(clientUniqueId);
		container.setSubscriptionDurable(false);

		container.setSessionTransacted(true);
		container.setErrorHandler(new ErrorHandler() {

			@Override
			public void handleError(Throwable t) {
				logger.warning("Message error" + t);
			}

		});
		return container;
	}
}
