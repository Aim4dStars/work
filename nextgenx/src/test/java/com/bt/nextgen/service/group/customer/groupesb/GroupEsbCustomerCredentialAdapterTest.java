package com.bt.nextgen.service.group.customer.groupesb;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.CredentialGroup;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.btfin.panorama.core.security.Roles;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.RetrieveChannelAccessCredentialResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ProviderErrorDetail;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusDetail;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;

public class GroupEsbCustomerCredentialAdapterTest
{

	private RetrieveChannelAccessCredentialResponse retrieveChannelAccessCredentialErrorResponse;
	private InputStream errorStream;
	private static String gcmId = "12344";

	private String errorResponseXML = "<out:retrieveChannelAccessCredentialResponse xmlns:io=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:io3=\"http://www.westpac.com.au/gn/utility/xsd/esbHeader/v3/\"  xmlns:tns=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
		+ "<sh:serviceStatus><sh:statusInfo><sh:level>Error</sh:level><sh:code>309,00010</sh:code><sh:description>No Credential found</sh:description>"
		+ "<sh:referenceId>EAM</sh:referenceId>"
		+ "<sh:statusDetail>"
		+ "<sh:mediationName>MVC0172v03</sh:mediationName><sh:providerErrorDetail><sh:providerErrorCode>invalidAccountFault</sh:providerErrorCode>"
		+ "<sh:providerErrorDescription>Invalid Account.Can't Perform getCredentialStatus Operation.</sh:providerErrorDescription>"
		+ "</sh:providerErrorDetail></sh:statusDetail></sh:statusInfo> </sh:serviceStatus>"
		+ "</out:retrieveChannelAccessCredentialResponse>";


	private String workingResponseNoCredGroup = "    <out:retrieveChannelAccessCredentialResponse xmlns:io=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:io2=\"http://www.westpac.com.au/gn/utility/xsd/statusHandling/v1/\" xmlns:io3=\"http://www.westpac.com.au/gn/utility/xsd/esbHeader/v3/\" xmlns:out=\"http://www.westpac.com.au/gn/channelManagement/services/credentialManagement/xsd/retrieveChannelAccessCredential/v4/SVC0311/\" xmlns:out2=\"http://www.westpac.com.au/gn/common/xsd/identifiers/v1/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
		"      <sh:serviceStatus xmlns:EAM=\"http://v1.0.eam.entity.olb.westpac.com.au\" xmlns:MVC=\"http://www.westpac.com.au/gn/MediationService/MVC0172/v03\" xmlns:sh=\"http://www.westpac.com.au/gn/utility/xsd/statusHandling/v1/\">\n" +
		"        <sh:statusInfo>\n" +
		"          <sh:level>Success</sh:level>\n" +
		"          <sh:code>000,00000</sh:code>\n" +
		"        </sh:statusInfo>\n" +
		"      </sh:serviceStatus>\n" +
		"      <userCredential>\n" +
		"        <credentialType>ONL</credentialType>\n" +
		"        <channel>\n" +
		"          <channelType>Online</channelType>\n" +
		"        </channel>\n" +
		"        <userName>\n" +
		"          <userId>201603416</userId>\n" +
		"          <userAlias>sausages</userAlias>\n" +
		"        </userName>\n" +
		"        <internalIdentifier>\n" +
		"          <out2:credentialId>8e5eb7d8-2dce-11e4-a367-32784669e80a</out2:credentialId>\n" +
		"        </internalIdentifier>\n" +
		"        <isCredentialOf>\n" +
		"          <hasBrandSilo>\n" +
		"            <brandSiloCode>WPAC</brandSiloCode>\n" +
		"          </hasBrandSilo>\n" +
		"        </isCredentialOf>\n" +
		"        <lastUsedDate>2015-03-12</lastUsedDate>\n" +
		"        <lastUsedTime>10:12:23Z</lastUsedTime>\n" +
		"        <lifecycleStatus>\n" +
		"          <status>ACTV</status>\n" +
		"          <startTimestamp>2015-03-12T10:57:32Z</startTimestamp>\n" +
		"        </lifecycleStatus>\n" +
		"        <sourceSystem>EAM</sourceSystem>\n" +
		"      </userCredential>\n" +
		"    </out:retrieveChannelAccessCredentialResponse>\n";

