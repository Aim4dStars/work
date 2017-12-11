/**
 * 
 */
package com.bt.nextgen.service.gesb.maintainipcontactmethod.v1;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.MaintainIPContactMethodsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.MaintainIPContactMethodsResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;

import com.bt.nextgen.config.WebServiceProviderConfig;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawDataImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author L081050
 */
@Service("maintainipcontactintegrationservicev1")
@SuppressWarnings("squid:S1200")
public class MaintainIpContactIntegrationServiceImplV1 implements
		MaintainIpContactIntegrationService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MaintainIpContactIntegrationServiceImplV1.class);

	@Autowired
	private WebServiceProvider provider;

	@Resource(name = "userDetailsService")
	private BankingAuthorityService userSamlService;

	@Override
	public CustomerRawData maintain(MaintainIpContactRequest req,
			ServiceErrors serviceErrors) {
		// TODO Auto-generated method stub
		MaintainIPContactMethodsRequest maintainIPContactMethodsRequest = MaintainIpContactRequestBuilderV1
				.createIpContactRequest(req);
		MaintainIPContactMethodsResponse response = retrieveCustomerDetailsFromWebservice(
				maintainIPContactMethodsRequest, serviceErrors);
		CustomerRawData customerRawData = null;
		try {
			customerRawData = new CustomerRawDataImpl(response);
		} catch (JsonProcessingException ex) {
			LOGGER.error("Error converting object to json", ex);
		}
		return customerRawData;
	}

	private MaintainIPContactMethodsResponse retrieveCustomerDetailsFromWebservice(
			MaintainIPContactMethodsRequest requestPayload,
			ServiceErrors serviceErrors) {
		CorrelatedResponse correlatedResponse = null;
		MaintainIPContactMethodsResponse response = new MaintainIPContactMethodsResponse();
		try {
			LOGGER.info("Calling web service to maintain ip contacts.");
			correlatedResponse = provider
					.sendWebServiceWithSecurityHeaderAndResponseCallback(
							userSamlService.getSamlToken(),
							WebServiceProviderConfig.GROUP_MAINTAIN_IP_CONTACTS_METHOD_V1
									.getConfigName(), requestPayload,
							serviceErrors);
			response = (MaintainIPContactMethodsResponse) correlatedResponse
					.getResponseObject();
		} catch (SoapFaultClientException sfe) {
			LOGGER.error("Getting Error Response for service 418", sfe);
			ServiceStatus serviceStatus = new ServiceStatus();
			StatusInfo statusInfo = new StatusInfo();
			statusInfo.setCode(sfe.getFaultCode().toString());
			statusInfo.setDescription(sfe.getFaultStringOrReason());
			serviceStatus.getStatusInfo().add(statusInfo);
			response.setServiceStatus(serviceStatus);
/*			response.getServiceStatus().add(serviceStatus);
*/		}

		LOGGER.info("Response returned when maintaining ip contacts.");
		return response;
	}
}
