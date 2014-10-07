
package org.alfresco.contentcraft.cmis;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.cmis.client.AlfrescoFolder;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * CMIS helper implementation.
 * 
 * @author Roy Wetherall
 */
public class CMIS 
{	
	private static final String CMIS_CONFIG_FILE = "cmis.json";
	private static boolean isInit = false;
	
	private static String ALFRSCO_ATOMPUB_URL; // = "http://localhost:8080/alfresco/service/cmis";
	private static String REPOSITORY_ID; // = "cc6265b4-ba96-4289-b5c4-5a9ab77f8999";
	private static String USER; // = "admin";
	private static String PASSWORD; // = "admin";
	
	private static Session session;
	
	private static void init()
	{
		try
		{
			InputStream is = CMIS.class.getClassLoader().getResourceAsStream(CMIS_CONFIG_FILE);
			InputStreamReader reader = new InputStreamReader(is);
					
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonCMIS = (JSONObject)jsonParser.parse(reader);
			
			CMIS.ALFRSCO_ATOMPUB_URL = (String)jsonCMIS.get("connection-url");
			CMIS.REPOSITORY_ID = (String)jsonCMIS.get("repository-id");
			CMIS.USER = (String)jsonCMIS.get("user");
			CMIS.PASSWORD = (String)jsonCMIS.get("password");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	public static Session getSession() 
	{
	    if (session == null)
	    {
    		if (isInit == false)
    		{
    			System.out.println("Init connection details.");
    			CMIS.init();
    			isInit = true;
    		}
    		
    		SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
    		Map<String, String> parameter = new HashMap<String, String>();
    		parameter.put(SessionParameter.USER, USER);
    		parameter.put(SessionParameter.PASSWORD, PASSWORD);
    		parameter.put(SessionParameter.ATOMPUB_URL, ALFRSCO_ATOMPUB_URL);
    		parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
    		parameter.put(SessionParameter.REPOSITORY_ID, REPOSITORY_ID);
    		parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");
    		
    		session = sessionFactory.createSession(parameter);
	    }
	    
	    return session;
	}
	
	public static String testConnect()
	{
		String result = "Unable to connect to " + ALFRSCO_ATOMPUB_URL;
		Session session = CMIS.getSession();		
		Folder rootFolder = session.getRootFolder();
		if (rootFolder != null)
		{
			result = "Successfully connected to " + ALFRSCO_ATOMPUB_URL; 
		}
		return result;
	}
	
	public static AlfrescoFolder getSiteRoot(Session session, String siteName)
	{
		String docLibPath = "/sites/" + siteName + "/documentLibrary";
		
		CmisObject folder = session.getObjectByPath(docLibPath);
		if (!(folder instanceof AlfrescoFolder))
		{
			throw new RuntimeException("Unable to get site root for site '" + siteName + "'");
		}
		
		return (AlfrescoFolder)folder;
	}

}
