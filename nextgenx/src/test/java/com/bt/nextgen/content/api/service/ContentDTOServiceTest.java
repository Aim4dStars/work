package com.bt.nextgen.content.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.bt.nextgen.cms.CmsEntry;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class ContentDTOServiceTest
{
	@InjectMocks
	private ContentDtoServiceImpl valuationDTOService;

	@Mock
	private CmsService cmsService;

	@Before
	public void setup() throws Exception
	{}

	@Test
	public void testFindSingle_whenContentIdDoesNotMatch_thenNull()
	{
		final CmsEntry entry = CmsService.MISSING;
		Mockito.doAnswer(new Answer <CmsEntry>()
		{
			@Override
			public CmsEntry answer(InvocationOnMock invocation) throws Throwable
			{
				return entry;
			}
		}).when(cmsService).getRawContent(anyString());
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		ContentDto contentDto = valuationDTOService.find(new ContentKey("thisdoesnotexist"), serviceErrors);
		assertNull(contentDto);
	}

	@Test
	public void testGetManagedPortfoliosValuationFromPortfolio__whenKnownContentIdSupplied_thenDtoContainsThatContent()
	{
		final CmsEntry entry = mock(CmsEntry.class);
		when(entry.getValue()).thenReturn("testing");
		Mockito.doAnswer(new Answer <CmsEntry>()
		{
			@Override
			public CmsEntry answer(InvocationOnMock invocation) throws Throwable
			{
				return entry;
			}
		}).when(cmsService).getRawContent(anyString());
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		ContentDto contentDto = valuationDTOService.find(new ContentKey("SomeString"), serviceErrors);
		assertEquals(entry.getValue(), contentDto.getContent());

	}
}
