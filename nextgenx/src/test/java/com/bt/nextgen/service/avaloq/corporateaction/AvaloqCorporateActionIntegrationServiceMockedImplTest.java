package com.bt.nextgen.service.avaloq.corporateaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.btfin.panorama.core.validation.Validator;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateAction;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetailsResponse;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionDetails;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AvaloqCorporateActionIntegrationServiceMockedImplTest {
	@InjectMocks
	private AvaloqCorporateActionIntegrationServiceImpl avaloqCorporateActionIntegrationServiceImpl;

	@Mock
	private AvaloqExecute avaloqExecute;

	@Mock
	private Validator validator;

	private ServiceErrors serviceErrors;

	@Before
	public void setup() {
		CorporateActionResponseImpl response = new CorporateActionResponseImpl();

		when(avaloqExecute
				.executeReportRequestToDomain(any(AvaloqReportRequest.class), eq(CorporateActionResponseImpl.class),
						any(ServiceErrors.class))).thenReturn(response);

		serviceErrors = mock(ServiceErrors.class);
	}

	@Test
	public void testResponseTypes()
	{
		List<CorporateAction> emptyCorporateActions = new ArrayList<>();
		List<CorporateAction> corporateActions = new ArrayList<>();

		CorporateAction corporateAction = mock(CorporateAction.class);
		corporateActions.add(corporateAction);

		CorporateActionResponseImpl responseNoData = mock(CorporateActionResponseImpl.class);
		CorporateActionResponseImpl responseNull = mock(CorporateActionResponseImpl.class);
		CorporateActionResponseImpl responseWithData = mock(CorporateActionResponseImpl.class);

		when(responseNoData.getCorporateActions()).thenReturn(emptyCorporateActions);
		when(responseNull.getCorporateActions()).thenReturn(null);
		when(responseWithData.getCorporateActions()).thenReturn(corporateActions);

		when(avaloqExecute
				.executeReportRequestToDomain(any(AvaloqReportRequest.class), eq(CorporateActionResponseImpl.class),
						any(ServiceErrors.class))).thenReturn(responseNoData);

		List<CorporateAction> caList =
				avaloqCorporateActionIntegrationServiceImpl
						.loadVoluntaryCorporateActions(new DateTime(), new DateTime(), Arrays.asList(""), serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(caList);

		when(avaloqExecute
				.executeReportRequestToDomain(any(AvaloqReportRequest.class), eq(CorporateActionResponseImpl.class),
						any(ServiceErrors.class))).thenReturn(responseNull);

		caList =
				avaloqCorporateActionIntegrationServiceImpl
						.loadVoluntaryCorporateActions(new DateTime(), new DateTime(), Arrays.asList(""), serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(caList);

		when(avaloqExecute
				.executeReportRequestToDomain(any(AvaloqReportRequest.class), eq(CorporateActionResponseImpl.class),
						any(ServiceErrors.class))).thenReturn(responseWithData);

		caList =
				avaloqCorporateActionIntegrationServiceImpl
						.loadVoluntaryCorporateActions(new DateTime(), new DateTime(), Arrays.asList(""), serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(caList);
	}


	@Test
	public void testCorporateAction_whenNoDataOrNullFromAvaloq_thenReturnNull() {
		List<CorporateAction> caList =
				avaloqCorporateActionIntegrationServiceImpl
						.loadVoluntaryCorporateActions(new DateTime(), new DateTime(), Arrays.asList(""), serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(caList);

		caList = avaloqCorporateActionIntegrationServiceImpl.
				loadVoluntaryCorporateActionsForSuper(new DateTime(), new DateTime(), Arrays.asList(""), serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(caList);

		caList = avaloqCorporateActionIntegrationServiceImpl.
                                                                    loadVoluntaryCorporateActionsForApproval(new DateTime(), new DateTime(), serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(caList);

		caList =
				avaloqCorporateActionIntegrationServiceImpl
						.loadVoluntaryCorporateActions(null, null, Arrays.asList("", null), serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(caList);

		caList = avaloqCorporateActionIntegrationServiceImpl.
				loadVoluntaryCorporateActionsForSuper(new DateTime(), new DateTime(), null, serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(caList);

		caList = avaloqCorporateActionIntegrationServiceImpl.
                                                                    loadVoluntaryCorporateActionsForApproval(null, null, serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(caList);
	}

	@Test
	public void testMandatoryCorporateActions()
	{
		List<CorporateAction> caList = avaloqCorporateActionIntegrationServiceImpl.
				loadMandatoryCorporateActions(new DateTime(), new DateTime(), Arrays.asList(""), serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(caList);

		caList = avaloqCorporateActionIntegrationServiceImpl.
				loadMandatoryCorporateActions(null, null, null, serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(caList);

		caList = avaloqCorporateActionIntegrationServiceImpl.
				loadMandatoryCorporateActions(null, null, Arrays.asList("", null), serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(caList);

		caList = avaloqCorporateActionIntegrationServiceImpl.
				loadMandatoryCorporateActionsForSuper(null, null, Arrays.asList("", null), serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(caList);
	}

	@Test
	public void testLoadCorporateActionDetails()
	{
		CorporateActionDetailsResponse details =
			avaloqCorporateActionIntegrationServiceImpl.loadCorporateActionDetails("0", serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(details);
	}

	@Test
	public void testLoadCorporateActionAccountDetails()
	{
		CorporateActionAccount account = mock(CorporateActionAccount.class);
		List<CorporateActionAccount> accounts = new ArrayList<>();
		accounts.add(account);

		CorporateActionAccountResponseImpl response = new CorporateActionAccountResponseImpl();
		response.setCorporateActionAccounts(accounts);
		response.setRequestId("0");

		//when(response.getCorporateActionAccounts()).thenReturn(accounts);

		when(avaloqExecute
				.executeReportRequestToDomain(any(AvaloqReportRequest.class), eq(CorporateActionAccountResponseImpl.class),
						any(ServiceErrors.class))).thenReturn(response);

		List<CorporateActionAccount> corporateActionAccounts =
				avaloqCorporateActionIntegrationServiceImpl.loadCorporateActionAccountsDetails("0", serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(corporateActionAccounts);

		//when(response.getCorporateActionAccounts()).thenReturn(null);
		response.setCorporateActionAccounts(null);

		corporateActionAccounts =
				avaloqCorporateActionIntegrationServiceImpl.loadCorporateActionAccountsDetails("0", serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(corporateActionAccounts);

		//when(response.getCorporateActionAccounts()).thenReturn(accounts);
		response.setCorporateActionAccounts(accounts);

		corporateActionAccounts = avaloqCorporateActionIntegrationServiceImpl.
				loadCorporateActionAccountsDetailsForIm("0","0", serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(corporateActionAccounts);
	}

	@Test
	public void testLoadCorporateActionTransactionDetails()
	{
		CorporateActionTransactionDetailsResponseImpl response = mock(CorporateActionTransactionDetailsResponseImpl.class);

		List<CorporateActionTransactionDetails> transactionDetails = new ArrayList<>();

		CorporateActionTransactionDetailsImpl caTransactionDetail = new CorporateActionTransactionDetailsImpl();
		caTransactionDetail.setAccountId("123");
		caTransactionDetail.setPositionId("456");
		caTransactionDetail.setTransactionDescription("buy something");
		caTransactionDetail.setTransactionNumber(Integer.valueOf(555));

		transactionDetails.add(caTransactionDetail);

		when(avaloqExecute
				.executeReportRequestToDomain(any(AvaloqReportRequest.class), eq(CorporateActionTransactionDetailsResponseImpl.class),
						any(ServiceErrors.class))).thenReturn(response);

		when(response.getCorporateActionTransactionDetails()).thenReturn(transactionDetails);

		List<CorporateActionTransactionDetails> corporateActionTransactionDetails =
				avaloqCorporateActionIntegrationServiceImpl.loadCorporateActionTransactionDetails(Arrays.asList("0"), serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(corporateActionTransactionDetails);

		CorporateActionTransactionDetails details = corporateActionTransactionDetails.get(0);
		Assert.assertEquals("123", details.getAccountId());
		Assert.assertEquals("456", details.getPositionId());
		Assert.assertEquals("buy something", details.getTransactionDescription());
		Assert.assertEquals(Integer.valueOf(555), details.getTransactionNumber());

		when(response.getCorporateActionTransactionDetails()).thenReturn(null);

		corporateActionTransactionDetails =
				avaloqCorporateActionIntegrationServiceImpl.loadCorporateActionTransactionDetails(Arrays.asList("0"), serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(corporateActionTransactionDetails);

		corporateActionTransactionDetails = avaloqCorporateActionIntegrationServiceImpl.
				loadCorporateActionTransactionDetailsForIm("0", Arrays.asList("0"), serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(corporateActionTransactionDetails);
	}

	@Test
	public void testLoadDrpCorporateActions()
	{
		List<CorporateAction> corporateActions =
				avaloqCorporateActionIntegrationServiceImpl.loadDrpCorporateActions(new DateTime(), new DateTime(), Arrays.asList(""), serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(corporateActions);

		corporateActions =
				avaloqCorporateActionIntegrationServiceImpl.loadDrpCorporateActions(null, null, null, serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(corporateActions);

		corporateActions =
				avaloqCorporateActionIntegrationServiceImpl.loadDrpCorporateActions(null, null, Arrays.asList("", null), serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(corporateActions);
	}

	@Test
	public void testGetCountForPendingCorporateEvents()
	{
		CorporateAction corporateAction = avaloqCorporateActionIntegrationServiceImpl.getCountForPendingCorporateEvents(
				Arrays.asList(AccountKey.valueOf("0")),
				new DateTime(),
				new DateTime(),
				serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(corporateAction);

		corporateAction = avaloqCorporateActionIntegrationServiceImpl.getCountForPendingCorporateEvents(
				Arrays.asList(AccountKey.valueOf("1")),
				null,
				null,
				serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNull(corporateAction);
	}
}
