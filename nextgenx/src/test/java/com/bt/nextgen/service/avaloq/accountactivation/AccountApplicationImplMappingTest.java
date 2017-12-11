package com.bt.nextgen.service.avaloq.accountactivation;

import static org.junit.Assert.assertEquals;

import java.io.InputStreamReader;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;

public class AccountApplicationImplMappingTest
{
	@Test
    @Ignore
	public void testAccountApplication() throws Exception
	{
		final ClassPathResource classPathResource = new ClassPathResource(
				"/webservices/response/BTFG$UI_DOC_CUSTR_LIST_DOC.xml");
		String content = FileCopyUtils.copyToString(new InputStreamReader(
				classPathResource.getInputStream()));
		AccountApplicationImpl applications = new DefaultResponseExtractor<AccountApplicationImpl>(AccountApplicationImpl.class)
				.extractData(content);
		
		assertEquals("227362", applications.getApplication().get(0)
		.getAppNumber());
		
		assertEquals("72506", applications.getApplication().get(0)
			.getPersonDetails().get(0).getClientKey().getId());
		
		assertEquals("72505", applications.getApplication().get(0)
			.getPersonDetails().get(1).getClientKey().getId());
		
		assertEquals("227362", applications.getApplication().get(0)
			.getAppNumber());
		
	}
}
