package com.bt.nextgen.core.api.model;

import com.bt.nextgen.util.Environment;
import org.joda.time.DateTime;

public class ApiResponse
{
	private String apiVersion;
	private Integer status;
	private Dto data;
	private ApiError error;
	private PagingResponse paging;
	private DateTime lastUpdatedTime = new DateTime();

	public ApiResponse(String apiVersion, Integer status, Dto data, ApiError error, PagingResponse paging)
	{
		super();
		this.apiVersion = apiVersion;
		this.status = status;
		this.data = data;
		this.error = error;
		this.paging = paging;
	}

	public ApiResponse(String apiVersion, ApiError error)
	{
		this(apiVersion, 0, null, error, null);
	}

	public ApiResponse(String apiVersion, Dto data)
	{
		this(apiVersion, 1, data, null, null);
	}

	public ApiResponse(String apiVersion, Dto data, PagingResponse paging)
	{
		this(apiVersion, 1, data, null, paging);
	}

	public String getApiVersion()
	{
		return apiVersion;
	}

	public Integer getStatus()
	{
		return status;
	}

	public Dto getData()
	{
		return data;
	}

	public Dto setData()
	{
		return data;
	}

	public ApiError getError()
	{
		return error;
	}

	public void setError(ApiError error)
	{
		this.error = error;
	}

	public PagingResponse getPaging()
	{
		return paging;
	}

	public DateTime getLastUpdatedTime()
	{
		return lastUpdatedTime;
	}

    public Boolean getSuperceded() {
        if (!Environment.isProduction() && data != null) {
            return data.getClass().isAnnotationPresent(Deprecated.class);
        }
        return null;
    }
}
