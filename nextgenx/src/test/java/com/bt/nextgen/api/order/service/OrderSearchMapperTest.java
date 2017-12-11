package com.bt.nextgen.api.order.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.web.model.SearchParams;
import com.bt.nextgen.web.controller.cash.util.Attribute;

public class OrderSearchMapperTest
{
	private OrderSearchMapper mapper = new OrderSearchMapperImpl();

	ApiSearchCriteria portfolioIdCriteria;
	ApiSearchCriteria fromDateCriteria;
	ApiSearchCriteria toDateCriteria;
	ApiSearchCriteria orderIdCriteria;
	ApiSearchCriteria badCriteria;

	@Before
	public void setup()
	{
		portfolioIdCriteria = new ApiSearchCriteria(Attribute.ACCOUNT_ID,
			SearchOperation.EQUALS,
			"C19C51E3452002F0DC6749AF7DEDEAAD4719006302A377BF",
			OperationType.STRING);

		orderIdCriteria = new ApiSearchCriteria(Attribute.ORDER_ID, SearchOperation.EQUALS, "1234", OperationType.STRING);

		fromDateCriteria = new ApiSearchCriteria(Attribute.LAST_UPDATE_DATE,
			SearchOperation.NEG_LESS_THAN,
			"2014-04-10T01:23:11.000+08:00",
			OperationType.DATE);

		toDateCriteria = new ApiSearchCriteria(Attribute.LAST_UPDATE_DATE,
			SearchOperation.NEG_GREATER_THAN,
			"2014-04-10T01:23:11.000+08:00",
			OperationType.DATE);

		badCriteria = new ApiSearchCriteria("someNonExistantSearchParam", SearchOperation.EQUALS, "", OperationType.STRING);
	}

	@Test
	public void testGetSearchKey_whenOrderIdSeupplied_thenOrderIdReturned()
	{
		Assert.assertEquals(SearchParams.ORDER_ID.name(), mapper.getSearchKey(orderIdCriteria));
	}

	@Test
	public void testGetSearchKey_whenLastUpdateDateNotLessThan_thenFromDateReturned()
	{
		Assert.assertEquals(SearchParams.FROM_DATE.name(), mapper.getSearchKey(fromDateCriteria));
	}

	@Test
	public void testGetSearchKey_whenLastUpdateDateNotGreaterThan_thenToDateReturned()
	{
		Assert.assertEquals(SearchParams.TO_DATE.name(), mapper.getSearchKey(toDateCriteria));
	}

	@Test
	public void testGetSearchKey_whenPortfolioIdSupplied_thenPortfolioIdReturned()
	{
		Assert.assertEquals(SearchParams.PORTFOLIO_ID.name(), mapper.getSearchKey(portfolioIdCriteria));
	}

	@Test
	public void testGetSearchKey_whenUnknownSearchKey_thenBadRequest()
	{
		try
		{
			mapper.getSearchKey(badCriteria);
			Assert.fail();
		}
		catch (BadRequestException e)
		{}
	}

	@Test
	public void testGetSearchKey_whenLastUpdateDate_thenAvaloqFormattedDate()
	{
		Assert.assertEquals("2014-04-10", mapper.getSearchValue(fromDateCriteria));
	}

	@Test
	public void testGetSearchKey_whenOrderIdSupplied_thenValueReturned()
	{
		Assert.assertEquals("1234", mapper.getSearchValue(orderIdCriteria));
	}

	@Test
	public void testGetSearchKey_whenPortfolioIdSupplied_thenDecodedPortfolioIdReturned()
	{
		Assert.assertEquals("12163", mapper.getSearchValue(portfolioIdCriteria));
	}

}
