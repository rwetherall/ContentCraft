package org.alfresco.contentcraft.command.build;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.cmis.client.AlfrescoFolder;
import org.alfresco.contentcraft.cmis.CMIS;
import org.alfresco.contentcraft.command.CommandUsageException;
import org.alfresco.contentcraft.command.macro.MacroCallback;
import org.alfresco.contentcraft.command.macro.MacroCommandExecuter;
import org.alfresco.contentcraft.command.macro.PlaceBlockMacroAction;
import org.alfresco.contentcraft.repository.Room;
import org.alfresco.contentcraft.repository.RoomType;
import org.alfresco.contentcraft.util.VectorUtil;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 * Site builder implementation.
 * 
 * @author Roy Wetherall
 * @since 1.0
 */
public class SiteBuilder implements Builder 
{
	/** macro names */
	private static final String SITE_FOLDER_FRONT = "site.folder.front";
	private static final String SITE_FOLDER_MIDDLE = "site.folder.middle";
	private static final String SITE_FOLDER_BACK = "site.folder.back";
	private static final String SITE_FOLDER_PLATFORM = "site.folder.platform";
	
	/** sign values */
	private static final int NUMBER_OF_LINES = 4;
	private static final int LINE_LEN = 15;
	
	// TODO find a better solution
	private boolean macrosLoaded = false;
	
	/**
	 * @return	{@link MacroCommandExecuter}	macro command executer
	 */
	private void loadMacros()
	{
	    if (macrosLoaded == false)
	    {
	        // load the site builder macros
	        InputStream is = getClass().getClassLoader().getResourceAsStream("site-builder.json");
	        InputStreamReader reader = new InputStreamReader(is);
	        MacroCommandExecuter.getInstance().load(reader);
	        
	        macrosLoaded = true;
	    }
	}
	
	/**
	 * @see org.alfresco.contentcraft.command.build.Builder#getName()
	 */
	public String getName() 
	{
		return "site";
	}

	/**
	 * @see org.alfresco.contentcraft.command.build.Builder#build(org.bukkit.entity.Player, org.bukkit.Location, org.bukkit.util.Vector, java.lang.String[])
	 */
	public void build(Location start, Vector direction, String... args) throws CommandUsageException
	{
		// get the site we want to use as a base
		String siteName = args[1];
		if (siteName == null || siteName.length() == 0)
		{
			throw new CommandUsageException("You didn't provide a site name!");
		}
		
		/// load the macros
		loadMacros();
		
		// grab the document lib for the site
		Session session = CMIS.connect();
		AlfrescoFolder siteRoot = CMIS.getSiteRoot(session, siteName);
		if (siteRoot == null)
		{
			throw new CommandUsageException("The site (" + siteName + ") you provided couldn't be found!");
		}	

		// build the root folders
		buildRootFolders(start, siteRoot.getChildren());			
		
	}	
	
	/**
	 * Build the root folders 
	 * 
	 * @param folderFrontLocation
	 * @param docLibTree
	 */
	private void buildRootFolders(Location start, ItemIterable<CmisObject> items)
	{
		Location startLocation = start.clone().add(VectorUtil.NORTH.clone().multiply(10));
				
		for (CmisObject item : items) 
		{
			if (item instanceof AlfrescoFolder)
			{
				// build root folder				
				buildRootFolder(startLocation, (AlfrescoFolder)item);			
				
				// move to the next folder location
				startLocation.add(VectorUtil.UP.clone().multiply(5));
			}
		}		
	}
	
