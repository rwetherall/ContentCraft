
package org.alfresco.contentcraft.command.cmis;

import java.util.Map;

import org.alfresco.contentcraft.cmis.CMIS;
import org.alfresco.contentcraft.command.BaseCommandExecuter;
import org.alfresco.contentcraft.command.CommandUsageException;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * CMIS command.
 * 
 * @author Roy Wetherall
 */
public class CMISCommandExecuter extends BaseCommandExecuter 
{
	public CMISCommandExecuter(String name, Map<String, Object> properties) 
	{
		super(name, properties);
	}

	public void onCommandImpl(CommandSender sender, Command command, String label, String[] args) throws CommandUsageException
	{
		// get the command to execute

		Session session = CMIS.connect();		
		Folder rootFolder = session.getRootFolder();
		
		System.out.println("The root folder is " + rootFolder.getName());
	}
	

}
