/**
 * 
 */
package org.alfresco.contentcraft.cmis;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

/**
 * @author Roy Wetherall
 */
public class CMIS 
{
	private static final String ALFRSCO_ATOMPUB_URL = "http://localhost:8080/alfresco/service/cmis";
	private static final String REPOSITORY_ID = "cc6265b4-ba96-4289-b5c4-5a9ab77f8999";
	private static final String USER = "admin";
	private static final String PASSWORD = "admin";
	
	public static Session connect() 
	{
		SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put(SessionParameter.USER, USER);
		parameter.put(SessionParameter.PASSWORD, PASSWORD);
		parameter.put(SessionParameter.ATOMPUB_URL, ALFRSCO_ATOMPUB_URL);
		parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
		parameter.put(SessionParameter.REPOSITORY_ID, REPOSITORY_ID);
		
		return sessionFactory.createSession(parameter);
	}
	
	public static Folder getSiteRoot(Session session, String siteName)
	{
		String docLibPath = "/sites/" + siteName + "/documentLibrary";
		
		CmisObject folder = session.getObjectByPath(docLibPath);
		if (!(folder instanceof Folder))
		{
			throw new RuntimeException("Unable to get site root for site '" + siteName + "'");
		}
		
		return (Folder)folder;
	}

}
