/**
 * 
 */
package com.bt.nextgen.service.gesb.maintainipcontactsmethod.v1;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ws.soap.client.SoapFaultClientException;

import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.RetrieveIDVDetailsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.CreateOrganisationResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.Action;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.MaintainIPContactMethodsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.MaintainIPContactMethodsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PhoneAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PriorityLevel;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.TelephoneAddress;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;

import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.gesb.maintainipcontactmethod.v1.MaintainIpContactIntegrationServiceImplV1;
import com.bt.nextgen.service.gesb.maintainipcontactmethod.v1.MaintainIpContactRequest;
import com.bt.nextgen.service.gesb.maintainipcontactmethod.v1.MaintainIpContactRequestBuilderV1;
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
public class MaintainIpContactIntegrationServiceImplV1Test {
	@InjectMocks
	private MaintainIpContactIntegrationServiceImplV1 maintainIpContactIntegrationServiceImplV1;

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
		MaintainIPContactMethodsResponse res = new MaintainIPContactMethodsResponse();
		CorrelatedResponse correlatedResponse = new CorrelatedResponse(
				new CorrelationIdWrapper(), response);

		ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
		res.setServiceStatus(serviceStatus);

		PowerMockito.mockStatic(CustomerRawDataImpl.class);
		Mockito.when(CustomerRawDataImpl.getJson(res)).thenReturn("{aaaaa}");

		correlatedResponse.setResponseObject(res);

