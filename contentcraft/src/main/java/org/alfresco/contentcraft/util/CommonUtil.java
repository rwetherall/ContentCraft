package org.alfresco.contentcraft.util;

import java.util.ArrayList;
import java.util.List;

public class CommonUtil
{
    /**
     * Helper method to split a string by pages and page size
     * 
     * @param message
     * @param maxPages
     * @param pageSize
     * @return
     */
    public static List<String> split(String message, int maxPages, int pageSize)
    {
        List<String> result = new ArrayList<String>(maxPages);
        for (int page = 0; page < maxPages; page++) 
        {
            int startIndex = page * pageSize;
            int endIndex = (page + 1) * pageSize;
            if (endIndex < message.length())
            {       
                result.add(message.substring(startIndex, endIndex));
            }
            else 
            {
                result.add(message.substring(startIndex));
                break;
            }
        }
        return result;
    }

}
