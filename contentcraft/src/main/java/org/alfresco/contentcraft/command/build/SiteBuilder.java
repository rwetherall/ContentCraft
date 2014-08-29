package org.alfresco.contentcraft.command.build;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.contentcraft.ContentCraftPlugin;
import org.alfresco.contentcraft.cmis.CMIS;
import org.alfresco.contentcraft.command.CommandUsageException;
import org.alfresco.contentcraft.command.macro.Macro;
import org.alfresco.contentcraft.command.macro.MacroCallback;
import org.alfresco.contentcraft.command.macro.MacroCommandExecuter;
import org.alfresco.contentcraft.command.macro.PlaceBlockMacroAction;
import org.alfresco.contentcraft.util.VectorUtil;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Session;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
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
	public void build(Player player, Location start, Vector direction, String... args) throws CommandUsageException
	{
		// get the site we want to use as a base
		String siteName = args[1];
		if (siteName == null || siteName.length() == 0)
		{
			throw new CommandUsageException("You didn't provide a site name!");
		}
		
		// grab the document lib for the site
		Session session = CMIS.connect();
		Folder siteRoot = CMIS.getSiteRoot(session, siteName);
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
		Location startLocation = start.clone();
				
		for (CmisObject item : items) 
		{
			if (item instanceof Folder)
			{
				// build root folder				
				buildRootFolder(startLocation, (Folder)item);			
				
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
	private void buildRootFolder(Location start, Folder folder)
	{				
		Macro folderFrontMacro = getMacroCommand().getMacro(SITE_FOLDER_FRONT);
		Macro folderMiddleMacro = getMacroCommand().getMacro(SITE_FOLDER_MIDDLE);
		Macro folderEndMacro = getMacroCommand().getMacro(SITE_FOLDER_BACK);
		Macro folderPlatformMacro = getMacroCommand().getMacro(SITE_FOLDER_PLATFORM);
		Macro subFolderLeft = getMacroCommand().getMacro(SITE_SUBFOLDER_LEFT);
		Macro subFolderRight = getMacroCommand().getMacro(SITE_SUBFOLDER_RIGHT);
		
		Location startClone = start.clone();
		
		final String[] messages = 
		{
			folder.getName(),
			folder.getDescription()
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
			folderMiddleMacro.run(middleClone);			
			subFolderLeft.run(middleClone.clone().add(VectorUtil.WEST));
			i++;
			if (i < folders.size())
			{
				subFolderRight.run(middleClone.clone().add(VectorUtil.EAST.clone().multiply(10)));
			}
			middleClone.add(VectorUtil.NORTH.clone().multiply(8));
		}
		
		// build the end section
		folderEndMacro.run(middleClone);
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
	
	// NOTES
	
	// chest .. Block->Chest.getInventry()
	
	// get information from a book
	
//	NBTTagCompound bookData = ((CraftItemStack) bookItem).getHandle().tag;
//    
//    this.author = bookData.getString("author");
//    this.title = bookData.getString("title");
//            
//    NBTTagList nPages = bookData.getList("pages");
//
//    String[] sPages = new String[nPages.size()];
//    for(int i = 0;i<nPages.size();i++)
//    {
//        sPages[i] = nPages.get(i).toString();
//    }
            

	// create a new book
	
//    CraftItemStack newbook = new CraftItemStack(Material.WRITTEN_BOOK);
//    
//    NBTTagCompound newBookData = new NBTTagCompound();
//    
//    newBookData.setString("author",author);
//    newBookData.setString("title",title);
//            
//    NBTTagList nPages = new NBTTagList();
//    for(int i = 0;i<pages.length;i++)
//    {  
//        nPages.add(new NBTTagString(pages[i],pages[i]));
//    }
//    
//    newBookData.set("pages", nPages);
//
//    newbook.getHandle().tag = newBookData;
//    
//    return (ItemStack) newbook;
	
}
