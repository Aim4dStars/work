package com.bt.nextgen.service.group.customer.groupesb.v7;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualcustomer.v2.svc0338.ModifyIndividualCustomerRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualcustomer.v2.svc0338.ModifyIndividualCustomerResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
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

import javax.annotation.Resource;

import java.util.List;
@Deprecated
@Service("preferredNameManagementV7Service")
public class GroupEsbCustomerPreferredNameManagementV7Impl implements CustomerDataManagementIntegrationService
{

	@Autowired
	private WebServiceProvider provider;

    @Autowired
    private CmsService cmsService;

	@Resource(name = "userDetailsService")
	private BankingAuthorityService userSamlService;

    @Autowired
    @Qualifier("cacheManagedCustomerDataManagementv7Service")
    private CacheManagedCustomerDataManagementService cacheCustomerPreferrednameManagementService;

    private static final Logger logger = LoggerFactory.getLogger(GroupEsbCustomerPreferredNameManagementV7Impl.class);
   @Override
   public CustomerData retrieveCustomerInformation(CustomerManagementRequest request, List<String> operationTypes, ServiceErrors serviceErrors) {

       //Clear the cache before proceeding for retrieve
       if(request != null && request.getInvolvedPartyRoleType()!= null && !request.getOperationTypes().isEmpty() && request.getCISKey()!=null) {
           logger.info("Inside retrieveCustomerInformation() : Proceeding to clear the cache of Preferred name for user{}", request.getCISKey());
           cacheCustomerPreferrednameManagementService.clearCustomerInformation(request);

           CorrelatedResponse correlatedResponse = cacheCustomerPreferrednameManagementService.retrieveCustomerInformation(request, serviceErrors);
           RetrieveDetailsAndArrangementRelationshipsForIPsResponse response = (RetrieveDetailsAndArrangementRelationshipsForIPsResponse) correlatedResponse.getResponseObject();
           String status = response.getServiceStatus().getStatusInfo().get(0).getLevel().toString();
           if (!status.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
               ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_258, correlatedResponse.getCorrelationIdWrapper(), cmsService);
               return null;
           }

           return CustomerManagementDataV7Converter.convertResponseToPreferredNameModel(response);
       }
       logger.error("Retrieve failed for preferred name details");
       return new CustomerDataImpl();
    }

    @Override
    public boolean updateCustomerInformation(CustomerData updatedData, ServiceErrors serviceErrors) {
        CorrelatedResponse correlatedResponse = null;

        CustomerManagementRequest request =updatedData.getRequest();
        CorrelatedResponse cachedCorrelatedResponse = cacheCustomerPreferrednameManagementService.retrieveCustomerInformation(request, serviceErrors);
        RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse = (RetrieveDetailsAndArrangementRelationshipsForIPsResponse)cachedCorrelatedResponse.getResponseObject();

        ModifyIndividualCustomerRequest requestPayload = GroupEsbPreferredNameUpdateRequestV7Builder.createModifyIndividualCustomerRequest(updatedData, cachedResponse);

        //Clear the cache before proceeding for update
        logger.info("Proceeding to clear the cache of Preferred Name for user{}", updatedData.getRequest().getCISKey());
        cacheCustomerPreferrednameManagementService.clearCustomerInformation(updatedData.getRequest());
        
        try
        {
           if(requestPayload!=null) {
               logger.info("Updating preferred name in UCM for user{}", request.getCISKey());
               correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
                       userSamlService.getSamlToken(), WebServiceProviderConfig.GROUP_ESB_UPDATE_CUSTOMER_DETAILS.getConfigName(), requestPayload, serviceErrors);
                logger.info("Updating preferred name completed in UCM for user{}", request.getCISKey());
           }
           else return true;
        }
        catch(SoapFaultClientException sfe)
        {
            ErrorHandlerUtil.parseSoapFaultException(serviceErrors, sfe, ServiceConstants.SERVICE_338);
            return false;
        }
        if(correlatedResponse!=null) {
        ModifyIndividualCustomerResponse response = (ModifyIndividualCustomerResponse)correlatedResponse.getResponseObject();

            String status = response.getServiceStatus().getStatusInfo().get(0).getLevel().toString();
            if (!status.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_338, correlatedResponse.getCorrelationIdWrapper(), cmsService);
            }
        }
        else
            return false;

        return true;

    }

}
