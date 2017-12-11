package com.bt.nextgen.core.web.util;

import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.core.type.Pair;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import static ch.lambdaj.Lambda.convert;

public class QueryString
{
	private final List<Pair<String, String>> tokens = new LinkedList<Pair<String, String>>();

	public QueryString add(String name, Object value)
	{
		tokens.add(Pair.create(name, ObjectUtils.toString(value)));
		return this;
	}

	@Override
	public String toString()
	{
		if(tokens.size() == 0)
		{
			return "";
		}
		else
		{
			return "?"+StringUtils.join(combineTokens(), "&");
		}
	}

	private List<String> combineTokens()
	{
		return convert(tokens, new Converter<Pair<String, String>, String>()
		{
			public String convert(Pair<String, String> pair)
			{
				return encode(pair.getKey()) + "=" + encode(pair.getValue());
			}
		});
	}

	private String encode(String str)
	{
		try
		{
			return URLEncoder.encode(str, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}
}
