package com.bt.nextgen.service.groupesb.createorganisationip.v5;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.CreateOrganisationIPRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.CreateOrganisationResponse;
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

@Service("createorganisationipintegrationservicev5")
public class CreateOrganisationIPIntegrationServiceImplv5 implements CreateOrganisationIPIntegrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateOrganisationIPIntegrationServiceImplv5.class);

    @Autowired
    private WebServiceProvider provider;

    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;
    @Override
    public CustomerRawData createorganisationIP(
            CreateOrganisationIPReq request,
            ServiceErrors serviceErrors) {
        // TODO Auto-generated method stub
         CreateOrganisationIPRequest requestPayload =
                    CreateOrganisationIPRequestBuilderV5.createOrganisationIpRequest(request);

            CreateOrganisationResponse response =
                    retrieveCustomerDetailsFromWebservice(requestPayload, serviceErrors);
            CustomerRawData customerRawData = null;
            try {
                customerRawData = (CustomerRawData) new CustomerRawDataImpl(response);
            } catch (JsonProcessingException ex) {
                LOGGER.error("Error converting object to json", ex);
            }
            return customerRawData;
    }
    
    private CreateOrganisationResponse retrieveCustomerDetailsFromWebservice(
            CreateOrganisationIPRequest requestPayload, ServiceErrors serviceErrors) {
        CorrelatedResponse correlatedResponse = null;
        CreateOrganisationResponse response = new CreateOrganisationResponse();
        try {
            LOGGER.info("Calling web service to retrieve IP to IP Relationship.");
            correlatedResponse =
                    provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
                            WebServiceProviderConfig.GROUP_ESB_CREATE_ORAGANISATION_V5.getConfigName(),
                            requestPayload, serviceErrors);
            response = (CreateOrganisationResponse) correlatedResponse.getResponseObject();
        } catch (SoapFaultClientException sfe) {
            LOGGER.error("Getting Error Response for service 260", sfe);
            ServiceStatus serviceStatus = new ServiceStatus();
            StatusInfo statusInfo = new StatusInfo();
            statusInfo.setCode(sfe.getFaultCode().toString());
            statusInfo.setDescription(sfe.getFaultStringOrReason());
            serviceStatus.getStatusInfo().add(statusInfo);
            response.setServiceStatus(serviceStatus);
        }

        LOGGER.info("Response returned when retrieving IP to IP Relationship.");
        return response;
    }

	
}
