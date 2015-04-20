package org.alfresco.contentcraft;

import junit.framework.Assert;

import org.alfresco.contentcraft.events.Listener;
import org.alfresco.events.test.EventFactory;
import org.alfresco.events.types.Event;
import org.alfresco.listener.message.EventMessageListener;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * Tests bootstrap
 * 
 * @author Gethin James
 *
 */
public class AppBootTest {

	static ApplicationContext context;
	
	@BeforeClass
	public static void setup() {
		AppBootstrap app = new AppBootstrap(java.util.logging.Logger.getLogger(AppBootstrap.class.getName()));	
		context = app.bootstrapSpring();
	}
	
	@Test
	public void testConnect()
	{
		Assert.assertNotNull(context);
		Listener listener = (Listener) context.getBean("listen");
		Assert.assertNotNull(listener);		
		listener.connect();
	}
	
	@Test
	public void testFolderEvent()
	{
		EventMessageListener eventListener = (EventMessageListener) context.getBean("eventListener");
		Event e = EventFactory.createActivityEvent("org.alfresco.documentlibrary.folder-added", "roy", "nodeI", "gr8t", null, null);
		eventListener.onEvent(e);
		e = EventFactory.createActivityEvent("org.alfresco.documentlibrary.folder-deleted", "roy", "nodeI", "gr8t", null, null);
		eventListener.onEvent(e);
		e = EventFactory.createActivityEvent("NOT HANDLED", "roy", "nodeI", "gr8t", null, null);
		eventListener.onEvent(e);
	}
	
	@Test
	public void testDocEvent()
	{
		EventMessageListener eventListener = (EventMessageListener) context.getBean("eventListener");
		Event e = EventFactory.createActivityEvent("org.alfresco.documentlibrary.file-added", "roy", "nodeI", "gr8t", "my.doc", null);
		eventListener.onEvent(e);
		e = EventFactory.createActivityEvent("org.alfresco.documentlibrary.file-deleted", "roy", "nodeI", "gr8t", "your.doc", null);
		eventListener.onEvent(e);
		e = EventFactory.createActivityEvent("NOT HANDLED AT ALL", "roy", "nodeI", "gr8t", null, null);
		eventListener.onEvent(e);
	}
	
	@AfterClass
	public static void cleanup() {
		if (context!= null){
			Listener listener = (Listener) context.getBean("listen");
			 if (listener!= null){
					listener.disconnect();				 
			 }
		}	
	}
}
