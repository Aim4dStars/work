package com.bt.nextgen.core.web.taglib;

import com.bt.nextgen.core.util.UriEncoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HashUrlTag extends SimpleTagSupport
{
	private static final Logger logger = LoggerFactory.getLogger(HashUrlTag.class);

	private String src = null;
	private String pdf = null;
	private static final Map<String, Entry> cache = new ConcurrentHashMap<>();

	public void doTag() throws JspException, IOException
	{
		HttpServletRequest httpServletRequest = ((HttpServletRequest) ((PageContext) getJspContext()).getRequest());
		int port = httpServletRequest.getLocalPort();
		String contextPath = httpServletRequest.getContextPath();

		JspWriter out = getJspContext().getOut();
		if (StringUtils.isNotBlank(pdf))
		{
            String cssUrlForPdf =  null;
            //TODO review this as it may be in the wrong context and need to be related to xforward host
            //Port is hardcoded to 9443 for the was environment as this is undetectable from the request

            logger.info("Request port is {}", httpServletRequest.getServerPort());
            logger.info("Other port suggestion is {}", port);
            logger.info("Request scheme is {}",httpServletRequest.getScheme() );
            logger.info("Request hostname is {}",httpServletRequest.getServerName() );

            cssUrlForPdf = httpServletRequest.getScheme() + "://localhost:" + port +   contextPath + src;

            logger.info("The css url is set to {}", cssUrlForPdf);
			out.write(cssUrlForPdf);
			return;
		}
		else
		{
			String hash = calculateHash();
			hash = hash.replaceAll("\\/", "");
			out.write(((HttpServletRequest) getJspContext().getAttribute(
				"javax.servlet.jsp.jspRequest")).getContextPath() + src.replaceFirst("\\/static\\/",
				"/static/_" + hash + "/"));
		}
	}

	public String calculateHash() throws IOException
	{
		ApplicationContext ctxt = getAppContext();
		Resource resource = ctxt.getResource(src);
		if (cache.containsKey(src))
		{
			Entry hit = cache.get(src);
			if (hit.loadTime == resource.lastModified())
			{
				logger.debug("Cache hit for {}", src);
				return hit.hash;
			}

		}
		logger.debug("Cache miss for {}", src);
		MessageDigest sha = null;
		try
		{
			sha = MessageDigest.getInstance("SHA");
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException(e);
		}
		InputStream is = null;
		try
		{
			is = resource.getInputStream();
		}
		catch (FileNotFoundException fnf)
		{
			return "NOTFOUND";
		}
		try
		{
			is = new DigestInputStream(is, sha);
			byte[] array = new byte[1024];
			while (is.read(array) != -1)
			{
				;
			}
		}
		finally
		{
			is.close();
		}

		Entry entry = new Entry(resource.lastModified(), UriEncoder.encode(sha.digest()));
		cache.put(src, entry);
		return entry.hash;
	}

	public String getSrc()
	{
		return src;
	}

	public void setSrc(String src)
	{
		this.src = src;
	}

	public String getPdf()
	{
		return pdf;
	}

	public void setPdf(String pdf)
	{
		this.pdf = pdf;
	}

	public ApplicationContext getAppContext()
	{
		return WebApplicationContextUtils.getWebApplicationContext(((PageContext) getJspContext()).getServletContext());
	}

	/**
	 * Will contain the data from the hashing
	 */
	private class Entry
	{
		public final long loadTime;
		public final String hash;

		private Entry(long loadTime, String hash)
		{
			this.loadTime = loadTime;
			this.hash = hash;
		}
	}
}