/**
 * 
 */
package com.bt.nextgen.service.gesb.createindividualip.v5;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.CreateIndividualIPRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.CreateIndividualIPResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
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
import com.bt.nextgen.serviceops.repository.GcmAuditRepository;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author L081050
 *
 */
@Service("createindividualipintegrationservicev5")
@SuppressWarnings("squid:S1200")
public class CreateIndividualIPIntegrationServiceImplV5 implements CreateIndividualIPIntegrationService{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateIndividualIPIntegrationServiceImplV5.class);
    
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
    public CustomerRawData create(CreateIndvIPRequest req, ServiceErrors serviceErrors) {
        CreateIndividualIPRequest requestPayLoad = CreateIndividualIPRequestV5Builder.createCreateIndividualIPRequest(req);
            gcmAuditRepository.logAuditEntry(userProfileService.getUserId(), WebServiceProviderConfig.GROUP_ESB_CREATE_INDIVIDUAL_IP_V5.getConfigName(), CustomerRawDataImpl.getJson(requestPayLoad));
            CreateIndividualIPResponse response = retriveDataFromWebService(requestPayLoad, serviceErrors);
            CustomerRawData customerRawData = null;
            try {
                customerRawData = (CustomerRawData) new CustomerRawDataImpl(response);
            } catch (JsonProcessingException ex) {
                LOGGER.error("create Error converting object to json", ex);
            }
            return customerRawData;
    }

    private CreateIndividualIPResponse retriveDataFromWebService(
            CreateIndividualIPRequest requestPayload, ServiceErrors errors) {
        CorrelatedResponse correlatedResponse = null;
        CreateIndividualIPResponse response =
                new CreateIndividualIPResponse();
        try {
            LOGGER.info("Calling web service to create individual ip.");
            CreateIndividualIPRequest request = requestPayload;
            correlatedResponse =
                    provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
                            userSamlService.getSamlToken(),
                            WebServiceProviderConfig.GROUP_ESB_CREATE_INDIVIDUAL_IP_V5.getConfigName(),
                            request, errors);
        } catch (SoapFaultClientException sfe) {
            LOGGER.error(" Getting Error response when calling create individual ip", sfe);
            ServiceStatus serviceStatus = new ServiceStatus();
            StatusInfo statusInfo = new StatusInfo();
            statusInfo.setCode(sfe.getFaultCode().toString());
            statusInfo.setDescription(sfe.getFaultStringOrReason());
            statusInfo.setLevel(Level.ERROR);
            serviceStatus.getStatusInfo().add(statusInfo);
            response.setServiceStatus(serviceStatus);
        }
        if(null != correlatedResponse && null != correlatedResponse.getResponseObject()){
            response = (CreateIndividualIPResponse) correlatedResponse.getResponseObject();
        }
        String status = response.getServiceStatus().getStatusInfo().get(0).getLevel().toString();

        if (!status.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
            LOGGER.error("CreateIndividualIPIntegrationServiceImplV5.retriveDataFromWebService returning failure");
        }
        LOGGER.info("Successful response returned when calling retriveDataFromWebService for create individual Ip request.");
        return response;

    }
}
