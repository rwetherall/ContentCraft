
package org.alfresco.contentcraft.api;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.alfresco.contentcraft.ContentCraftPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.alfresco.client.AlfrescoClient;
import com.alfresco.client.api.core.NodesAPI;
import com.alfresco.client.api.core.model.representation.NodeRepresentation;

import retrofit2.Response;

/**
 * Alfresco V1 REST API Helper
 * 
 * @author Roy Wetherall
 */
public class AlfrescoAPI 
{	
	private static final String ALFRESCO_CONFIG = "alfresco.json";
	private static boolean isInit = false;
	
	private static String connectionURL; 
	private static String user; 
	private static String password;
	
	private static AlfrescoClient alfrescoClient;
	
	private static void init()
	{
		ContentCraftPlugin.logger.info("Init API connection details ...");
		
		try
		{
			InputStream is = AlfrescoAPI.class.getClassLoader().getResourceAsStream(ALFRESCO_CONFIG);
			InputStreamReader reader = new InputStreamReader(is);
			try
			{					
				JSONParser jsonParser = new JSONParser();
				JSONObject json = (JSONObject)jsonParser.parse(reader);
				
				connectionURL = (String)json.get("connection-url");
				user = (String)json.get("user");
				password = (String)json.get("password");
				
				ContentCraftPlugin.logger.info("Init API connection details = " + connectionURL + "," + user + "," + password);
			}
			finally
			{
				reader.close();
				is.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	public static AlfrescoClient getClient() 
	{
		ContentCraftPlugin.logger.info("Getting API client ...");
		
	    if (alfrescoClient == null)
	    {
    		if (isInit == false)
    		{    			
    			AlfrescoAPI.init();
    			isInit = true;
    		}
    		
    		ContentCraftPlugin.logger.info("Connecting to = " + connectionURL + "," + user + "," + password);
    		alfrescoClient = new AlfrescoClient.Builder().connect(connectionURL, user, password).build();
	    }
	    
	    return alfrescoClient;
	}
	
	public static String testConnect()
	{
		String result = null;
		
		try
		{
			NodesAPI nodesAPI = getClient().getNodesAPI();
			Response<NodeRepresentation> rootFolder = nodesAPI.getNodeCall(NodesAPI.FOLDER_ROOT).execute();
			
			if (rootFolder != null)
			{
				result = "Successfully connected to " + connectionURL; 
			}
			else
			{
				result = "Unable to connect to " + connectionURL;
			}
		}
		catch (Exception exception)
		{
			ContentCraftPlugin.logger.log(Level.INFO, "Unable to connect to " + connectionURL, exception);
		}
		return result;
	}
}
