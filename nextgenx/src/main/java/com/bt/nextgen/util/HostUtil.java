package com.bt.nextgen.util;

import static com.bt.nextgen.core.util.SETTINGS.HTTP_REDIRECT_TEMPLATE;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_HEADER_XFORWARDHOST;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

public class HostUtil
{
	/**
	 * Based on forwarded host
	 * @return
	 */
	public static String createOriginBaseUrl(HttpServletRequest request)
	{
		String base = HTTP_REDIRECT_TEMPLATE.value();
		return MessageFormat.format(base, request.getHeader(SECURITY_HEADER_XFORWARDHOST.value()));
	}

}
