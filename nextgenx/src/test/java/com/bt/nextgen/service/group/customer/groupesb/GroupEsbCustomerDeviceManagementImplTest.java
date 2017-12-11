package com.bt.nextgen.service.group.customer.groupesb;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.Agent;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.AgentRoleType;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.Individual;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.InvolvedPartyNetwork;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.InvolvedPartyRoleType;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.maintainmfadevicearrangement.v1.svc0276.MaintainMFADeviceArrangementRequest;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.maintainmfadevicearrangement.v1.svc0276.MaintainMFADeviceArrangementResponse;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.maintainmfadevicearrangement.v1.svc0276.UpdateAction;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.util.SamlUtil;
import com.bt.nextgen.web.controller.cash.util.Attribute;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;

@RunWith(MockitoJUnitRunner.class)
public class GroupEsbCustomerDeviceManagementImplTest
{

	@InjectMocks
	private GroupEsbCustomerDeviceManagementImpl deviceManagementIntegrationService;

	@Mock
	private WebServiceProvider provider;

	@Mock
	private BankingAuthorityService userSamlService;

	private String mobileNumber = "123456";
	private String safiDeviceId = "61481207035";
	private String gcmId = "1452162";
	private String deviceProvisioningStatus = ServiceConstants.PROVISIONING_STATUS_ACTIVE;
	private String userId = "876282";

	@Before
	public void setUp()
	{
		Mockito.doAnswer(new Answer <SamlToken>()
		{
			@Override
			public SamlToken answer(InvocationOnMock invocation) throws Throwable
			{
				return new SamlToken(SamlUtil.loadSaml());
			}

		}).when(userSamlService).getSamlToken();
	}

	@Test
	public void testCreateMFADeviceArrangementRequestUnblockMobile_ForActiveUser()
	{
		MaintainMFADeviceArrangementRequest request = deviceManagementIntegrationService.createMFADeviceArrangementRequest(mobileNumber,
			safiDeviceId,
			gcmId,
			deviceProvisioningStatus);
		assertThat(request.getRequestedAction(), Is.is(UpdateAction.UPDATE_AUTHENTICATION_DEVICE));
		assertNotNull(request.getArrangement());
		assertNotNull(request.getArrangement().getHasMaintenanceTransaction());
		assertNotNull(request.getArrangement().getHasMaintenanceTransaction().getIsInitiatedBy());
		Agent agent = (Agent)request.getArrangement().getHasMaintenanceTransaction().getIsInitiatedBy();
		assertNotNull(agent);
		assertThat(agent.getExternalIdentifier().getEmployeeNumber(), Is.is(gcmId));
		assertThat(agent.getAgentRoleType(), Is.is(AgentRoleType.NON_FRAUD));
		assertThat(request.getArrangement().getHasMaintenanceTransaction().getMaintenanceTransactionType(),
			Is.is(ServiceConstants.MAINTENANCE_TRANSACTION_TYPE));
		assertThat(request.getArrangement().getHasMaintenanceTransaction().getMaintenanceReasonCode(), Is.is("Administration"));

		assertThat(request.getArrangement().getHasBrand().getBrandCode(), Is.is(ServiceConstants.BRAND_CODE));
		assertThat(request.getArrangement().getInternalIdentifier().getArrangementId(), Is.is(safiDeviceId));

		assertNotNull(request.getArrangement().getHasAuthenticationAuditContext());
		assertThat(request.getArrangement().getProvisioningStatus(), Is.is(ServiceConstants.PROVISIONING_STATUS_ACTIVE));
		assertNull(request.getArrangement().getIsLinkedToSecurityDevice());
		/*	assertNotNull(request.getArrangement().getIsLinkedToSecurityDevice());

			assertThat(request.getArrangement().getIsLinkedToSecurityDevice().getHasParameter().get(0).getName(),
				Is.is(ServiceConstants.SMSOTP_PHONE_NUMBER));
			assertThat(request.getArrangement().getIsLinkedToSecurityDevice().getHasParameter().get(0).getValue(),
				Is.is(mobileNumber));*/
	}

