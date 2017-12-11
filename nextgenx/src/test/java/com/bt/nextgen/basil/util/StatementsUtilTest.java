package com.bt.nextgen.basil.util;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.SearchImagesRequestMsgType;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.service.integration.StatementsUtil;

@RunWith(MockitoJUnitRunner.class)
public class StatementsUtilTest
{
	@Test
	public void testMakeSearchImageRequest_Quartely()
	{
		SearchImagesRequestMsgType searchImagesRequestMsgType = StatementsUtil.makeSearchImageRequest("10000005");
		String investornumber = searchImagesRequestMsgType.getSearchFilter()
			.getDocumentProperties()
			.getKeyIndexProperty()
			.getInvertedKeyIndexProperty()
			.getDocumentIndexPropertyValues()
			.getDocumentIndexStringPropertyValue()
			.get(0);

		assertThat(searchImagesRequestMsgType, Is.is(notNullValue()));
		assertThat(investornumber, Is.is("10000005"));

	}

	@Test
	public void testMakeSearchImageRequest_Quartely_Payg()
	{
		SearchImagesRequestMsgType searchImagesRequestMsgType = StatementsUtil.makeSearchImageRequest("10000005");
		String investornumber = searchImagesRequestMsgType.getSearchFilter()
			.getDocumentProperties()
			.getKeyIndexProperty()
			.getInvertedKeyIndexProperty()
			.getDocumentIndexPropertyValues()
			.getDocumentIndexStringPropertyValue()
			.get(0);

		assertThat(searchImagesRequestMsgType, Is.is(notNullValue()));
		assertThat(investornumber, Is.is("10000005"));

	}

	@Test
	public void testMakeSearchImageRequest_Exit_Stmt()
	{
		SearchImagesRequestMsgType searchImagesRequestMsgType = StatementsUtil.makeSearchImageRequest("10000005");
		String investornumber = searchImagesRequestMsgType.getSearchFilter()
			.getDocumentProperties()
			.getKeyIndexProperty()
			.getInvertedKeyIndexProperty()
			.getDocumentIndexPropertyValues()
			.getDocumentIndexStringPropertyValue()
			.get(0);
		int withoutEffectiveDate = searchImagesRequestMsgType.getSearchFilter()
			.getDocumentProperties()
			.getFilterDocumentProperties()
			.getDocumentIndexProperties()
			.getDocumentIndexProperty()
			.size();
		assertThat(searchImagesRequestMsgType, Is.is(notNullValue()));
		assertThat(investornumber, Is.is("10000005"));
		assertThat(withoutEffectiveDate, Is.is(1));
	}

	@Test
	public void testMakeSearchImageRequest_Failure_Notification()
	{
		SearchImagesRequestMsgType searchImagesRequestMsgType = StatementsUtil.makeSearchImageRequest("10000005");
		String investornumber = searchImagesRequestMsgType.getSearchFilter()
			.getDocumentProperties()
			.getKeyIndexProperty()
			.getInvertedKeyIndexProperty()
			.getDocumentIndexPropertyValues()
			.getDocumentIndexStringPropertyValue()
			.get(0);
		int withoutEffectiveDate = searchImagesRequestMsgType.getSearchFilter()
			.getDocumentProperties()
			.getFilterDocumentProperties()
			.getDocumentIndexProperties()
			.getDocumentIndexProperty()
			.size();
		assertThat(searchImagesRequestMsgType, Is.is(notNullValue()));
		assertThat(investornumber, Is.is("10000005"));
		assertThat(withoutEffectiveDate, Is.is(1));
	}
}
