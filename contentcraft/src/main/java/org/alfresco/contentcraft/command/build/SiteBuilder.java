package org.alfresco.contentcraft.command.build;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.cmis.client.AlfrescoFolder;
import org.alfresco.contentcraft.ContentCraftPlugin;
import org.alfresco.contentcraft.cmis.CMIS;
import org.alfresco.contentcraft.command.CommandUsageException;
import org.alfresco.contentcraft.command.macro.Macro;
import org.alfresco.contentcraft.command.macro.MacroCallback;
import org.alfresco.contentcraft.command.macro.MacroCommandExecuter;
import org.alfresco.contentcraft.command.macro.PlaceBlockMacroAction;
import org.alfresco.contentcraft.util.VectorUtil;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
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
	private static final String SITE_SUBFOLDER_LEFT = "site.subfolder.left";
	private static final String SITE_SUBFOLDER_RIGHT = "site.subfolder.right";
	
	/** sign values */
	private static final int NUMBER_OF_LINES = 4;
	private static final int LINE_LEN = 15;
	
	/** macro command */
	private MacroCommandExecuter macroCommand;
	
	/**
	 * @return	{@link MacroCommandExecuter}	macro command executer
	 */
	private MacroCommandExecuter getMacroCommand()
	{
		if (macroCommand == null)
		{
			// get the macro command
			macroCommand = (MacroCommandExecuter)ContentCraftPlugin.getPlugin().getCommand("macro").getExecutor();
			
			// load the site builder macros
			InputStream is = getClass().getClassLoader().getResourceAsStream("site-builder.json");
			InputStreamReader reader = new InputStreamReader(is);
			macroCommand.load(reader);
		}
		
		return macroCommand;
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
		Macro folderFrontMacro = getMacroCommand().getMacro(SITE_FOLDER_FRONT);
		Macro folderMiddleMacro = getMacroCommand().getMacro(SITE_FOLDER_MIDDLE);
		Macro folderEndMacro = getMacroCommand().getMacro(SITE_FOLDER_BACK);
		Macro folderPlatformMacro = getMacroCommand().getMacro(SITE_FOLDER_PLATFORM);
		
		Location startClone = start.clone();
		
		final String[] messages = 
		{
			folder.getName(),
			"Created By     " + folder.getPropertyValue(PropertyIds.CREATED_BY)
		};
		
		// execute the folder front macro
		folderFrontMacro.run(startClone, new MacroCallback() 
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
		folderPlatformMacro.run(platformStart);
		
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
			
			folderMiddleMacro.run(middleClone, new MacroCallback() 
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
						
			buildSubFolder(folder1, middleClone, SubFolderOrinitation.LEFT);
			i++;
			if (i < folders.size())
			{
				buildSubFolder(folder2, middleClone, SubFolderOrinitation.RIGHT);
			}
			middleClone.add(VectorUtil.NORTH.clone().multiply(8));
		}
		
		// build the end section
		folderEndMacro.run(middleClone);
	}
	
	private enum SubFolderOrinitation
	{
		LEFT,
		RIGHT
	}
	
	private List<Chest> chests;
	
	private void buildSubFolder(Folder folder, Location start, SubFolderOrinitation folderOrinitation)
	{
		Macro macro = null;
		Location subFolderStart = null;
		if (SubFolderOrinitation.LEFT.equals(folderOrinitation))
		{
			macro = getMacroCommand().getMacro(SITE_SUBFOLDER_LEFT);		
			subFolderStart = start.clone().add(VectorUtil.WEST);
		}
		else
		{
			macro = getMacroCommand().getMacro(SITE_SUBFOLDER_RIGHT);	
			subFolderStart = start.clone().add(VectorUtil.EAST.clone().multiply(10));
		}
		
		chests = new ArrayList<Chest>();		
		macro.run(subFolderStart, new MacroCallback() 
		{
			/**
			 * @see org.alfresco.contentcraft.command.macro.MacroCallback#placeBlock(org.bukkit.block.Block)
			 */
			public void callback(String macroAction, Block block) 
			{
				if (Material.CHEST.equals(block.getType()))
				{
					chests.add((Chest)(block.getState()));
				}				
			}					
		});	
		
		// TODO get the next chest with room
		Chest chest = chests.get(0);
		
		int i = 0;
		for (CmisObject cmisObject : folder.getChildren())
		{
			if (cmisObject instanceof Document)
			{
				chest.getInventory().setItem(i, getBook((Document)cmisObject));
				i++;
			}			
		}
	}
	
	/**
	 * Divde the string over the four available lines on the given 
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
			
			for (int line = 0; line < NUMBER_OF_LINES; line++) 
			{
				int startIndex = line * LINE_LEN;
				int endIndex = (line + 1) * LINE_LEN;
				if (endIndex < message.length())
				{		
					sign.setLine(line, message.substring(startIndex, endIndex));
				}
				else 
				{
					sign.setLine(line,  message.substring(startIndex));
					break;
				}
			}
			
			sign.update();
		}
	}
	
	public ItemStack getBook(Document document)
    {	
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		
		BookMeta bookMeta = (BookMeta)book.getItemMeta();
		
		bookMeta.setTitle(document.getName());
		bookMeta.setAuthor((String)document.getPropertyValue(PropertyIds.CREATED_BY));
		
		String content = getContentAsString(document); //.replace("\r", "");	
		
		List<String> pages = new ArrayList<String>();
		pages.add(content.substring(0, 265));
		
		//String rest = content;		
		//while (rest.length() > 266) 
		//{
		//	pages.add(content.substring(0, 265));
		//	rest = content.substring(265);
		//	
		//	System.out.println(rest);
		//}
		
		//if (!rest.isEmpty())
		//{
		//	pages.add(rest);
		//}
		
		bookMeta.setPages(pages);		
		book.setItemMeta(bookMeta);
		
		return book;
    }
	
	/**
	 * Helper method to get the contents of a stream
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private String getContentAsString(Document document)
	{
	    StringBuilder sb = new StringBuilder();
		ContentStream stream = document.getContentStream();
		
		try
		{
		    Reader reader = new InputStreamReader(stream.getStream(), "UTF-8");	
		    try 
		    {
		        final char[] buffer = new char[4 * 1024];
		        int b;
		        while (true) 
		        {
		            b = reader.read(buffer, 0, buffer.length);
		            if (b > 0) 
		            {
		                sb.append(buffer, 0, b);
		            } 
		            else if (b == -1) 
		            {
		                break;
		            }
		        }
		    } 
		    finally 
		    {
		        reader.close();
		    }
		}
		catch (IOException exception)
		{
			System.out.println("Unable to read content.  " + exception.getMessage());
		}

	    return sb.toString();
	}
	
}