	@Test
	public void testCreateMFADeviceArrangementRequestUnblockMobile_ForNotActiveUser()
	{
		MaintainMFADeviceArrangementRequest request = deviceManagementIntegrationService.createMFADeviceArrangementRequest(mobileNumber,
			safiDeviceId,
			gcmId,
			"");
		assertThat(request.getRequestedAction(), Is.is(UpdateAction.UPDATE_AUTHENTICATION_DEVICE));
		assertNotNull(request.getArrangement());
		assertNotNull(request.getArrangement().getHasMaintenanceTransaction());
		assertNotNull(request.getArrangement().getHasMaintenanceTransaction().getIsInitiatedBy());
		Agent agent = (Agent)request.getArrangement().getHasMaintenanceTransaction().getIsInitiatedBy();
		assertNotNull(agent);
		assertThat(agent.getExternalIdentifier().getEmployeeNumber(), Is.is(gcmId));
		assertThat(agent.getAgentRoleType(), Is.is(AgentRoleType.NON_FRAUD));
		assertThat(request.getArrangement().getHasMaintenanceTransaction().getMaintenanceTransactionType(),
			Is.is(ServiceConstants.MAINTENANCE_TRANSACTION_TYPE));
		assertThat(request.getArrangement().getHasMaintenanceTransaction().getMaintenanceReasonCode(), Is.is("Administration"));

		assertThat(request.getArrangement().getHasBrand().getBrandCode(), Is.is(ServiceConstants.BRAND_CODE));
		assertThat(request.getArrangement().getInternalIdentifier().getArrangementId(), Is.is(safiDeviceId));

		assertNotNull(request.getArrangement().getHasAuthenticationAuditContext());
		//		assertThat(request.getArrangement().getProvisioningStatus(), Is.is(ServiceConstants.PROVISIONING_STATUS_ACTIVE));
		assertNull(request.getArrangement().getProvisioningStatus());
		assertNotNull(request.getArrangement().getIsLinkedToSecurityDevice());

			assertThat(request.getArrangement().getIsLinkedToSecurityDevice().getHasParameter().get(0).getName(),
				Is.is(ServiceConstants.SMSOTP_PHONE_NUMBER));
			assertThat(request.getArrangement().getIsLinkedToSecurityDevice().getHasParameter().get(0).getValue(),
			Is.is(mobileNumber));
	}

	@Test
	public void testUpdateUserMobileNumber_ForErrorResponse() throws Exception
	{
		stubErrorResponse();
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerCredentialManagementInformation customerInformation = deviceManagementIntegrationService.updateUserMobileNumber(mobileNumber,
			safiDeviceId,
			gcmId,
			deviceProvisioningStatus,
			serviceErrors);

		assertNotNull(customerInformation);
		assertThat(customerInformation.getServiceLevel(), Is.is(Attribute.ERROR_MESSAGE));
		assertThat(customerInformation.getServiceStatusErrorCode(), Is.is("ERR_INVALID_INPUT_PARAMETERS"));
		assertThat(serviceErrors.getErrorList().iterator().next().getErrorCode(), Is.is("309,00018"));
		assertThat(serviceErrors.getErrorList().iterator().next().getReason(),
 Is.is("Invalid input parameters"));
		assertThat(serviceErrors.getErrorList().iterator().next().getType(), Is.is("ERR_INVALID_INPUT_PARAMETERS"));
		assertThat(serviceErrors.getErrorList().iterator().next().getService(),
			Is.is("Group-ESB MaintainMFADeviceArrangement Service (svc0276)"));
	}

	@Test
	public void testUpdateUserMobileNumber_ForSuccessResponse() throws Exception
	{

		stubSuccessResponse();
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerCredentialManagementInformation customerInformation = deviceManagementIntegrationService.updateUserMobileNumber(mobileNumber,
			safiDeviceId,
			gcmId,
			deviceProvisioningStatus,
			serviceErrors);

		assertNotNull(customerInformation);
		assertThat(customerInformation.getServiceLevel(), Is.is(Attribute.SUCCESS_MESSAGE));
	}

