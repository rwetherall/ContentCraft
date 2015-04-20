package org.alfresco.contentcraft.events.messaging;

import java.io.ByteArrayOutputStream;

import org.gytheio.messaging.jackson.ObjectMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Basic marshalling methods, uses gytheio.
 * 
 * @author Gethin James
 *
 */
public class Marshaller {

	ObjectMapper mapper = ObjectMapperFactory.createInstance();

	/**
	 * Marshals an object to a String
	 * @param obj
	 * @return String representation of the object
	 * @throws Exception
	 */
	public String marshal(Object obj) throws Exception {
		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
		mapper.writeValue(outBytes, obj);
		return outBytes.toString();
	}

	/**
	 * Unmarshalls from a String.
	 * @param in
	 * @return Object - probably and Event object.
	 * @throws Exception
	 */
	public Object unmarshal(String in) throws Exception {
		return mapper.readValue(in, Object.class);
	}
}
