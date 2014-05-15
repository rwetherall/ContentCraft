/**
 * 
 */
package org.alfresco.contentcraft.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO use this it mark commands and auto register them with the plugin
 * 
 * @author Roy Wetherall
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface CommandBean 
{
	/** name of the command */
	String name();
	
	/** command usage */
	String usage() default "";

	/** indicates if this is a player command or not */
	boolean playerCommand() default true;
}
