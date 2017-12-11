package com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.MaintainArrangementAndIPArrangementRelationshipsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.MaintainArrangementAndIPArrangementRelationshipsResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.WebServiceProviderConfig;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawDataImpl;
import com.bt.nextgen.service.group.maintainarrangementandiparrangementrelationships.MaintainArrangementAndRelationshipIntegrationService;
import com.bt.nextgen.serviceops.repository.GcmAuditRepository;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service("maintainArrangementAndRelationshipIntegrationServiceV1")
public class GroupEsbRelationshipManagementV1Impl implements MaintainArrangementAndRelationshipIntegrationService {

	 private static final Logger LOGGER = LoggerFactory.getLogger(GroupEsbRelationshipManagementV1Impl.class);
	 
	 @Autowired
	    private WebServiceProvider provider;

	    @Resource(name = "userDetailsService")
	    private BankingAuthorityService userSamlService;

	    @Autowired
	    private CmsService cmsService;
	    
	    @Autowired
	    private GcmAuditRepository gcmAuditRepository;
	    
	    @Autowired
	    private UserProfileService userProfileService;
	
	@Override
	public CustomerRawData createArrangementAndRelationShip(
			ArrangementAndRelationshipManagementRequest req,
			ServiceErrors serviceErrors) {
		MaintainArrangementAndIPArrangementRelationshipsRequest requestPayLoad = GroupEsbRelationshipManagementRequestV1Builder.createMaintainAndArrangementRelationshipRequest(req);
        gcmAuditRepository.logAuditEntry(userProfileService.getUserId(), WebServiceProviderConfig.GROUP_ESB_MAINTAIN_ARRANGEMENT_AND_RELATIONSHIP_V1.getConfigName(), CustomerRawDataImpl.getJson(requestPayLoad));
		MaintainArrangementAndIPArrangementRelationshipsResponse response = retriveDataFromWebService(requestPayLoad, serviceErrors);
		CustomerRawData customerRawData = null;
        try {
            customerRawData = (CustomerRawData) new CustomerRawDataImpl(response);
        } catch (JsonProcessingException ex) {
            LOGGER.error("createArrangementAndRelationShip Error converting object to json", ex);
        }
        return customerRawData;
	
	}

    private MaintainArrangementAndIPArrangementRelationshipsResponse retriveDataFromWebService(
            MaintainArrangementAndIPArrangementRelationshipsRequest requestPayload, ServiceErrors errors) {

        CorrelatedResponse correlatedResponse = null;
        MaintainArrangementAndIPArrangementRelationshipsResponse response =
                new MaintainArrangementAndIPArrangementRelationshipsResponse();
        try {
            LOGGER.info("Calling web service to create maintain and arrangement.");
            MaintainArrangementAndIPArrangementRelationshipsRequest request = requestPayload;
            correlatedResponse =
                    provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
                            userSamlService.getSamlToken(),
                            WebServiceProviderConfig.GROUP_ESB_MAINTAIN_ARRANGEMENT_AND_RELATIONSHIP_V1.getConfigName(),
                            request, errors);
        } catch (SoapFaultClientException sfe) {
            LOGGER.error(" Getting Error response when calling create maintain and arrangement", sfe);
            ServiceStatus serviceStatus = new ServiceStatus();
            StatusInfo statusInfo = new StatusInfo();
            statusInfo.setCode(sfe.getFaultCode().toString());
            statusInfo.setDescription(sfe.getFaultStringOrReason());
            serviceStatus.getStatusInfo().add(statusInfo);
            response.setServiceStatus(serviceStatus);
        }
        if(null != correlatedResponse.getResponseObject()){
        	response = (MaintainArrangementAndIPArrangementRelationshipsResponse) correlatedResponse.getResponseObject();
        }
        String status = response.getServiceStatus().getStatusInfo().get(0).getLevel().toString();

        if (!status.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
            LOGGER.error("GroupEsbCustomerDataManagementImpl.createArrangementAndRelationShip returning failure");
        }
        LOGGER.info("Successful response returned when calling maintain arrangements and relationship.");
        return response;

    }
}
