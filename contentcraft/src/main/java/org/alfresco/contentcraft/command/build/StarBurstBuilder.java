package org.alfresco.contentcraft.command.build;

import org.alfresco.contentcraft.cmis.CMIS;
import org.alfresco.contentcraft.command.CommandUsageException;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class StarBurstBuilder implements Builder 
{
	public String getName() 
	{
		return "starburst";
	}

	public void build(Location start, Vector direction, String... args) throws CommandUsageException
	{
		// get the site we want to use as a base
		String siteName = args[1];
		if (siteName == null || siteName.length() == 0)
		{
			throw new CommandUsageException("You didn't provide a site name!");
		}
		
		// grab the document libaray for the site
		Session session = CMIS.connect();
		Folder siteRoot = CMIS.getSiteRoot(session, siteName);
		if (siteRoot == null)
		{
			throw new CommandUsageException("The site (" + siteName + ") you provided couldn't be found!");
		}
		
		System.out.println(siteRoot.getName());
		
		Location current = start;
		
		for (int side = 0; side < 4; side++)
		{
			for (int i = 0; i < 10; i++) 
			{
				Block currentBlock = current.getBlock();
				currentBlock.setType(Material.GOLD_BLOCK);			
				current.add(direction);
			}
			
			direction = rotate90(direction);
		}
	}
	
	private Vector rotate90(Vector vector)
	{
		double x = vector.getZ()*-1;
		double z = vector.getX();
		
		return new Vector(x, vector.getY(), z);
	}
}
