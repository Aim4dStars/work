package com.bt.nextgen.cms.service;

import com.bt.nextgen.cms.CmsEntry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public interface CmsService
{
	public static enum STATUS
	{
		SUCCESS, FAILURE
	}

	/**
	 * This represents a 'missing' entry
	 */
	public static final CmsEntry MISSING = new CmsEntry()
	{
		@Override
		public String getMetaData(String name)
		{
			return null;
		}

		@Override
		public String getValue()
		{
			return null;
		}

		@Override
		public InputStream getStream() throws IOException
		{
			return null;
		}

	};

    /**
     * Retrieves an individiual content entry. User specific content indexes are
     * checked and the resultant text has user specific placeholders
     * substituted.
     */
	String getContent(String key);

    /**
     * Retrieves an individiual content entry. User specific content indexes are
     * checked and the resultant text has user specific placeholders
     * substituted. Custom parameter replacements are performed.
     */
    String getDynamicContent(String key, String[] params);

    /**
     * Retrieves an individiual content entry. User specific content indexes are
     * checked.
     */
	CmsEntry getRawContent(String key);

    /**
     * retrieves the full list of available content indexes
     */
	Collection <String> getRawContentIndex();

	STATUS reLoadCmsContent();
}