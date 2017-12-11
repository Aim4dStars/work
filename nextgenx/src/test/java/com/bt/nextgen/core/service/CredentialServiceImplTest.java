package com.bt.nextgen.core.service;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.CredentialGroup;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.RetrieveChannelAccessCredentialResponse;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.UserAlternateAliasCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.UserCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.UserNameAliasCredentialDocument;
import au.com.westpac.gn.common.xsd.identifiers.v1.CredentialIdentifier;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.group.customer.CredentialRequest;
import com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation;
import com.bt.nextgen.service.group.customer.CustomerLoginManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.GroupEsbCustomerCredentialAdapter;
import com.bt.nextgen.serviceops.model.UserAccountStatusModel;
import com.btfin.panorama.core.security.UserAccountStatus;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CredentialServiceImplTest
{
	private WebServiceProvider provider;
	private CredentialService credentialService;
	private BankingAuthorityService userSamlService;

	private CustomerLoginManagementIntegrationService customerLoginManagement;
	
	@Before
	public void setup()
	{
        credentialService = new CredentialServiceImpl();
        customerLoginManagement = mock(CustomerLoginManagementIntegrationService.class);
        ReflectionTestUtils.setField(credentialService, "customerLoginManagement", customerLoginManagement);
        /*
         * credentialService = new CredentialServiceImpl(); provider =
         * mock(WebServiceProvider.class); userSamlService =
         * mock(BankingAuthorityService.class);
         * ReflectionTestUtils.setField(credentialService, "userSamlService",
         * userSamlService); when(userSamlService.getSamlToken()).thenReturn(new
         * SamlToken("")); ReflectionTestUtils.setField(credentialService,
         * "provider", provider); customerLoginManagement =
         * mock(CustomerLoginManagementIntegrationService.class);
         * ReflectionTestUtils.setField(credentialService,
         * "customerLoginManagement", customerLoginManagement);
         */
	}

	@Test
	@Ignore
	public void testLookupStatusWithErrorCode() throws Exception
	{
		String userId = "11969";
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		RetrieveChannelAccessCredentialResponse response = new RetrieveChannelAccessCredentialResponse();
		ServiceStatus serviceStatus = new ServiceStatus();
		StatusInfo statusInfo = new StatusInfo();
		statusInfo.setLevel(Level.SUCCESS);
		statusInfo.setCode("309,00008");
		serviceStatus.getStatusInfo().add(statusInfo);
		
		UserCredentialDocument userCredentialDocument = new UserCredentialDocument();
		UserNameAliasCredentialDocument userNameAliasCredentialDocument = new UserNameAliasCredentialDocument();
		userNameAliasCredentialDocument.setUserAlias("userAlias");
		userNameAliasCredentialDocument.setUserId("11969");
		userCredentialDocument.setUserName(userNameAliasCredentialDocument);
		CredentialIdentifier credentialIdentifier = new CredentialIdentifier();
		credentialIdentifier.setCredentialId("11969");
		userCredentialDocument.setInternalIdentifier(credentialIdentifier);
		response.getUserCredential().add(userCredentialDocument);
		response.setServiceStatus(serviceStatus);
		
		when(provider.sendWebServiceWithSecurityHeader(any(SamlToken.class), anyString(), anyObject())).thenReturn(response);
		CustomerCredentialInformation CustomerCredentialInformation = new GroupEsbCustomerCredentialAdapter(response,
			userId,
			serviceErrors);
		when(customerLoginManagement.getCustomerInformation(any(CredentialRequest.class), any(ServiceErrorsImpl.class))).thenReturn(CustomerCredentialInformation);

		UserAccountStatusModel userAccountStatus = credentialService.lookupStatus(userId, new ServiceErrorsImpl());
		Assert.assertThat(userAccountStatus, IsNull.notNullValue());
		Assert.assertThat(userAccountStatus.getUserAccountStatus(), CoreMatchers.equalTo(UserAccountStatus.ACCOUNT_CREATION_INCOMPLETE));
	}

    @Test
    public void testGetZnumberForInvestor() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        String expectedZNumber = "92773955";
        String userId = "201665513";
        RetrieveChannelAccessCredentialResponse response = new RetrieveChannelAccessCredentialResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setLevel(Level.SUCCESS);
        statusInfo.setCode("309,00008");
        serviceStatus.getStatusInfo().add(statusInfo);
        UserCredentialDocument userCredentialDocument = new UserCredentialDocument();
        UserNameAliasCredentialDocument userNameAliasCredentialDocument = new UserNameAliasCredentialDocument();
        UserAlternateAliasCredentialDocument userAlternateAliasCredentialDocument = new UserAlternateAliasCredentialDocument();
        userNameAliasCredentialDocument.setUserAlias("userAlias");
        userNameAliasCredentialDocument.setUserId("92773955");
        userAlternateAliasCredentialDocument.setUserId(userId);

        CredentialGroup credentialGroup = new CredentialGroup();
        credentialGroup.setCredentialGroupType("olb_base_role");
        CredentialGroup credentialGroup1 = new CredentialGroup();
        credentialGroup1.setCredentialGroupType("bt-investor");

        userNameAliasCredentialDocument.setHasAlternateUserNameAlias(userAlternateAliasCredentialDocument);
        userCredentialDocument.setUserName(userNameAliasCredentialDocument);
        userCredentialDocument.getCredentialGroup().add(credentialGroup);
        userCredentialDocument.getCredentialGroup().add(credentialGroup1);
        response.getUserCredential().add(userCredentialDocument);
        response.setServiceStatus(serviceStatus);

        CustomerCredentialInformation CustomerCredentialInformation = new GroupEsbCustomerCredentialAdapter(response, userId,
                serviceErrors);
        when(customerLoginManagement.getDirectCustomerInformation(any(CredentialRequest.class), any(ServiceErrorsImpl.class)))
                .thenReturn(CustomerCredentialInformation);

        String zNumber = credentialService.getZnumberForInvestor("201665513");
        Assert.assertThat(zNumber, IsNull.notNullValue());
        Assert.assertThat(zNumber, CoreMatchers.equalTo(expectedZNumber));
    }

    @Test
    public void testGetZnumberForAdviser() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        String userId = "201665513";
        RetrieveChannelAccessCredentialResponse response = new RetrieveChannelAccessCredentialResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setLevel(Level.SUCCESS);
        statusInfo.setCode("309,00008");
        serviceStatus.getStatusInfo().add(statusInfo);
        UserCredentialDocument userCredentialDocument = new UserCredentialDocument();
        UserNameAliasCredentialDocument userNameAliasCredentialDocument = new UserNameAliasCredentialDocument();
        UserAlternateAliasCredentialDocument userAlternateAliasCredentialDocument = new UserAlternateAliasCredentialDocument();
        userNameAliasCredentialDocument.setUserAlias("userAlias");
        userNameAliasCredentialDocument.setUserId("92773955");
        userAlternateAliasCredentialDocument.setUserId(userId);

        CredentialGroup credentialGroup = new CredentialGroup();
        credentialGroup.setCredentialGroupType("olb_base_role");
        CredentialGroup credentialGroup1 = new CredentialGroup();
        credentialGroup1.setCredentialGroupType("bt-adviser");

        userNameAliasCredentialDocument.setHasAlternateUserNameAlias(userAlternateAliasCredentialDocument);
        userCredentialDocument.setUserName(userNameAliasCredentialDocument);
        userCredentialDocument.getCredentialGroup().add(credentialGroup);
        userCredentialDocument.getCredentialGroup().add(credentialGroup1);
        response.getUserCredential().add(userCredentialDocument);
        response.setServiceStatus(serviceStatus);

        CustomerCredentialInformation CustomerCredentialInformation = new GroupEsbCustomerCredentialAdapter(response, userId,
                serviceErrors);
        when(customerLoginManagement.getDirectCustomerInformation(any(CredentialRequest.class), any(ServiceErrorsImpl.class)))
                .thenReturn(CustomerCredentialInformation);

        String zNumber = credentialService.getZnumberForInvestor("201665513");
        Assert.assertThat(zNumber, IsNull.notNullValue());
        Assert.assertThat(zNumber, CoreMatchers.equalTo(""));
    }

    @Test
    public void testGetPPID() throws  Exception{
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

    }


}
