package com.bt.nextgen.service.basil;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import ns.btfin_com.sharedservices.bpm.image.imageservice.imagereply.v1_0.SearchImagesResponseMsgType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.portfolio.web.model.StatementTypeErrorModel;
import com.bt.nextgen.service.bassil.BassilServiceImpl;
import com.bt.nextgen.service.bassil.ClientStatements;

@RunWith(MockitoJUnitRunner.class)
public class BassilServiceImplTest
{
	@InjectMocks
	BassilServiceImpl bassilService;

	@Mock
	WebServiceProvider provider;

	List <String> docIds = null;

	@Before
	public void prepareDate()
	{
		docIds = new ArrayList <String>();
		docIds.add("100951");
		docIds.add("100961");
		docIds.add("100951");
		docIds.add("100961");
		docIds.add("100971");
		docIds.add("100971");
		docIds.add("100981");
	}

	@Test
	public void testLoadClientStatements() throws Exception
	{
		SearchImagesResponseMsgType searchImagesResponseMsgType = JaxbUtil.unmarshall("/webservices/response/BasilServiceResponse_UT.xml",
			SearchImagesResponseMsgType.class);

		String accountId = "12345";
		StatementTypeErrorModel error = null;
		Mockito.when(provider.sendWebService(anyString(), anyObject())).thenReturn(searchImagesResponseMsgType);
		List <ClientStatements> statements = bassilService.loadClientStatements(accountId, error);
		assertNotNull(statements);
		for (ClientStatements statement : statements)
		{
			assertTrue(docIds.contains(statement.getDocumentID()));
			assertNotNull(statement.getDocumentType());
			assertNotNull(statement.getDocumentProperties());
			assertNotNull(statement.getDocumentEntryDate());
			assertNotNull(statement.getDocumentProperties("DocumentType"));

			if (statement.getDocumentProperties().get("PdfSize") != null)
			{
				assertEquals("Expected 23.10", "23.20", statement.getDocumentProperties().get("PdfSize").get(0));
			}
			if (statement.getDocumentProperties().get("CsvSize") != null)
			{
				assertEquals("Expected 23.20", "23.10", statement.getDocumentProperties().get("CsvSize").get(0));
			}

		}
	}

	@Test
	public void test_error_esenarion_for_LoadClientStatements() throws Exception
	{
		SearchImagesResponseMsgType searchImagesResponseMsgType = JaxbUtil.unmarshall("/webservices/response/BasilServiceResponse_Error_UT.xml",
			SearchImagesResponseMsgType.class);
		String accountId = "12345";
		StatementTypeErrorModel error = new StatementTypeErrorModel();
		Mockito.when(provider.sendWebService(anyString(), anyObject())).thenReturn(searchImagesResponseMsgType);
		List <ClientStatements> statements = bassilService.loadClientStatements(accountId, error);
		assertNull(statements);
		assertNotNull(error);
		assertEquals("Expected Error", "Error", error.getDescription());
		assertEquals("Expected 'ER-123'", "ER-123", error.getSubCode());
		assertEquals("Expected 'Basil Error'", "Basil Error", error.getReason());
	}
}
