/**
 * 
 */
package com.bt.nextgen.service.gesb.maintainidvdetail.v5;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.MaintainIDVDetailsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.MaintainIDVDetailsResponse;
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
@Service("maintainidvdetailintegrationservicev5")
@SuppressWarnings("squid:S1200")
public class MaintainIdvDetailIntegrationServiceImplV5 implements
		MaintainIdvDetailIntegrationService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MaintainIdvDetailIntegrationServiceImplV5.class);

	@Autowired
	private WebServiceProvider provider;

	@Resource(name = "userDetailsService")
	private BankingAuthorityService userSamlService;

	@Override
	public CustomerRawData maintain(MaintainIdvRequest req,
			ServiceErrors serviceErrors) {
		// TODO Auto-generated method stub
		MaintainIDVDetailsRequest maintainIDVDetailsRequest = MaintainIdvDetailRequestBuilderV5
				.createIDVDetialsRequest(req);
		MaintainIDVDetailsResponse response = retrieveCustomerDetailsFromWebservice(
				maintainIDVDetailsRequest, serviceErrors);
		CustomerRawData customerRawData = null;
		try {
			customerRawData = new CustomerRawDataImpl(response);
		} catch (JsonProcessingException ex) {
			LOGGER.error("Error converting object to json", ex);
		}
		return customerRawData;
	}

	private MaintainIDVDetailsResponse retrieveCustomerDetailsFromWebservice(
			MaintainIDVDetailsRequest requestPayload,
			ServiceErrors serviceErrors) {
		CorrelatedResponse correlatedResponse = null;
		MaintainIDVDetailsResponse response = new MaintainIDVDetailsResponse();
		try {
			LOGGER.info("Calling web service to retrieve IP to IP Relationship.");
			correlatedResponse = provider
					.sendWebServiceWithSecurityHeaderAndResponseCallback(
							userSamlService.getSamlToken(),
							WebServiceProviderConfig.GROUP_ESB_MAINTAIN_IDV_DETAIL_V5
									.getConfigName(), requestPayload,
							serviceErrors);
			response = (MaintainIDVDetailsResponse) correlatedResponse
					.getResponseObject();
		} catch (SoapFaultClientException sfe) {
			LOGGER.error("Getting Error Response for service 325", sfe);
			ServiceStatus serviceStatus = new ServiceStatus();
			StatusInfo statusInfo = new StatusInfo();
			statusInfo.setCode(sfe.getFaultCode().toString());
			statusInfo.setDescription(sfe.getFaultStringOrReason());
			serviceStatus.getStatusInfo().add(statusInfo);
			response.getServiceStatus().add(serviceStatus);
		}

		LOGGER.info("Response returned when retrieving IP to IP Relationship.");
		return response;
	}
}
