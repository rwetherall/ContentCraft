package org.alfresco.contentcraft.command;

public class CommandUsageException extends Exception 
{
	private static final long serialVersionUID = -5002358001546407140L;

	public CommandUsageException(String arg0) 
	{
		super(arg0);
	}

	public CommandUsageException(Throwable arg0) 
	{
		super(arg0);
	}

	public CommandUsageException(String arg0, Throwable arg1) 
	{
		super(arg0, arg1);
	}

	public CommandUsageException(String arg0, Throwable arg1, boolean arg2, boolean arg3) 
	{
		super(arg0, arg1, arg2, arg3);
	}
}
