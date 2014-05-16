package org.alfresco.contentcraft.command.build;

import java.util.List;

import org.alfresco.contentcraft.cmis.CMIS;
import org.alfresco.contentcraft.command.CommandUsageException;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.avaje.ebeaninternal.server.ddl.AddForeignKeysVisitor;

public class SiteBuilder implements Builder 
{
	private static final Vector UP = new Vector(0, 1, 0);
	private static final Vector DOWN = new Vector(0, -1, 0);
	
	public String getName() 
	{
		return "site";
	}

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
		
		List<Tree<FileableCmisObject>> docLibTree = siteRoot.getFolderTree(3);
		
		System.out.println(docLibTree.size());
		
		if (docLibTree.size() == 0)
		{
			player.sendMessage("The site " + siteName + " has not root documents or folders.");
		}
		else
		{
			Location origin = start.clone();
			
			// dump the base of the site
			int size = docLibTree.size();
			square(start, direction, size, Material.BRICK);
			
			origin.add(direction.clone().multiply(size));
			buildStairs(origin, direction);
			
			origin.add(UP.clone().multiply(3));
			origin.add(direction.clone().multiply(3));
			
			// start to build folder structure
			buildFolder(origin, direction, docLibTree);
		}
	}
	
	private void buildStairs(Location start, Vector direction)
	{
		Byte facing = (0x1);		
		if (direction.getZ() == -1)
		{
			// north
			facing = (0x3);
		}
		else if (direction.getZ() == 1)
		{
			// south
			facing = (0x2);
		}
		else if (direction.getX() == 1)
		{
			// east
			facing = (0x0);
		}
		else if (direction.getX() == -1)
		{
			// west
			facing = (0x1);
		}
		
		Location location = start.clone();
		
		// build stairs to next floor
		location.add(UP);
		
		Block block = location.getBlock();
		block.setTypeIdAndData(53, facing, false);
		
		location.add(UP);
		location.add(direction);
		
		block = location.getBlock();
		block.setTypeIdAndData(53, facing, false);
		
		location.add(UP);
		location.add(direction);
		
		block = location.getBlock();
		block.setTypeIdAndData(53, facing, false);
		location.add(direction);		
	}
	
	private void buildFolder(Location start, Vector direction, List<Tree<FileableCmisObject>> list)
	{
		Location current = start.clone();
		
		for (Tree<FileableCmisObject> tree : list) 
		{
			List<Tree<FileableCmisObject>> children = tree.getChildren();
			
			if (children.size() != 0)
			{
				int size = children.size();
				
				// build door
				buildDoor(current, direction, tree.getItem().getName());
				
				// build floor
				square(current, direction, size, Material.BRICK);
				current.add(direction.clone().multiply(size));
				
				// build bridge
				Block block = current.getBlock();
				block.setType(Material.WOOD);
				current.add(direction);				
				block = current.getBlock();
				block.setType(Material.WOOD);
				current.add(direction);
			}
		}		
	}
	
	private void buildDoor(Location start, Vector direction, String name)
	{
		
		Block location = start.getBlock();
		Block bottomBlock = location.getRelative(BlockFace.UP, 1);
		Block topBlock = bottomBlock.getRelative(BlockFace.UP, 1);
		
		Byte top = (0x8); // top half with hinge on right		
		Byte bottom = (0x1);	
		BlockFace face = BlockFace.NORTH;
		
		if (direction.getZ() == 1)
		{
			// north
			bottom = (0x1);
			face = BlockFace.NORTH;
		}
		else if (direction.getZ() == -1)
		{
			// south
			bottom = (0x3);
			face = BlockFace.SOUTH;
		}
		else if (direction.getX() == -1)
		{
			// east
			bottom = (0x2);
			face = BlockFace.EAST;
		}
		else if (direction.getX() == 1)
		{
			// west
			bottom = (0x0);
			face = BlockFace.WEST;
		}
		
		bottomBlock.setTypeIdAndData( 64, bottom, false);
		topBlock.setTypeIdAndData( 64, top, false);
		
		Location temp = start.clone();
		temp.add(UP);
		temp.add(rotate90(direction).multiply(2));
		Block signBlock = temp.getBlock();
		signBlock.setType(Material.SIGN_POST);
		
		//signBlock.setType(Material.WALL_SIGN);

		org.bukkit.block.Sign sign = (org.bukkit.block.Sign)(signBlock.getState());
		
		org.bukkit.material.Sign signData = (org.bukkit.material.Sign)(sign.getData());
		signData.setFacingDirection(face);
		
		sign.setLine(0, name);
		sign.update();
		System.out.println(sign.getLine(0));

		
		
//		sign.setType(Material.SIGN);
//	    Sign state = (Sign)sign.getState();
//	    state.setLine(0, "Boo Yeah");
//	    state.update();
	}
	
	private Vector rotate90(Vector vector)
	{
		double x = vector.getZ()*-1;
		double z = vector.getX();		
		return new Vector(x, vector.getY(), z);
	}
	
	private void square(Location start, Vector direction, int size, Material material)
	{
		Location localStart = start.clone();
		
		for (int j = 0; j < size; j++)
		{			
			line(localStart, rotate90(direction), size, material);
			localStart.add(direction);
		}
	}
	
	private void line(Location start, Vector direction, int size, Material material)
	{
		Location localStart = start.clone();
		
		for (int i = 0; i < size; i++)
		{			
			Block block = localStart.getBlock();
			block.setType(material);
			localStart.add(direction);
		}
		
	}
}
