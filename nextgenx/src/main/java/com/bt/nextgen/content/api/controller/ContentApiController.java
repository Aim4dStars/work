package com.bt.nextgen.content.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.BeanFilter;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.Sort;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API_PUBLIC, produces = "application/json")
public class ContentApiController
{
	@Autowired
	private ContentDtoService contentService;

	/**
	 * Find content by primary key. No additional operations supported
	 */
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CONTENT_BY_ID)
	public @ResponseBody
	KeyedApiResponse <ContentKey> getContentById(@PathVariable(UriMappingConstants.CONTENT_ID_URI_MAPPING) String contentId)
		throws Exception
	{
		ContentKey key = new ContentKey(contentId);
		return new FindByKey <>(ApiVersion.CURRENT_VERSION, contentService, key).performOperation();
	}

	/**
	 * Find all available content. Supports sorting and bean filtering.
	 */
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CONTENT)
	public @ResponseBody
	ApiResponse getContent(@RequestParam(value = BeanFilter.QUERY_PARAMETER, required = false) String queryString,
		@RequestParam(value = Sort.SORT_PARAMETER, required = false) String sortOrder) throws Exception
	{
		return new Sort <>(new BeanFilter(ApiVersion.CURRENT_VERSION, new FindAll <>(ApiVersion.CURRENT_VERSION,
			contentService), queryString), sortOrder).performOperation();
	}
}
