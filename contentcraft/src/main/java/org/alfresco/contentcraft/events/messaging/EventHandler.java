package org.alfresco.contentcraft.events.messaging;

import org.alfresco.events.types.Event;

public interface EventHandler<T extends Event> {

	String FOLDER_CREATE = "activity.org.alfresco.documentlibrary.folder-added";
	String FOLDER_DELETED = "activity.org.alfresco.documentlibrary.folder-deleted";
	String FILE_ADDED = "activity.org.alfresco.documentlibrary.file-added";
	String FILE_DELETED = "activity.org.alfresco.documentlibrary.file-deleted";
	String USER_CREATE = "user.create";
	String USER_UPDATE = "user.update";
	String SITE_CREATE = "site.create";
	String SITE_UPDATE = "site.update";
	
	void handle(T event);
}
