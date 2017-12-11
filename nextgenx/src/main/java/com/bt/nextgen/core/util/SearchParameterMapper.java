package com.bt.nextgen.core.util;

import com.bt.nextgen.core.api.operation.ApiSearchCriteria;

public interface SearchParameterMapper
{
	String getSearchKey(ApiSearchCriteria criteria);

	String getSearchValue(ApiSearchCriteria criteria);
}
