package com.bt.nextgen.core.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.web.model.SearchParameters;
import com.bt.nextgen.core.web.model.SearchParams;
import com.bt.nextgen.service.avaloq.Template;

public class SearchResultUtilTest
{
	private static class TestSearchParameterMapper implements SearchParameterMapper
	{

		@Override
		public String getSearchKey(ApiSearchCriteria criteria)
		{
			return criteria.getProperty();
		}

		@Override
		public String getSearchValue(ApiSearchCriteria criteria)
		{
			return criteria.getValue();
		}

	}

	@Test
	public void testBuildSearchQueryFor_whenListEmpty_thenSearchParametersIsEmpty()
	{
		List <ApiSearchCriteria> criteriaList = Collections.emptyList();
		SearchParameters searchParameters = SearchResultsUtil.buildSearchQueryFor(criteriaList,
			new TestSearchParameterMapper(),
			Template.ORDERS);
		Assert.assertEquals(0, searchParameters.getSearchCriterias().size());
	}

	@Test
	public void testBuildSearchQueryFor_whenListSupplied_thenSearchParametersMatches()
	{
		List <ApiSearchCriteria> criteriaList = new ArrayList <>();

		criteriaList.add(new ApiSearchCriteria(SearchParams.ORDER_ID.name(), SearchOperation.EQUALS, "1234", OperationType.STRING));
		criteriaList.add(new ApiSearchCriteria(SearchParams.FROM_DATE.name(),
			SearchOperation.EQUALS,
			"12/11/2013",
			OperationType.DATE));
		criteriaList.add(new ApiSearchCriteria(SearchParams.TO_DATE.name(),
			SearchOperation.EQUALS,
			"12/12/2013",
			OperationType.DATE));

		SearchParameters searchParameters = SearchResultsUtil.buildSearchQueryFor(criteriaList,
			new TestSearchParameterMapper(),
			Template.ORDERS);

		Assert.assertEquals(criteriaList.size(), searchParameters.getSearchCriterias().size());

		for (int i = 0; i < criteriaList.size(); i++)
		{
			Assert.assertEquals(criteriaList.get(i).getProperty(), searchParameters.getSearchCriterias()
				.get(i)
				.getSearchKey()
				.name());
			Assert.assertEquals(criteriaList.get(i).getValue(), searchParameters.getSearchCriterias().get(i).getSearchValue());
		}

		Assert.assertEquals(Template.ORDERS, searchParameters.getSearchFor());
	}

}
