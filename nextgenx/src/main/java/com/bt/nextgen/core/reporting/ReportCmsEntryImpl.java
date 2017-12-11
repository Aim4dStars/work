package com.bt.nextgen.core.reporting;

import com.bt.nextgen.cms.CmsEntry;
import com.bt.nextgen.cms.service.CmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implements the adaption of a report and a CmsEntry. Also houses a basic 100 item compiled cache.
 */
public class ReportCmsEntryImpl implements ReportTemplate
{
	private static final Logger logger = LoggerFactory.getLogger(ReportCmsEntryImpl.class);

	private final CmsEntry me;
    private final String reportBase;
	private final ReportIdentity id;

	private static final int MAX_COMPILER_CACHE = 100;
	private static final LinkedHashMap<CmsEntry, Object> COMPILER_CACHE = new LinkedHashMap<CmsEntry, Object>(MAX_COMPILER_CACHE)
	{
		@Override protected boolean removeEldestEntry(Map.Entry<CmsEntry, Object> eldest)
		{
			return size() > MAX_COMPILER_CACHE;
		}
	};

    public ReportCmsEntryImpl(CmsEntry me, String reportBase, ReportIdentity id)
	{
		this.me = me;
        this.reportBase = reportBase;
		this.id = id;
	}

	@Override public ReportIdentity getId()
	{
		return id;
	}

	@Override public boolean isAvailable()
	{
		return !CmsService.MISSING.equals(me);
	}

	@Override public String getType()
	{
		return me.getMetaData("contentType");
	}

	@Override public InputStream getAsStream() throws IOException
	{
        return new DefaultResourceLoader().getResource(reportBase + me.getValue()).getInputStream();
	}

	@Override public void setCompiledVersion(Object compiledVersion)
	{
		logger.info("Caching compiled report for {}", id);
		COMPILER_CACHE.put(me, compiledVersion);
		logger.debug("Cache statistics : size {}", COMPILER_CACHE.size());
	}

	@Override public Object getCompiledVersion()
	{
		if(logger.isDebugEnabled())
		{
			if(COMPILER_CACHE.get(me) == null)
			{
				logger.debug("Cache miss - {}", id);
			}
			else {
				logger.debug("Cache hit - {}", id);
			}
		}
		return COMPILER_CACHE.get(me);
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

		ReportCmsEntryImpl cmsEntry = (ReportCmsEntryImpl) o;

		if (id != null ? !id.equals(cmsEntry.id) : cmsEntry.id != null)
		{
			return false;
		}
		if (me != null ? !me.equals(cmsEntry.me) : cmsEntry.me != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = me != null ? me.hashCode() : 0;
		result = 31 * result + (id != null ? id.hashCode() : 0);
		return result;
	}
}
