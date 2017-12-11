package com.bt.nextgen.service.group.maintainiptoiprelationships.groupesb;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainiptoiprelationships.v1.svc0257.MaintainIPToIPRelationshipsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainiptoiprelationships.v1.svc0257.MaintainIPToIPRelationshipsResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;

import com.bt.nextgen.config.WebServiceProviderConfig;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawDataImpl;
import com.bt.nextgen.serviceops.repository.GcmAuditRepository;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service("maintainIpToIpRelationshipIntegrationServiceV1")
public class GroupEsbIpToIpRelationManagementV1Impl implements
		MaintainIpToIpRelationshipIntegrationService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GroupEsbIpToIpRelationManagementV1Impl.class);

	@Autowired
	private WebServiceProvider provider;

	@Resource(name = "userDetailsService")
	private BankingAuthorityService userSamlService;

	@Autowired
	private GcmAuditRepository gcmAuditRepository;

	@Autowired
	private UserProfileService userProfileService;

	@Override
	public CustomerRawData maintainIpToIpRelationship(
			IpToIpRelationshipRequest req, ServiceErrors serviceError) {
		LOGGER.info("Inside GroupEsbIpToIpRelationManagementV1Impl.maintainIpToIpRelationship()");
		MaintainIPToIPRelationshipsRequest requestPayLoad = GroupEsbIpToIpManagementRequestV1Builder
				.maintainIpToIpRelation(req);
		gcmAuditRepository
				.logAuditEntry(
						userProfileService.getUserId(),
						WebServiceProviderConfig.GROUP_ESB_MAINTAIN_IP_TO_IP_RELATIONSHIP_V1
								.getConfigName(), CustomerRawDataImpl
								.getJson(requestPayLoad));
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		MaintainIPToIPRelationshipsResponse response = retriveDataFromWebService(
				requestPayLoad, serviceErrors);
		CustomerRawData customerRawData = null;
		try {
			customerRawData = (CustomerRawData) new CustomerRawDataImpl(
					response);
		} catch (JsonProcessingException ex) {
			LOGGER.error("Error converting object to json", ex);
		}
		return customerRawData;
	}

	private MaintainIPToIPRelationshipsResponse retriveDataFromWebService(
			MaintainIPToIPRelationshipsRequest requestPayload,
			ServiceErrors serviceErrors) {

		CorrelatedResponse correlatedResponse = null;
		MaintainIPToIPRelationshipsResponse response = new MaintainIPToIPRelationshipsResponse();
		try {
			LOGGER.info("Calling web service to maintain IP to IP relationship.");
			correlatedResponse = provider
					.sendWebServiceWithSecurityHeaderAndResponseCallback(
							userSamlService.getSamlToken(),
							WebServiceProviderConfig.GROUP_ESB_MAINTAIN_IP_TO_IP_RELATIONSHIP_V1
									.getConfigName(), requestPayload, serviceErrors);
		} catch (SoapFaultClientException sfe) {
			LOGGER.error(
					" Getting Error response when calling maintain IP to IP Relationship",
					sfe);
			ServiceStatus serviceStatus = new ServiceStatus();
			StatusInfo statusInfo = new StatusInfo();
			statusInfo.setCode(sfe.getFaultCode().toString());
			statusInfo.setDescription(sfe.getFaultStringOrReason());
			serviceStatus.getStatusInfo().add(statusInfo);
			response.setServiceStatus(serviceStatus);
		}
		if (null != correlatedResponse.getResponseObject()) {
			response = (MaintainIPToIPRelationshipsResponse) correlatedResponse
					.getResponseObject();
		}
		String status = response.getServiceStatus().getStatusInfo().get(0)
				.getLevel().toString();

		if (!status.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
			LOGGER.error("GroupEsbIpToIpRelationManagementV1Impl.maintainIpToIpRelationship returning failure");
		}
		LOGGER.info("Successful response returned when calling maintain Ip to Ip relationship.");
		return response;

	}

}
