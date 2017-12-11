package com.bt.nextgen.service.group.customer.groupesb.v11;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyEntityFilter;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.WebServiceProviderConfig;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawDataImpl;
import com.bt.nextgen.serviceops.repository.GcmAuditRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Created by L069679 on 16/01/2017.
 */
@Service("customerManagementV11Service")
public class GroupEsbCustomerManagementV11Impl implements CustomerManagementIntegrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupEsbCustomerManagementV11Impl.class);

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
    public CustomerRawData retrieveCustomerRawInformation(CustomerManagementRequest request, List<String> operationTypes, ServiceErrors serviceErrors) {
        RetrieveDetailsAndArrangementRelationshipsForIPsRequest requestPayload = GroupEsbUserDetailsRequestV11Builder.createRetrieveDetailsAndArrangementRelationships(request);
        
        // Adding user selected filters to requsetPayload
        InvolvedPartyEntityFilter involvedPartyEntityFilter = requestPayload.getInvolvedPartyEntityFilter();
        GroupEsbUserDetailsRequestV11Builder.addUserSelectedFilters(involvedPartyEntityFilter, operationTypes);
        requestPayload.setInvolvedPartyEntityFilter(involvedPartyEntityFilter);
        gcmAuditRepository.logAuditEntry(userProfileService.getUserId(), WebServiceProviderConfig.GROUP_ESB_RETRIEVE_CUSTOMER_DETAILS_V11.getConfigName(), CustomerRawDataImpl.getJson(requestPayload));
        RetrieveDetailsAndArrangementRelationshipsForIPsResponse response = retrieveCustomerDetailsFromWebservice(requestPayload, serviceErrors);
        CustomerRawData customerRawData = null;
        try {
            customerRawData = (CustomerRawData) new CustomerRawDataImpl(response);
        } catch (JsonProcessingException ex) {
            LOGGER.error("Error converting object to json", ex);
        }
        return customerRawData;
    }

    private RetrieveDetailsAndArrangementRelationshipsForIPsResponse retrieveCustomerDetailsFromWebservice(
            RetrieveDetailsAndArrangementRelationshipsForIPsRequest requestPayload, ServiceErrors serviceErrors) {
        CorrelatedResponse correlatedResponse = null;
        RetrieveDetailsAndArrangementRelationshipsForIPsResponse response = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
        try {
            LOGGER.info("Calling web service to retrieve customer details and arrangements.");
            correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
                    WebServiceProviderConfig.GROUP_ESB_RETRIEVE_CUSTOMER_DETAILS_V11.getConfigName(), requestPayload, serviceErrors);
            response = (RetrieveDetailsAndArrangementRelationshipsForIPsResponse) correlatedResponse.getResponseObject();
        } catch (SoapFaultClientException sfe) {
            LOGGER.error("Error getting response", sfe);
            //ErrorHandlerUtil.parseSoapFaultException(serviceErrors, sfe, ServiceConstants.SERVICE_258);
            ServiceStatus serviceStatus = new ServiceStatus();
            StatusInfo statusInfo = new StatusInfo();
            statusInfo.setCode(sfe.getFaultCode().toString());
            statusInfo.setDescription(sfe.getFaultStringOrReason());
            serviceStatus.getStatusInfo().add(statusInfo);
            response.setServiceStatus(serviceStatus);
        }

        LOGGER.info("Response returned when retrieving customer details and arrangements.");
        return response;
    }
}
