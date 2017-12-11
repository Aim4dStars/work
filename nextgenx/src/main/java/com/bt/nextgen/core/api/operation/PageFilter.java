package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.core.api.model.PagingResponse;
import com.bt.nextgen.core.api.model.ResultListDto;

import java.util.Collections;
import java.util.List;

/**
 * Decorates the another operation by providing filtering out result not matching
 * requested pages.
 *
 * THis filter should only be used where similar functionality is not provided by
 * avaloq. The operation works by requesting all data and then throwing away the ones
 * which are not appropriate which is significantly less efficient than just
 * requesting the appropriate page range from avaloq in the first place.
 */
public class PageFilter <K, T extends KeyedDto <K>> extends ChainedControllerOperation
{
	public static final String PAGING_PARAMETER = "paging";

	private String apiVersion;
	private PageCriteria paging;

	public PageFilter(String apiVersion, ControllerOperation chained, String queryString)
	{
		super(chained);
		this.apiVersion = apiVersion;
		paging = PageCriteria.parsePagingString(apiVersion, queryString);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ApiResponse performChainedOperation(ApiResponse chainedResponse)
	{
		if (paging == null)
		{
			//short circuit where no criteria has been supplied
			return chainedResponse;
		}

		final Dto data = chainedResponse.getData();

		if (!(data instanceof ResultListDto))
		{
			throw new IllegalArgumentException("Paging only support for list operations");
		}

		return new ApiResponse(chainedResponse.getApiVersion(),
			chainedResponse.getStatus(),
			new ResultListDto <Dto>(pageFilter(((ResultListDto <Dto>)data).getResultList())),
			chainedResponse.getError(),
			getPagingResponse(((ResultListDto <Dto>)data).getResultList()));
	}

	private List <Dto> pageFilter(List <Dto> resultList)
	{
		if (paging.getStartIndex() >= resultList.size())
		{
			return Collections.emptyList();
		}
		if (paging.getStartIndex() + paging.getMaxResults() >= resultList.size())
		{
			return resultList.subList(paging.getStartIndex(), resultList.size());
		}
		return resultList.subList(paging.getStartIndex(), paging.getStartIndex() + paging.getMaxResults());
	}

	private PagingResponse getPagingResponse(List <Dto> resultList)
	{
		return new PagingResponse(paging.getStartIndex(), resultList.size());
	}
}
