
package org.alfresco.contentcraft.cmis;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.cmis.client.AlfrescoFolder;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Repository;
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
	private static final String CMIS_CONFIG_FILE = "alfresco.json";
	private static boolean isInit = false;
	
	private static String alfrescoAtomPubURL; 
	private static String user; 
	private static String password;
	
	private static Session session;
	
	private static void init()
	{
		try
		{
			InputStream is = CMIS.class.getClassLoader().getResourceAsStream(CMIS_CONFIG_FILE);
			InputStreamReader reader = new InputStreamReader(is);
					
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonCMIS = (JSONObject)jsonParser.parse(reader);
			
			alfrescoAtomPubURL = (String)jsonCMIS.get("connection-url") + "/api/-default-/public/cmis/versions/1.0/atom";
			user = (String)jsonCMIS.get("user");
			password = (String)jsonCMIS.get("password");
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
    		Map<String, String> parameters = new HashMap<String, String>();
    		
    		// User credentials.
    		parameters.put(SessionParameter.USER, user);
    		parameters.put(SessionParameter.PASSWORD, password);

    		// Connection settings.
    		parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
    		parameters.put(SessionParameter.ATOMPUB_URL, alfrescoAtomPubURL); // URL to your CMIS server.
    		parameters.put(SessionParameter.AUTH_HTTP_BASIC, "true" );
    		parameters.put(SessionParameter.COOKIES, "true" );
    		parameters.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");

    		// Create session.
    		// Alfresco only provides one repository.
    		Repository repository = sessionFactory.getRepositories(parameters).get(0);
    		session = repository.createSession();
	    }
	    
	    return session;
	}
	
	public static String testConnect()
	{
		String result = "Unable to connect to " + alfrescoAtomPubURL;
		Session session = CMIS.getSession();		
		Folder rootFolder = session.getRootFolder();
		if (rootFolder != null)
		{
			result = "Successfully connected to " + alfrescoAtomPubURL; 
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