	/**
	 * 
	 * @param start
	 * @param folder
	 */
	private void buildRootFolder(Location start, AlfrescoFolder folder)
	{				
		//Macro folderFrontMacro = getMacroCommand().getMacro(SITE_FOLDER_FRONT);
		//Macro folderMiddleMacro = getMacroCommand().getMacro(SITE_FOLDER_MIDDLE);
	//	Macro folderEndMacro = getMacroCommand().getMacro(SITE_FOLDER_BACK);
		//Macro folderPlatformMacro = getMacroCommand().getMacro(SITE_FOLDER_PLATFORM);
		
		Location startClone = start.clone();
		
		final String[] messages = 
		{
			folder.getName(),
			"Created By     " + folder.getPropertyValue(PropertyIds.CREATED_BY)
		};
		
		// execute the folder front macro
		MacroCommandExecuter.run(SITE_FOLDER_FRONT, startClone, new MacroCallback() 
		{
			/** sign count */
			int signCount = 0;
			
			/**
			 * @see org.alfresco.contentcraft.command.macro.MacroCallback#placeBlock(org.bukkit.block.Block)
			 */
			public void callback(String macroAction, Block block) 
			{
				if (PlaceBlockMacroAction.NAME.equals(macroAction))
				{
					// set the messages on the signs
					if (Material.WALL_SIGN.equals(block.getType()))
					{
						setSignMessage(block, messages[signCount]);
						signCount ++;
					}
				}
			}					
		});	
		
		// build the folder platform
		Location platformStart = startClone.clone().add(VectorUtil.SOUTH.clone().multiply(2));
		MacroCommandExecuter.run(SITE_FOLDER_PLATFORM, platformStart);
		
		// grab all sub-folders
		List<Folder> folders = new ArrayList<Folder>(21);
		ItemIterable<CmisObject> children = folder.getChildren();
		for (CmisObject cmisObject : children) 
		{
			if (cmisObject instanceof Folder)
			{
				// add to folder list
				folders.add((Folder)cmisObject);
			}
		}
		
		// build a middle section for each pair of folders
		Location middleClone = startClone.clone().add(VectorUtil.NORTH.clone().multiply(4));
		for (int i = 0; i < folders.size(); i++) 
		{
			Folder folder1 = folders.get(i);
			Folder folder2 = null;
			
			
			String folder2Name = "";
			String folder2CreatedBy = "";
			if (i+1 < folders.size())
			{
				folder2 = folders.get(i+1);
				folder2Name = folder2.getName();
				folder2CreatedBy = "Created By     " + folder.getPropertyValue(PropertyIds.CREATED_BY);
			}
						
			final String[] signs = 
			{
				folders.get(i).getName(),
				"Created By     " + folder.getPropertyValue(PropertyIds.CREATED_BY),
				folder2Name,
				folder2CreatedBy
			};
			
			MacroCommandExecuter.run(SITE_FOLDER_MIDDLE, middleClone, new MacroCallback() 
			{
				/** sign count */
				int signCount = 0;
				
				/**
				 * @see org.alfresco.contentcraft.command.macro.MacroCallback#placeBlock(org.bukkit.block.Block)
				 */
				public void callback(String macroAction, Block block) 
				{
					if (PlaceBlockMacroAction.NAME.equals(macroAction))
					{
						// set the messages on the signs
						if (Material.WALL_SIGN.equals(block.getType()))
						{
							setSignMessage(block, signs[signCount]);
							signCount ++;
						}
					}
				}					
			});		
						
			//buildSubFolder(folder1, middleClone, SubFolderOrinitation.LEFT);
			new Room(folder1, middleClone, RoomType.ROOM_LEFT).build();
			i++;
			if (i < folders.size())
			{
				//buildSubFolder(folder2, middleClone, SubFolderOrinitation.RIGHT);
	            new Room(folder2, middleClone, RoomType.ROOM_RIGHT).build();
			}
			middleClone.add(VectorUtil.NORTH.clone().multiply(8));
		}
		
		// build the end section
		MacroCommandExecuter.run(SITE_FOLDER_BACK, middleClone);
	}

	
	/**
	 * Divide the string over the four available lines on the given 
	 * sign.
	 * 
	 * @param block		sign block
	 * @param message	message
	 */
	private void setSignMessage(Block block, String message)
	{
		if (message != null && !message.isEmpty())
		{
			org.bukkit.block.Sign sign = (org.bukkit.block.Sign)(block.getState());	
	
			List<String> messages = split(message, NUMBER_OF_LINES, LINE_LEN);
			for (int index = 0; index < messages.size(); index++)
			{
				sign.setLine(index, messages.get(index));
			}
			
			sign.update();
		}
	}
	
	/**
	 * Helper method to split a string by pages and page size
	 * 
	 * @param message
	 * @param maxPages
	 * @param pageSize
	 * @return
	 */
	private List<String> split(String message, int maxPages, int pageSize)
	{
		List<String> result = new ArrayList<String>(maxPages);
		for (int page = 0; page < maxPages; page++) 
		{
			int startIndex = page * pageSize;
			int endIndex = (page + 1) * pageSize;
			if (endIndex < message.length())
			{		
				result.add(message.substring(startIndex, endIndex));
			}
			else 
			{
				result.add(message.substring(startIndex));
				break;
			}
		}
		return result;
	}
}
