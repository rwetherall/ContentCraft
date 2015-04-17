package org.alfresco.contentcraft;

import org.junit.Test;

/**
 * Tests bootstrap
 * 
 * @author Gethin James
 *
 */
public class AppBootTest {

	@Test
	public void testSprintBootstrap()
	{
		AppBootstrap app = new AppBootstrap(java.util.logging.Logger.getLogger(AppBootstrap.class.getName()));
		app.bootstrapSpring();
	}
}
