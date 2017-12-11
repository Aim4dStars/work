package com.bt.nextgen.service.group.customer.groupesb.retriveidvdetails.v6;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.RetrieveIDVDetailsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.RetrieveIDVDetailsResponse;
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

@Service("retriveidvdetialsintegrationservicev6")
public class RetriveIDVDetailsIntegrationServiceImplv6 implements RetriveIDVDetailsIntegrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetriveIDVDetailsIntegrationServiceImplv6.class);

    @Autowired
    private WebServiceProvider provider;

    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;

    @Override
    public CustomerRawData retrieveIDVDetails(RetriveIDVDtlRequest request, ServiceErrors serviceErrors) {
        RetrieveIDVDetailsRequest requestPayload = RetriveIDVDetailsRequestBuilderV6.createIDVDetialsRequest(request);

        RetrieveIDVDetailsResponse response = retrieveCustomerDetailsFromWebservice(requestPayload, serviceErrors);
        CustomerRawData customerRawData = null;
        try {
            customerRawData = new CustomerRawDataImpl(response);
        } catch (JsonProcessingException ex) {
            LOGGER.error("Error converting object to json", ex);
        }
        return customerRawData;
    }

    private RetrieveIDVDetailsResponse retrieveCustomerDetailsFromWebservice(RetrieveIDVDetailsRequest requestPayload,
            ServiceErrors serviceErrors) {
        CorrelatedResponse correlatedResponse = null;
        RetrieveIDVDetailsResponse response = new RetrieveIDVDetailsResponse();
        try {
            LOGGER.info("Calling web service to retrieve IP to IP Relationship.");
            correlatedResponse =
                    provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
                            WebServiceProviderConfig.GROUP_ESB_RETRIVE_IDV_DETAILS_V6.getConfigName(), requestPayload,
                            serviceErrors);
            response = (RetrieveIDVDetailsResponse) correlatedResponse.getResponseObject();
        } catch (SoapFaultClientException sfe) {
            LOGGER.error("Getting Error Response for service 324", sfe);
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
