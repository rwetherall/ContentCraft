/**
 * 
 */
package org.alfresco.contentcraft.cmis;

import junit.framework.Assert;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.junit.Test;

/**
 * @author Roy Wetherall
 */
public class CMISUnitTest 
{
	@Test
	public void getDocumentLibFolderForASite()
	{
		Session session = CMIS.getSession();
		
		Folder rootFolder = session.getRootFolder();
		System.out.println(rootFolder.getPath());		
		
	    Folder folder = CMIS.getSiteRoot(session, "test");
	    Assert.assertNotNull(folder);
	    System.out.println(folder.getName());	    
	}
}
