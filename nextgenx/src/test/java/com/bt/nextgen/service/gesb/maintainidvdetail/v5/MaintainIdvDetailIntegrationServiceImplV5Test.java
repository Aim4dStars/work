/**
 * 
 */
package com.bt.nextgen.service.gesb.maintainidvdetail.v5;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import au.com.westpac.gn.common.xsd.identifiers.v1.EmployeeIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.Agent;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.IndividualIDVAssessment;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.IndividualName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.MaintainIDVDetailsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.MaintainIDVDetailsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.MoneyLaunderingAssessment;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.RequestAction;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.RetrieveIDVDetailsResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;

import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawDataImpl;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author L081050
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ CustomerRawDataImpl.class })
public class MaintainIdvDetailIntegrationServiceImplV5Test {
	@InjectMocks
	private MaintainIdvDetailIntegrationServiceImplV5 maintainIdvDetailIntegrationServiceImplV5;

	@Mock
	private WebServiceProvider provider;

	@Mock
	private RetrieveIDVDetailsResponse response;

	@Mock
	private BankingAuthorityService userSamlService;
	@Mock
	private ServiceStatus serviceStatus;

	@Mock
	private CustomerRawData customerRawData;

	private void runCommonMockServices() throws Exception {
		SamlToken samlToken = new SamlToken(SamlUtil.loadSaml());
		when(userSamlService.getSamlToken()).thenReturn(samlToken);
		when(response.getServiceStatus()).thenReturn(serviceStatus);
	}

	@Test
	public void testMaintainIDVDetails() throws JsonProcessingException {
		MaintainIDVDetailsResponse res = new MaintainIDVDetailsResponse();
		CorrelatedResponse correlatedResponse = new CorrelatedResponse(
				new CorrelationIdWrapper(), response);

		ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
		res.getServiceStatus().add(serviceStatus);

		PowerMockito.mockStatic(CustomerRawDataImpl.class);
		Mockito.when(CustomerRawDataImpl.getJson(res)).thenReturn("{aaaaa}");

		correlatedResponse.setResponseObject(res);

		MaintainIdvRequest maintainIdvRequest = new MaintainIdvRequest();
		maintainIdvRequest
				.setRequestAction(RequestAction.UPDATE_AND_SET_IDV_STATUS);
		IndividualIDVAssessment identityVerificationAssessment = new IndividualIDVAssessment();
		identityVerificationAssessment.setAssessmentMethod("XYZ");
		Agent agent = new Agent();
		agent.setAgentType("Other");
		EmployeeIdentifier employeeIdentifier = new EmployeeIdentifier();
		employeeIdentifier
				.setIdentificationScheme(IdentificationScheme.CIS_KEY);
		employeeIdentifier.setEmployeeNumber("24536466466477");
		agent.setExternalIdentifier(employeeIdentifier);
		Individual individual = new Individual();
		IndividualName individualName = new IndividualName();
		individual.getHasForName().add(individualName);
		agent.setRoleIsPlayedBy(individual);
		identityVerificationAssessment.setPerformedExternallyBy(agent);
		identityVerificationAssessment.setNoABNReason("Not Applicable");
		Individual indiv = new Individual();
		MoneyLaunderingAssessment moneyLaunderingAssessment = new MoneyLaunderingAssessment();
		indiv.getHasAntiMoneyLaunderingAssessment().add(
				moneyLaunderingAssessment);
		identityVerificationAssessment.setHasForSubject(indiv);
		maintainIdvRequest
				.setIdentityVerificationAssessment(identityVerificationAssessment);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		MaintainIDVDetailsRequest maintainIDVDetailsRequest = MaintainIdvDetailRequestBuilderV5
				.createIDVDetialsRequest(maintainIdvRequest);
		SamlToken samlToken = new SamlToken(SamlUtil.loadSaml());
		when(userSamlService.getSamlToken()).thenReturn(samlToken);
		when(
				provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
						any(SamlToken.class), anyString(), anyObject(),
						any(ServiceErrors.class))).thenReturn(
				correlatedResponse);
		when(serviceStatus.getStatusInfo())
				.thenReturn(getStatus(Level.SUCCESS));

		CustomerRawData customerRawData = maintainIdvDetailIntegrationServiceImplV5
				.maintain(maintainIdvRequest, serviceErrors);
		assertThat(customerRawData.getRawResponse(), is("{aaaaa}"));
	}

	@Test
	public void testretrieveIDVDetailsWithError()
			throws JsonProcessingException {
		MaintainIDVDetailsResponse res = new MaintainIDVDetailsResponse();
		CorrelatedResponse correlatedResponse = new CorrelatedResponse(
				new CorrelationIdWrapper(), response);

		ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
		res.getServiceStatus().add(serviceStatus);
		PowerMockito.mockStatic(CustomerRawDataImpl.class);
		Mockito.when(CustomerRawDataImpl.getJson(res)).thenReturn("{aaaaa}");

		correlatedResponse.setResponseObject(res);

		MaintainIdvRequest maintainIdvRequest = new MaintainIdvRequest();
		maintainIdvRequest
				.setRequestAction(RequestAction.UPDATE_AND_SET_IDV_STATUS);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		SamlToken samlToken = new SamlToken(SamlUtil.loadSaml());
		when(userSamlService.getSamlToken()).thenReturn(samlToken);
		when(
				provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
						any(SamlToken.class), anyString(), anyObject(),
						any(ServiceErrors.class))).thenReturn(
				correlatedResponse);
		when(serviceStatus.getStatusInfo()).thenReturn(getStatus(Level.ERROR));

		CustomerRawData customerRawData = maintainIdvDetailIntegrationServiceImplV5
				.maintain(maintainIdvRequest, serviceErrors);
		assertThat(customerRawData.getRawResponse(), is("{aaaaa}"));
	}

	@Test
	public void testretrieveIDVDetailsThrowException()
			throws JsonProcessingException {
		MaintainIDVDetailsResponse res = new MaintainIDVDetailsResponse();
		CorrelatedResponse correlatedResponse = new CorrelatedResponse(
				new CorrelationIdWrapper(), response);

		ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
		res.getServiceStatus().add(serviceStatus);
		PowerMockito.mockStatic(CustomerRawDataImpl.class);
		Mockito.when(CustomerRawDataImpl.getJson(res)).thenReturn("{aaaaa}");

		correlatedResponse.setResponseObject(res);

		MaintainIdvRequest maintainIdvRequest = new MaintainIdvRequest();
		maintainIdvRequest
				.setRequestAction(RequestAction.UPDATE_AND_SET_IDV_STATUS);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		SamlToken samlToken = new SamlToken(SamlUtil.loadSaml());
		when(userSamlService.getSamlToken()).thenReturn(samlToken);
		when(
				provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
						any(SamlToken.class), anyString(), anyObject(),
						any(ServiceErrors.class))).thenReturn(
				correlatedResponse);
		when(serviceStatus.getStatusInfo())
				.thenReturn(getStatus(Level.SUCCESS));
		when(
				maintainIdvDetailIntegrationServiceImplV5.maintain(
						maintainIdvRequest, serviceErrors)).thenThrow(
				JsonProcessingException.class);
		maintainIdvDetailIntegrationServiceImplV5.maintain(maintainIdvRequest,
				serviceErrors);
	}

	private List<StatusInfo> getStatus(Level level) {
		List<StatusInfo> statusInfo = new ArrayList<StatusInfo>();
		StatusInfo status = new StatusInfo();
		status.setLevel(level);
		statusInfo.add(status);
		return statusInfo;
	}
}
