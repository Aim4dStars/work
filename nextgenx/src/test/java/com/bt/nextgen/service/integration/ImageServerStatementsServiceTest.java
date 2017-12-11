package com.bt.nextgen.service.integration;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.hamcrest.core.Is;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.portfolio.web.model.ClientStatementsInterface;
import com.bt.nextgen.portfolio.web.model.StatementTypeModel;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.web.controller.cash.util.Attribute;

import ns.btfin_com.sharedservices.bpm.image.imageservice.imagereply.v1_0.SearchImagesResponseMsgType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.SearchImagesRequestMsgType;


@RunWith(MockitoJUnitRunner.class)
public class ImageServerStatementsServiceTest
{

	@InjectMocks
	ImageServerStatementsService imageServerStatementsService = new ImageServerStatementsService();
	
	@Mock BankingAuthorityService userSamlService;

	@Mock
	WebServiceProvider webServiceProvider;

	@Test
	@Ignore
	public void testGetStatements_WithSuccessResponse()
	{
		SearchImagesResponseMsgType searchImagesResponseMsgType = JaxbUtil.unmarshall("/webservices/response/BasilServiceResponse_UT.xml",
			SearchImagesResponseMsgType.class);
		when(userSamlService.getSamlToken()).thenReturn(new SamlToken(""));
		when(webServiceProvider.sendWebServiceWithSecurityHeader(any(SamlToken.class), anyString(),
			any(SearchImagesRequestMsgType.class)))
			.thenReturn(searchImagesResponseMsgType);

		ClientStatementsInterface clientStatements = imageServerStatementsService.getStatements("123456", new ServiceErrorsImpl());
		assertNotNull(clientStatements);
		//Check Error Model should null
		assertThat(clientStatements.getStatementTypeErrorModel(), nullValue());

		//Check Exit/Closer Statements
		assertThat(clientStatements.getAccountConfirmationModelList(), notNullValue());
		assertThat(clientStatements.getAccountConfirmationModelList().size(), Is.is(1));
		StatementTypeModel accountConfirmationStatModel = clientStatements.getAccountConfirmationModelList().get(0);
		assertThat(accountConfirmationStatModel.getStatementType(), Is.is(Attribute.ACCOUNT_CONFIRMATIONS));
		assertThat(accountConfirmationStatModel.getStatementDetailList().get(0).getPeriodTypeStatement(),
			Is.is("E-Statement Failure Notification"));
		assertThat(accountConfirmationStatModel.getStatementDetailList().get(0).getReportSource(),
			Is.is(Attribute.REPORT_SOURCE_BASIL));

		//Check Quarterly Statements
		assertThat(clientStatements.getStatementTypeModelList(), notNullValue());
		assertThat(clientStatements.getStatementTypeModelList().size(), Is.is(2));

		//Check Annual Statements
		assertThat(clientStatements.getAnnualStatementTypeModelList(), notNullValue());
		assertThat(clientStatements.getAnnualStatementTypeModelList().size(), Is.is(1));
		StatementTypeModel annualStatementModel = clientStatements.getAnnualStatementTypeModelList().get(0);
		assertThat(annualStatementModel.getStatementType(), Is.is(Attribute.ANNUAL_STMTS));
		assertThat(annualStatementModel.getStatementDetailList().get(0).getPeriodTypeStatement(),
			Is.is("2013-2014 Annual investor statement"));
		assertThat(annualStatementModel.getStatementDetailList().get(0).getPeriodFromDate(), Is.is("01 Jul 2013"));
		assertThat(annualStatementModel.getStatementDetailList().get(0).getPeriodToDate(), Is.is("30 Jun 2014"));
		assertThat(annualStatementModel.getStatementDetailList().get(0).getReportSource(), Is.is(Attribute.REPORT_SOURCE_BASIL));

	}

	@Test
	public void testGetStatements_ErrorSuccessResponse()
	{
		SearchImagesResponseMsgType searchImagesResponseMsgType = JaxbUtil.unmarshall("/webservices/response/BasilServiceResponse_Error_UT.xml",
			SearchImagesResponseMsgType.class);
		when(userSamlService.getSamlToken()).thenReturn(new SamlToken(""));
		when(webServiceProvider.sendWebServiceWithSecurityHeader(any(SamlToken.class), anyString(),
			any(SearchImagesRequestMsgType.class)))
			.thenReturn(searchImagesResponseMsgType);

		ClientStatementsInterface clientStatements = imageServerStatementsService.getStatements("123456", new ServiceErrorsImpl());
		assertNotNull(clientStatements);
		//Check Error Model should null
		assertThat(clientStatements.getStatementTypeErrorModel(), notNullValue());
		assertThat(clientStatements.getStatementTypeErrorModel().getSubCode(), Is.is("ER-123"));
		assertThat(clientStatements.getStatementTypeErrorModel().getDescription(), Is.is("Error"));
		assertThat(clientStatements.getStatementTypeErrorModel().getReason(), Is.is("Basil Error"));
	}

}