	private static final String workingResponse = "<out:retrieveChannelAccessCredentialResponse xmlns:io=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:io2=\"http://www.westpac.com.au/gn/utility/xsd/statusHandling/v1/\" xmlns:io3=\"http://www.westpac.com.au/gn/utility/xsd/esbHeader/v3/\" xmlns:out=\"http://www.westpac.com.au/gn/channelManagement/services/credentialManagement/xsd/retrieveChannelAccessCredential/v4/SVC0311/\" xmlns:out2=\"http://www.westpac.com.au/gn/common/xsd/identifiers/v1/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
		"      <sh:serviceStatus xmlns:EAM=\"http://v1.0.eam.entity.olb.westpac.com.au\" xmlns:MVC=\"http://www.westpac.com.au/gn/MediationService/MVC0172/v03\" xmlns:sh=\"http://www.westpac.com.au/gn/utility/xsd/statusHandling/v1/\">\n" +
		"        <sh:statusInfo>\n" +
		"          <sh:level>Success</sh:level>\n" +
		"          <sh:code>000,00000</sh:code>\n" +
		"        </sh:statusInfo>\n" +
		"      </sh:serviceStatus>\n" +
		"      <userCredential>\n" +
		"        <credentialType>ONL</credentialType>\n" +
		"        <credentialGroup>\n" +
		"          <credentialGroupType>bt-adviser</credentialGroupType>\n" +
		"        </credentialGroup>\n" +
		"        <channel>\n" +
		"          <channelType>Online</channelType>\n" +
		"        </channel>\n" +
		"        <userName>\n" +
		"          <userId>217186501</userId>\n" +
		"          <userAlias>sausages1</userAlias>\n" +
		"          <userAliasCreatedTimestamp>2015-03-10T05:43:46Z</userAliasCreatedTimestamp>\n" +
		"        </userName>\n" +
		"        <internalIdentifier>\n" +
		"          <out2:credentialId>5be6baf0-8d0f-4652-b2b0-7bb6e617f3c1</out2:credentialId>\n" +
		"        </internalIdentifier>\n" +
		"        <isCredentialOf>\n" +
		"          <hasBrandSilo>\n" +
		"            <brandSiloCode>WPAC</brandSiloCode>\n" +
		"          </hasBrandSilo>\n" +
		"        </isCredentialOf>\n" +
		"        <lastUsedDate>2015-03-12</lastUsedDate>\n" +
		"        <lastUsedTime>10:22:23Z</lastUsedTime>\n" +
		"        <lifecycleStatus>\n" +
		"          <status>ACTV</status>\n" +
		"          <startTimestamp>2015-03-12T11:09:04Z</startTimestamp>\n" +
		"        </lifecycleStatus>\n" +
		"        <sourceSystem>EAM</sourceSystem>\n" +
		"      </userCredential>\n" +
		"    </out:retrieveChannelAccessCredentialResponse>\n";

	@Before
	public void setUp() throws Exception
	{
		retrieveChannelAccessCredentialErrorResponse = new RetrieveChannelAccessCredentialResponse();
		StatusInfo statusInfo = new StatusInfo();
		statusInfo.setLevel(Level.ERROR);
		statusInfo.setCode("309,00010");
		statusInfo.setDescription("No Credential found");
		statusInfo.setReferenceId("EAM");
		
		StatusDetail statusDetail = new StatusDetail();
		statusDetail.setMediationName("MVC0172v03");
		
		ProviderErrorDetail errorDetail = new ProviderErrorDetail();
		errorDetail.setProviderErrorCode("invalidAccountFault");
		errorDetail.setProviderErrorDescription("Invalid Account.Can't Perform getCredentialStatus Operation.");

		statusDetail.getProviderErrorDetail().add(errorDetail);

		statusInfo.getStatusDetail().add(statusDetail);
		ServiceStatus serviceStatus = new ServiceStatus();
		serviceStatus.getStatusInfo().add(statusInfo);
		retrieveChannelAccessCredentialErrorResponse.setServiceStatus(serviceStatus);
	}

