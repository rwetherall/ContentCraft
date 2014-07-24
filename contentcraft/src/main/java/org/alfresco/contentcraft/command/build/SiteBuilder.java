package org.alfresco.contentcraft.command.build;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.alfresco.contentcraft.ContentCraftPlugin;
import org.alfresco.contentcraft.cmis.CMIS;
import org.alfresco.contentcraft.command.CommandUsageException;
import org.alfresco.contentcraft.command.macro.Macro;
import org.alfresco.contentcraft.command.macro.MacroCallback;
import org.alfresco.contentcraft.command.macro.MacroCommandExecuter;
import org.alfresco.contentcraft.command.macro.PlaceBlockMacroAction;
import org.alfresco.contentcraft.util.VectorUtil;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.Tree;
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
		
		// grab the macro command executer instance		
		Macro folderFrontMacro = getMacroCommand().getMacro("site.folder.front");
		
		// get the folder tree
		List<Tree<FileableCmisObject>> docLibTree = siteRoot.getFolderTree(3);		
		if (docLibTree.size() == 0)
		{
			player.sendMessage("The site " + siteName + " has not root documents or folders.");
		}
		else
		{
			// get the folder start location
			Location folderFrontLocation = start.clone();
			
			for (Tree<FileableCmisObject> tree : docLibTree) 
			{
				// get the folder information
				FileableCmisObject folder = tree.getItem();
				final String[] messages = 
				{
					folder.getName(),
					folder.getDescription()
				};
				
				// execute the folder front macro
				folderFrontMacro.run(folderFrontLocation, new MacroCallback() 
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
				
				// move to the next folder location
				folderFrontLocation.add(VectorUtil.UP.clone().multiply(5));				
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
		
	
}
