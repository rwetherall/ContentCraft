
package org.alfresco.contentcraft.command.cmis;

import java.util.Map;

import org.alfresco.contentcraft.cmis.CMIS;
import org.alfresco.contentcraft.command.BaseCommandExecuter;
import org.alfresco.contentcraft.command.CommandUsageException;
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
		String result = CMIS.testConnect();
		sender.sendMessage(result);
	}	
}
