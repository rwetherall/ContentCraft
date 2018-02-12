package org.alfresco.contentcraft.rest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class REST {
	
	private static final String REST_CONFIG_FILE = "rest.json";
	private static boolean isInit = false;

	private static String alfrescoURL; 
	private static String user; 
	private static String password;
	
	private static void init()
	{
		try
		{
			InputStream is = REST.class.getClassLoader().getResourceAsStream(REST_CONFIG_FILE);
			InputStreamReader reader = new InputStreamReader(is);
					
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonCMIS = (JSONObject)jsonParser.parse(reader);
			
			alfrescoURL = (String)jsonCMIS.get("connection-url");
			user = (String)jsonCMIS.get("user");
			password = (String)jsonCMIS.get("password");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	public static String getTicket() throws Exception {
		
		if (isInit == false)
		{
			System.out.println("Init connection details.");
			REST.init();
			isInit = true;
		}
		
		// HTTP request
		JSONObject au = new JSONObject();
		au.put("username", user);
		au.put("password", password);

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = 
				new HttpPost(alfrescoURL + "/s/api/login");

		StringEntity input = new StringEntity(au.toJSONString());
		input.setContentType("application/json");
		postRequest.setEntity(input);

		HttpResponse response = httpClient.execute(postRequest);
		
		// HTTP response

		JSONParser jsonParser = new JSONParser();
		JSONObject ticket = 
				(JSONObject) jsonParser.parse(new InputStreamReader(response.getEntity().getContent()));
		JSONObject data = (JSONObject) ticket.get("data");
		String alfrescoTicket = data.get("ticket").toString();
		
		httpClient.getConnectionManager().shutdown();
		
		return alfrescoTicket;
		
	}
	
	public static List<String> getMembers(String siteId, String alfrescoTicket) throws Exception {
		
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
		httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
		HttpGet getRequest = 
				new HttpGet(alfrescoURL + "/s/api/sites/test/memberships?alf_ticket=" + alfrescoTicket);

		HttpResponse response = httpClient.execute(getRequest);
		JSONParser jsonParser = new JSONParser();
		JSONArray body = 
		    (JSONArray) jsonParser.parse(new InputStreamReader(response.getEntity().getContent()));
		
		List<String> members = new ArrayList<String>();
		for (Object element : body) {
			JSONObject member = (JSONObject) element;
			JSONObject authority = (JSONObject) member.get("authority");
			members.add(authority.get("userName").toString());
		}
		
		return members;
	}
	
	public static String testConnect() 
	{
		String result = "Unable to connect to " + alfrescoURL;
		try {
			String ticket = REST.getTicket();		
			if (ticket != null)
			{
				result = "Successfully connected to " + alfrescoURL; 
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return result;
	}

}
