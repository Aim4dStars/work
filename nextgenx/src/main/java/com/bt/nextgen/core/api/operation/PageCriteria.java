package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.exception.BadRequestException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Parsed API tier paging criteria
 */
public class PageCriteria
{
	private String apiVersion;
	private int startIndex;
	private int maxResults;

	private PageCriteria(String apiVersion, int startIndex, int maxResults)
	{
		super();
		this.apiVersion = apiVersion;
		this.startIndex = startIndex;
		this.maxResults = maxResults;
	}

	public int getStartIndex()
	{
		return startIndex;
	}

	public int getMaxResults()
	{
		return maxResults;
	}

	public static PageCriteria parsePagingString(String apiVersion, String pagingString)
	{
		if (pagingString == null)
		{
			return null;
		}

		try
		{
			JSONObject paging = new JSONObject(pagingString);
			return new PageCriteria(apiVersion, paging.getInt("startIndex"), paging.getInt("maxResults"));
		}
		catch (JSONException e)
		{
			throw new BadRequestException("Unable to parse query string", e);
		}
	}

}
