package com.bt.nextgen.service.group.customer.groupesb.v11;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;

import com.bt.nextgen.cms.service.CmsService;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequestImpl;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.serviceops.repository.GcmAuditRepository;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class GroupEsbCustomerManagementV11ImplTest {

	@Mock
	private WebServiceProvider provider;

	@Mock(name = "userDetailsService")
	private BankingAuthorityService userSamlService;

	@Mock
	private CmsService cmsService;

	@Mock
	private GcmAuditRepository gcmAuditRepository;

	@Mock
	private UserProfileService userProfileService;

	@Mock
	RetrieveDetailsAndArrangementRelationshipsForIPsResponse responseObject;

	@Mock
	CustomerRawData rawData;

	@InjectMocks
	private GroupEsbCustomerManagementV11Impl groupEsbCustomerManagementV11;

	@Mock
	private ServiceStatus serviceStatus;

	@Mock
	Individual individual;

	private static final String[] OPERATION_TYPES = { "ID", "IP", "IPF", "ICA", "ICAF", "IC", "IA", "AAF", "IPOS", "ICR", "IAN", "ANAF", "AMLC" };

	private void runCommonMockServices() throws FileNotFoundException {
		SamlToken samlToken = new SamlToken(SamlUtil.loadSaml());
		when(userSamlService.getSamlToken()).thenReturn(samlToken);

		CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), responseObject);
		when(provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(), anyObject(), any(ServiceErrors.class)))
				.thenReturn(correlatedResponse);
		when(responseObject.getServiceStatus()).thenReturn(serviceStatus);
		when(serviceStatus.getStatusInfo()).thenReturn(Arrays.asList(getStatus(Level.SUCCESS)));
		when(responseObject.getIndividual()).thenReturn(individual);
	}

	private StatusInfo getStatus(Level level) {
		StatusInfo status = new StatusInfo();
		status.setLevel(level);
		return status;
	}

	@Test
	public void test_retrieveCustomerRawInformation_success() throws FileNotFoundException {
		runCommonMockServices();
		List<InvolvedPartyIdentifier> involvedPartyIdentifier = Arrays.asList(getInvolvedPartyIdentifier());
		when(individual.getInvolvedPartyIdentifier()).thenReturn(involvedPartyIdentifier);

		CustomerManagementRequest request = createCustomerManagementRequest(CustomerManagementOperation.values());

		when(rawData.getRawResponse()).thenReturn("sample response just to check not null");

		groupEsbCustomerManagementV11.retrieveCustomerRawInformation(request, Arrays.asList(OPERATION_TYPES), new ServiceErrorsImpl());

		Assert.assertTrue(null != rawData.getRawResponse());
	}

	private InvolvedPartyIdentifier getInvolvedPartyIdentifier() {
		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
		involvedPartyIdentifier.setInvolvedPartyId("66786610081");
		return involvedPartyIdentifier;
	}

	private CustomerManagementRequest createCustomerManagementRequest(CustomerManagementOperation... operations) {
		CustomerManagementRequest request = new CustomerManagementRequestImpl();
		request.setOperationTypes(Arrays.asList(operations));
		request.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
		request.setCISKey(CISKey.valueOf("66786610081"));
		return request;
	}
}