		MaintainIpContactRequest maintainIpContactRequest = new MaintainIpContactRequest();
		/*maintainIpContactRequest
				.setRequestAction(RequestAction.UPDATE_AND_SET_IDV_STATUS);*/
		InvolvedPartyIdentifier involvedPartyIdentifier=new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
		maintainIpContactRequest.setInvolvedPartyIdentifier(involvedPartyIdentifier);
		maintainIpContactRequest.setHasPhoneAddressContactMethod(getPhoneAddressContact());
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		MaintainIPContactMethodsRequest maintainIPContactMethodsRequest = MaintainIpContactRequestBuilderV1
				.createIpContactRequest(maintainIpContactRequest);
		when(
				provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
						any(SamlToken.class), anyString(), anyObject(),
						any(ServiceErrors.class))).thenReturn(
				correlatedResponse);
		when(serviceStatus.getStatusInfo())
				.thenReturn(getStatus(Level.SUCCESS));

		CustomerRawData customerRawData = maintainIpContactIntegrationServiceImplV1
				.maintain(maintainIpContactRequest, serviceErrors);
		assertThat(customerRawData.getRawResponse(), is("{aaaaa}"));
	}

	@Test
	public void testretrieveIDVDetailsWithError()
			throws JsonProcessingException {
		MaintainIPContactMethodsResponse res = new MaintainIPContactMethodsResponse();
		CorrelatedResponse correlatedResponse = new CorrelatedResponse(
				new CorrelationIdWrapper(), response);

		ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
		res.setServiceStatus(serviceStatus);;
		PowerMockito.mockStatic(CustomerRawDataImpl.class);
		Mockito.when(CustomerRawDataImpl.getJson(res)).thenReturn("{aaaaa}");

		correlatedResponse.setResponseObject(res);

		MaintainIpContactRequest maintainIpContactRequest = new MaintainIpContactRequest();
		/*maintainIpContactRequest
				.setRequestAction(RequestAction.UPDATE_AND_SET_IDV_STATUS);*/
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		when(
				provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
						any(SamlToken.class), anyString(), anyObject(),
						any(ServiceErrors.class))).thenReturn(
				correlatedResponse);
		when(serviceStatus.getStatusInfo()).thenReturn(getStatus(Level.ERROR));

		CustomerRawData customerRawData = maintainIpContactIntegrationServiceImplV1
				.maintain(maintainIpContactRequest, serviceErrors);
		assertThat(customerRawData.getRawResponse(), is("{aaaaa}"));
	}
	 @Test
	    public void createorganisationIPTestSoapFaultClientException() throws SoapFaultClientException {
	        CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), response);
	        CreateOrganisationResponse res = new CreateOrganisationResponse();
	        ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
	        res.setServiceStatus(serviceStatus);
	        correlatedResponse.setResponseObject(res);
	        SoapFaultClientException soapFaultClientException = Mockito.mock(SoapFaultClientException.class);
	        when(soapFaultClientException.getFaultCode()).thenReturn(new QName("405"));
	        when(soapFaultClientException.getFaultStringOrReason()).thenReturn("Service Error");
	        when(serviceStatus.getStatusInfo()).thenReturn(getStatus(Level.ERROR));
	        ServiceErrors serviceErrors = new ServiceErrorsImpl();
	        MaintainIpContactRequest maintainIpContactRequest = new MaintainIpContactRequest();
	        when(
	                provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
	                        anyObject(), any(ServiceErrors.class))).thenThrow(soapFaultClientException);
	        maintainIpContactIntegrationServiceImplV1.maintain(maintainIpContactRequest,
	                serviceErrors);
	    }
	@Test
	public void testretrieveIDVDetailsThrowException()
			throws JsonProcessingException {
		MaintainIPContactMethodsResponse res = new MaintainIPContactMethodsResponse();
		CorrelatedResponse correlatedResponse = new CorrelatedResponse(
				new CorrelationIdWrapper(), response);

		ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
		//res.getServiceStatus().add(serviceStatus);
		PowerMockito.mockStatic(CustomerRawDataImpl.class);
		Mockito.when(CustomerRawDataImpl.getJson(res)).thenReturn("{aaaaa}");

		correlatedResponse.setResponseObject(res);

		MaintainIpContactRequest maintainIpContactRequest = new MaintainIpContactRequest();
		/*maintainIpContactRequest
				.setRequestAction(RequestAction.UPDATE_AND_SET_IDV_STATUS);*/
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		when(
				provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
						any(SamlToken.class), anyString(), anyObject(),
						any(ServiceErrors.class))).thenReturn(
				correlatedResponse);
		when(serviceStatus.getStatusInfo())
				.thenReturn(getStatus(Level.SUCCESS));
		when(
				maintainIpContactIntegrationServiceImplV1.maintain(
						maintainIpContactRequest, serviceErrors)).thenThrow(
				JsonProcessingException.class);
		maintainIpContactIntegrationServiceImplV1.maintain(maintainIpContactRequest,
				serviceErrors);
	}

	private List<StatusInfo> getStatus(Level level) {
		List<StatusInfo> statusInfo = new ArrayList<StatusInfo>();
		StatusInfo status = new StatusInfo();
		status.setLevel(level);
		statusInfo.add(status);
		return statusInfo;
	}
	
private PhoneAddressContactMethod getPhoneAddressContact()
	
	{
		PhoneAddressContactMethod phoneAddressContactMethod=new PhoneAddressContactMethod();
		phoneAddressContactMethod.setRequestedAction(Action.ADD);
		phoneAddressContactMethod.setPriorityLevel(PriorityLevel.PRIMARY);
		phoneAddressContactMethod.setUsageId("1");
		phoneAddressContactMethod.setValidityStatus("true");
		phoneAddressContactMethod.setIsActive("true");
		MaintenanceAuditContext maintenanceAuditContext=new MaintenanceAuditContext();
		maintenanceAuditContext.setIsActive(true);
		phoneAddressContactMethod.setAuditContext(maintenanceAuditContext);
		TelephoneAddress telephoneAddress=new TelephoneAddress();
		telephoneAddress.setAreaCode("1");
		telephoneAddress.setContactMedium("Mobile");
		telephoneAddress.setCountryCode("1");
		telephoneAddress.setLocalNumber("1");
		phoneAddressContactMethod.setHasAddress(telephoneAddress);
		return phoneAddressContactMethod;
	}
	
	
}
