package com.bt.nextgen.cms.service;

import com.bt.nextgen.cms.CmsEntry;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import static com.bt.nextgen.core.util.SETTINGS.CMS_BASE_DIR;

/**
 * This JAXB class will contain entries from the cms-index.xml file.
 */
public class FileCmsEntry implements CmsEntry
{
	private static final Logger logger = LoggerFactory.getLogger(FileCmsEntry.class);

	/**
	 * Will contain a cached value of the file contents, lazy loaded
	 */
	private String value;

	/**
	 * Will be the link to the resource via the url from wrapped cms entry
	 */
	private Resource referenceFile;

	/**
	 * Original cmsEntry
	 */
	private final CmsEntry cmsEntry;

	private FileCmsEntry(CmsEntry toWrap)
	{
		this.cmsEntry = toWrap;
	}

	/**
	 * Given a cms entry, if it is of type 'url' wrap and load the contents from file
	 *
	 * @param mightBeAUrl this cms entry might represent a url rather than content.
	 * @return if not a url the original cms entry otherwise an inflated version.
	 */
	public static CmsEntry wrapIfValueUrl(CmsEntry mightBeAUrl)
	{
		if ("url".equalsIgnoreCase(mightBeAUrl.getMetaData("type")))
		{
			return new FileCmsEntry(mightBeAUrl);
		}
		else
		{
			return mightBeAUrl;
		}
	}

	@Override public String getMetaData(String name)
	{
		return cmsEntry.getMetaData(name);
	}

	@Override public String getValue()
	{
		if (value == null)
		{
			final StringWriter makeString = new StringWriter();
			try
			{
                IOUtils.copy(getReferenceFile().getInputStream(), makeString);
			}
			catch (IOException e)
			{
				logger.warn("Problem getting input stream for {} returning null", cmsEntry.getValue());
				return null;
			}
			value = makeString.toString();
		}

		return value;
	}

	@Override public InputStream getStream() throws IOException
	{
        return getReferenceFile().getInputStream();
	}
	
	/**
     * Convenience method to help test cases check that the correct file is being returned, not to be used by code
     * @return
     */
    public synchronized Resource getReferenceFile()
    {
        if (referenceFile == null) {
            referenceFile = new DefaultResourceLoader().getResource(CMS_BASE_DIR.value() + cmsEntry.getValue().trim());
        }
        return this.referenceFile;
    }

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		FileCmsEntry that = (FileCmsEntry) o;

		if (cmsEntry != null ? !cmsEntry.equals(that.cmsEntry) : that.cmsEntry != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return cmsEntry != null ? cmsEntry.hashCode() : 0;
	}
}
