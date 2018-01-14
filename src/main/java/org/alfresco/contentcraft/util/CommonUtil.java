package org.alfresco.contentcraft.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.chemistry.opencmis.commons.data.ContentStream;


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

    /**
     * Helper method to get the contents of a stream
     *
     * @param stream
     * @return
     * @throws IOException
     */
    public static String getContentAsString(ContentStream stream)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Reader reader = new InputStreamReader(stream.getStream(), "UTF-8");
            try
            {
                final char[] buffer = new char[4 * 1024];
                int b;
                while (true)
                {
                    b = reader.read(buffer, 0, buffer.length);
                    if (b > 0)
                    {
                        sb.append(buffer, 0, b);
                    }
                    else if (b == -1)
                    {
                        break;
                    }
                }
            }
            finally
            {
                reader.close();
            }
        }
        catch (IOException exception)
        {
            System.out.println("Unable to read content.  " + exception.getMessage());
        }

        return sb.toString();
    }

}