	//TODO : Fix this once the version of Gesb svc0311 is finalised
	@Ignore
	@Test
	public void testGroupEsbCustomerCredentialAdapterForNullResponse()
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerCredentialInformation customerCredentialInformation = new GroupEsbCustomerCredentialAdapter(retrieveChannelAccessCredentialErrorResponse,
			gcmId,
			serviceErrors);
		assertTrue(serviceErrors.hasErrors());
		assertEquals("The Credential Response was null", serviceErrors.getFirstError());
		assertNotNull(customerCredentialInformation);
		assertNull(customerCredentialInformation.getCredentialId());

	}

	@Test
	public void testGroupEsbCustomerCredentialAdapterForNullId()
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerCredentialInformation customerCredentialInformation = new GroupEsbCustomerCredentialAdapter(retrieveChannelAccessCredentialErrorResponse,
			null,
			serviceErrors);
		assertTrue(serviceErrors.hasErrors());
		assertEquals("No customer ID , cannot find the credential", serviceErrors.getErrorList().iterator().next().getMessage());
		assertNotNull(customerCredentialInformation);
		assertNull(customerCredentialInformation.getCredentialId());

	}

	//TODO : Fix this
	@Ignore
	@Test
	public void testGroupEsbCustomerCredentialAdapterForErrorRsp()
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerCredentialInformation customerCredentialInformation = new GroupEsbCustomerCredentialAdapter(retrieveChannelAccessCredentialErrorResponse,
			gcmId,
			serviceErrors);
		assertTrue(serviceErrors.hasErrors());
		assertEquals("The credential query failed", serviceErrors.getErrorList().iterator().next().getMessage());
		assertEquals(customerCredentialInformation.getStatusInfo(), "309,00010");
		assertNotNull(customerCredentialInformation);
		assertNull(customerCredentialInformation.getUsername());
		assertNull(customerCredentialInformation.getCredentialId());
	}

    @Test
    public void testGetCredentialRole () {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        GroupEsbCustomerCredentialAdapter groupEsbCustomerCredentialAdapter = new GroupEsbCustomerCredentialAdapter(retrieveChannelAccessCredentialErrorResponse,
                gcmId,
                serviceErrors);
        List<CredentialGroup> groups = new ArrayList<>();
        CredentialGroup group = new CredentialGroup();
        group.setCredentialGroupType("bt-role_adviser");
        groups.add(group);

        group = new CredentialGroup();
        group.setCredentialGroupType("bt-role_investor");
        groups.add(group);

        group = new CredentialGroup();
        group.setCredentialGroupType("abcdefag");
        groups.add(group);

        List<Roles> roles = groupEsbCustomerCredentialAdapter.getCredentialRoles(groups);
        assertNotNull(roles);
        assertEquals(roles.size(), 2);
        assertEquals(roles.get(0) , Roles.ROLE_ADVISER);
        assertEquals(roles.get(1) , Roles.ROLE_INVESTOR);
    }
/*
	--------------Tests Removed due to JaxbUnmarshall Issue --------------------

	@Test
	public void testGroupEsbCustomerCredentialAdapterForMissingCredGroup() throws Exception
	{
		RetrieveChannelAccessCredentialResponse channelAccessCredentialResponse = JaxbUnmarshallTestUtil.unmarshallSimpleObject(workingResponseNoCredGroup, RetrieveChannelAccessCredentialResponse.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerCredentialInformation customerCredentialInformation = new GroupEsbCustomerCredentialAdapter(channelAccessCredentialResponse,
			"201603416",
			serviceErrors);
		assertThat(customerCredentialInformation,is(not(nullValue())));
		assertThat(customerCredentialInformation.getCredentialGroups(),is(nullValue()));
		assertThat(customerCredentialInformation.getCredentialId(), is ("8e5eb7d8-2dce-11e4-a367-32784669e80a"));
		assertThat(customerCredentialInformation.getBankReferenceId(),is("201603416"));
		assertThat(customerCredentialInformation.getUsername(),is("sausages"));

	}


	@Test
	public void testGroupEsbCustomerCredentialAdapterForGoodResponse() throws Exception
	{
		RetrieveChannelAccessCredentialResponse channelAccessCredentialResponse = JaxbUnmarshallTestUtil.unmarshallSimpleObject(workingResponse, RetrieveChannelAccessCredentialResponse.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerCredentialInformation customerCredentialInformation = new GroupEsbCustomerCredentialAdapter(channelAccessCredentialResponse,
			"217186501",
			serviceErrors);
		assertThat(customerCredentialInformation,is(not(nullValue())));
		assertThat(customerCredentialInformation.getCredentialGroups(),is(not(nullValue())));
		assertThat(customerCredentialInformation.getCredentialGroups().size(),is(1));
		assertThat(customerCredentialInformation.getCredentialGroups().get(0),is(Roles.ROLE_ADVISER));
		assertThat(customerCredentialInformation.getCredentialId(), is ("5be6baf0-8d0f-4652-b2b0-7bb6e617f3c1"));
		assertThat(customerCredentialInformation.getBankReferenceId(),is("217186501"));
		assertThat(customerCredentialInformation.getUsername(),is("sausages1"));

	}
*/
}
