package com.bt.nextgen.cms;

import java.io.IOException;
import java.io.InputStream;

/**
 * An entry in the CMS
 */
public interface CmsEntry
{
	/**
	 * Get a particular meta data entry
	 * @param name by this name
	 * @return the value or null if not found
	 */
	String getMetaData(String name);

	/**
	 * The value of the entry
	 * @return value of entry
	 */
	String getValue();

	/**
	 * Stream the contents
	 */
	InputStream getStream() throws IOException;
}