	@Test
	public void testMFADeviceArrangementReqUnblockMobile_ForNetworkType()
	{
		MaintainMFADeviceArrangementRequest request = deviceManagementIntegrationService.createMFADeviceArrangementRequestUnblockMobile(userId,
			safiDeviceId,
			gcmId,
			"NETWORK");
		
		assertThat(request.getRequestedAction(), Is.is(UpdateAction.UPDATE_AUTHENTICATION_DEVICE));
		assertNotNull(request.getArrangement());
		assertNotNull(request.getArrangement().getHasMaintenanceTransaction());
		assertNotNull(request.getArrangement().getHasMaintenanceTransaction().getIsInitiatedBy());
		Agent agent = (Agent)request.getArrangement().getHasMaintenanceTransaction().getIsInitiatedBy();
		assertNotNull(agent);
		assertThat(agent.getExternalIdentifier().getEmployeeNumber(), Is.is(gcmId));
		assertThat(agent.getAgentRoleType(), Is.is(AgentRoleType.NON_FRAUD));
		assertThat(request.getArrangement().getHasMaintenanceTransaction().getMaintenanceTransactionType(),
			Is.is(ServiceConstants.MAINTENANCE_TRANSACTION_TYPE));
		assertThat(request.getArrangement().getHasMaintenanceTransaction().getMaintenanceReasonCode(), Is.is("ADMINISTRATIVE_OTHER"));

		assertThat(request.getArrangement().getHasBrand().getBrandCode(), Is.is(ServiceConstants.BRAND_CODE));
		assertThat(request.getArrangement().getInternalIdentifier().getArrangementId(), Is.is(safiDeviceId));

		assertThat(request.getArrangement().getHasArrangementRole().get(0).getRoleType(), Is.is(InvolvedPartyRoleType.USER));
		assertNull(request.getArrangement().getAuthenticationStatus());
		assertThat(request.getArrangement()
			.getHasArrangementRole()
			.get(0)
			.getHasAccessCondition()
			.get(0)
			.getHasArrangementAuthority()
			.get(0)
			.getLifecycleStatus(), Is.is(ServiceConstants.PROVISIONING_STATUS_ACTIVE));
		Individual individual = (Individual)request.getArrangement()
			.getHasArrangementRole()
			.get(0)
			.getHasAccessCondition()
			.get(0)
			.getHasArrangementAuthority()
			.get(0)
			.getIsGrantedBy();
		//assertThat(individual.getExternalIdentifier().getCustomerNumber(), Is.is(userId));
		InvolvedPartyNetwork involvedPartyNetwork = request.getArrangement()
			.getHasArrangementRole()
			.get(0)
			.getHasAccessCondition()
			.get(0)
			.getHasArrangementAuthority()
			.get(0)
			.getIsResultOf();
		assertThat(involvedPartyNetwork.getInternalIdentifier().getGroupId(), Is.is(userId));
	}

	@Test
	public void testMFADeviceArrangementReqUnblockMobile_ForDeviceType()
	{
		MaintainMFADeviceArrangementRequest request = deviceManagementIntegrationService.createMFADeviceArrangementRequestUnblockMobile(userId,
			safiDeviceId,
			gcmId,
			"DEVICE");

		assertThat(request.getRequestedAction(), Is.is(UpdateAction.UPDATE_AUTHENTICATION_DEVICE));
		assertNotNull(request.getArrangement());
		assertNotNull(request.getArrangement().getHasMaintenanceTransaction());
		assertNotNull(request.getArrangement().getHasMaintenanceTransaction().getIsInitiatedBy());
		Agent agent = (Agent)request.getArrangement().getHasMaintenanceTransaction().getIsInitiatedBy();
		assertNotNull(agent);
		assertThat(agent.getExternalIdentifier().getEmployeeNumber(), Is.is(gcmId));
		assertThat(agent.getAgentRoleType(), Is.is(AgentRoleType.NON_FRAUD));
		assertThat(request.getArrangement().getHasMaintenanceTransaction().getMaintenanceTransactionType(),
			Is.is(ServiceConstants.MAINTENANCE_TRANSACTION_TYPE));
		assertThat(request.getArrangement().getHasMaintenanceTransaction().getMaintenanceReasonCode(), Is.is("ADMINISTRATIVE_OTHER"));

		assertThat(request.getArrangement().getHasBrand().getBrandCode(), Is.is(ServiceConstants.BRAND_CODE));
		assertThat(request.getArrangement().getInternalIdentifier().getArrangementId(), Is.is(safiDeviceId));

		//assertThat(request.getArrangement().getHasArrangementRole().get(0).getRoleType(), Is.is(InvolvedPartyRoleType.USER));
		assertThat(request.getArrangement().getAuthenticationStatus(), Is.is(ServiceConstants.AUTHENTICATION_STATUS_ACT));
		//assertThat(individual.getExternalIdentifier().getCustomerNumber(), Is.is(userId));
	}

