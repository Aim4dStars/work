package com.bt.nextgen.service.group.customer.groupesb.address.v10;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.MaintainIPContactMethodsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.MaintainIPContactMethodsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.WebServiceProviderConfig;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.service.ErrorHandlerUtil;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerDataImpl;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.address.CacheManagedCustomerDataManagementService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import java.util.List;

/**
 * Created by F057654 on 23/07/2015.
 */
@Service("addressManagementV10Service")
public class GroupEsbCustomerAddressManagementV10Impl implements CustomerDataManagementIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(GroupEsbCustomerAddressManagementV10Impl.class);

    @Autowired
    private WebServiceProvider provider;

    @Autowired
    @Qualifier("userDetailsService")
    private BankingAuthorityService userSamlService;

    @Autowired
    @Qualifier("cacheManagedCustomerDataManagementv10Service")
    private CacheManagedCustomerDataManagementService cacheCustomerAddressManagementService;

    @Autowired
    private CmsService cmsService;

    //default constructor for spring ioc
    public GroupEsbCustomerAddressManagementV10Impl() {}

    public GroupEsbCustomerAddressManagementV10Impl(WebServiceProvider provider, BankingAuthorityService userSamlService,
                                                    CacheManagedCustomerDataManagementService cacheCustomerAddressManagementService,
                                                    CmsService cmsService) {
        this.provider = provider;
        this.userSamlService = userSamlService;
        this.cacheCustomerAddressManagementService = cacheCustomerAddressManagementService;
        this.cmsService = cmsService;
    }

    @Override
    @SuppressWarnings({"squid:S1199","findbugs:DLS_DEAD_LOCAL_STORE"})
    public CustomerData retrieveCustomerInformation(CustomerManagementRequest request, List<String> operationTypes, ServiceErrors serviceErrors) {
        {
            //Clear the cache before proceeding for retrieve
            if (request != null && request.getInvolvedPartyRoleType() != null && !request.getOperationTypes().isEmpty() && request.getCISKey() != null) {
                logger.info("Inside retrieveCustomerInformation() : Proceeding to clear the cache of Address for user{}", request.getCISKey());
                cacheCustomerAddressManagementService.clearCustomerInformation(request);

                CorrelatedResponse correlatedResponse = cacheCustomerAddressManagementService.retrieveCustomerInformation(request, serviceErrors);
                RetrieveDetailsAndArrangementRelationshipsForIPsResponse response = (RetrieveDetailsAndArrangementRelationshipsForIPsResponse) correlatedResponse.getResponseObject();
                String status = response.getServiceStatus().getStatusInfo().get(0).getLevel().toString();

                if (!status.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                    logger.error("GroupEsbCustomerAddressManagementImpl.retrieveCustomerInformation returning failure");
                    ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_258, correlatedResponse.getCorrelationIdWrapper(), cmsService);
                    return null;
                }
                return CustomerManagementDataV10Converter.convertResponseInAddressModel(response, request);
            }
        }

        logger.error("Retrieve failed for address details");
        return new CustomerDataImpl();
    }

    @Override
    public boolean updateCustomerInformation(CustomerData updatedData, ServiceErrors serviceErrors) {
        logger.info("GroupEsbCustomerAddressManagementImpl.updateCustomerInformation for user{}", updatedData.getRequest().getCISKey());

        if(updatedData.getAddress() == null){
            logger.warn("Nothing to update for Address : Returning success from the service level");
            return true;
        }

        CorrelatedResponse correlatedResponse = null;

        //Fetch the request sent for retrieve - the same will be sent again and the response will be fetched and transformed in the current data
        //In case the cache is empty, a new call will be made to retrieve the details

        logger.info("Proceeding to retrieve cached address for user{}", updatedData.getRequest().getCISKey());
        CorrelatedResponse cachedCorrelatedResponse = cacheCustomerAddressManagementService.retrieveCustomerInformation(updatedData.getRequest(), serviceErrors);
        RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse = (RetrieveDetailsAndArrangementRelationshipsForIPsResponse)cachedCorrelatedResponse.getResponseObject();

        MaintainIPContactMethodsRequest requestPayload = GroupEsbAddressUpdateRequestV10Builder.createUpdateIPContactMethods(updatedData, cachedResponse);

        //Clear the cache before proceeding for update
        logger.info("Proceeding to clear the cache of Address for user{}", updatedData.getRequest().getCISKey());
        cacheCustomerAddressManagementService.clearCustomerInformation(updatedData.getRequest());

        try
        {
            logger.info("Sending request of Address update for user{}", updatedData.getRequest().getCISKey());
            correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
                    userSamlService.getSamlToken(), WebServiceProviderConfig.GROUP_ESB_UPDATE_CUSTOMER_ADDRESS_DETAILS.getConfigName(), requestPayload, serviceErrors);
        }
        catch(SoapFaultClientException sfe)
        {
            logger.error("Error received when updating Address for user{}", updatedData.getRequest().getCISKey());
            ErrorHandlerUtil.parseSoapFaultException(serviceErrors, sfe, ServiceConstants.SERVICE_418);
            return false;
        }

        MaintainIPContactMethodsResponse response = (MaintainIPContactMethodsResponse)correlatedResponse.getResponseObject();

        for(StatusInfo statusInfo : response.getServiceStatus().getStatusInfo()){
        	if (!statusInfo.getLevel().toString().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
        		logger.error("GroupEsbCustomerContactDetailsManagementImpl.updateCustomerInformation returning failure while parsing statusInfo");
                ErrorHandlerUtil.parseGCMErrors(statusInfo, serviceErrors, ServiceConstants.SERVICE_418, correlatedResponse.getCorrelationIdWrapper());
                return false;
        	}
        }
      
        logger.info("Returning success of address update for user{}", updatedData.getRequest().getCISKey());
        return true;
    }

}