	private void stubErrorResponse() throws Exception
	{
		String errorResponseXML = "<SVC:maintainMFADeviceArrangementResponse xmlns:SVC=\"http://www.westpac.com.au/gn/resourceItemManagement/services/deviceManagement/xsd/maintainMFADeviceArrangement/v1/SVC0276/\">"
			+ "<sh:serviceStatus xmlns:sh=\"http://www.westpac.com.au/gn/utility/xsd/statusHandling/v1/\">"
			+ "<sh:statusInfo><sh:level>Error</sh:level><sh:code>309,00018</sh:code><sh:description>Invalid input parameters</sh:description><sh:statusDetail><sh:mediationName>MVC0162v01</sh:mediationName><sh:providerErrorDetail>"
			+ "<sh:providerErrorCategory/><sh:providerErrorCode>ERR_INVALID_INPUT_PARAMETERS</sh:providerErrorCode><sh:providerErrorDescription>Only one update action allowed per update authentication device call</sh:providerErrorDescription>"
			+ "</sh:providerErrorDetail></sh:statusDetail></sh:statusInfo></sh:serviceStatus></SVC:maintainMFADeviceArrangementResponse>";
		InputStream failStream = new ByteArrayInputStream(errorResponseXML.getBytes("UTF-8"));
		MaintainMFADeviceArrangementResponse maintainMFADeviceArrangementErrorResponse = JaxbUtil.unmarshall(failStream,
			MaintainMFADeviceArrangementResponse.class);
		final CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(),
			maintainMFADeviceArrangementErrorResponse);

		Mockito.doAnswer(new Answer <CorrelatedResponse>()
		{
			@Override
			public CorrelatedResponse answer(InvocationOnMock invocation) throws Throwable
			{
				return correlatedResponse;
			}

		})
			.when(provider)
			.sendWebServiceWithSecurityHeaderAndResponseCallback(isA(SamlToken.class),
				anyString(),
				anyObject(),
				any(ServiceErrorsImpl.class));
	}

	private void stubSuccessResponse() throws Exception
	{
		String responseXML = "<SVC:maintainMFADeviceArrangementResponse xmlns:SVC=\"http://www.westpac.com.au/gn/resourceItemManagement/services/deviceManagement/xsd/maintainMFADeviceArrangement/v1/SVC0276/\">"
			+ "<sh:serviceStatus xmlns:sh=\"http://www.westpac.com.au/gn/utility/xsd/statusHandling/v1/\">"
			+ "<sh:statusInfo><sh:level>Success</sh:level><sh:code>000000</sh:code></sh:statusInfo></sh:serviceStatus></SVC:maintainMFADeviceArrangementResponse>";
		InputStream inputStream = new ByteArrayInputStream(responseXML.getBytes("UTF-8"));
		MaintainMFADeviceArrangementResponse maintainMFADeviceArrangementErrorResponse = JaxbUtil.unmarshall(inputStream,
			MaintainMFADeviceArrangementResponse.class);
		final CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(),
			maintainMFADeviceArrangementErrorResponse);

		Mockito.doAnswer(new Answer <CorrelatedResponse>()
		{
			@Override
			public CorrelatedResponse answer(InvocationOnMock invocation) throws Throwable
			{
				return correlatedResponse;
			}

		})
			.when(provider)
			.sendWebServiceWithSecurityHeaderAndResponseCallback(isA(SamlToken.class),
				anyString(),
				anyObject(),
				any(ServiceErrorsImpl.class));
	}

}
