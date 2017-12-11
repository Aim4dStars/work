package com.bt.nextgen.service.group.customer.groupesb.state.v10;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyorganisationcustomer.v2.svc0339.ModifyOrganisationCustomerRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyorganisationcustomer.v2.svc0339.ModifyOrganisationCustomerResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
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
import com.bt.nextgen.service.group.customer.groupesb.address.v10.CustomerManagementDataV10Converter;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.annotation.Resource;

import java.util.List;

@Service("regStateManagementV10Service")
public class GroupEsbRegStateManagementV10Impl implements CustomerDataManagementIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(GroupEsbRegStateManagementV10Impl.class);

    @Autowired
    private WebServiceProvider provider;

    @Autowired
    private CmsService cmsService;

    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;

    @Resource(name = "cacheManagedCustomerDataManagementv10Service")
    private CacheManagedCustomerDataManagementService cacheManagedCustomerDataManagementService;

    @Override
    public CustomerData retrieveCustomerInformation(CustomerManagementRequest request, List<String> operationTypes, ServiceErrors errors) {

        if (request != null && request.getInvolvedPartyRoleType() != null && !request.getOperationTypes().isEmpty() && request.getCISKey() != null) {
            logger.info("Inside retrieveCustomerInformation() : Proceeding to clear the cache of Registered State for user{}", request.getCISKey());
            cacheManagedCustomerDataManagementService.clearCustomerInformation(request);

            CorrelatedResponse correlatedResponse = cacheManagedCustomerDataManagementService.retrieveCustomerInformation(request, errors);
            RetrieveDetailsAndArrangementRelationshipsForIPsResponse response = (RetrieveDetailsAndArrangementRelationshipsForIPsResponse) correlatedResponse.getResponseObject();
            String status = response.getServiceStatus().getStatusInfo().get(0).getLevel().toString();
            if (!status.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                ErrorHandlerUtil.parseErrors(response.getServiceStatus(), errors, ServiceConstants.SERVICE_258, correlatedResponse.getCorrelationIdWrapper(), cmsService);
                return null;
            }
            return CustomerManagementDataV10Converter.convertResponseInRegistrationStateModel(response);
        }

        logger.error("Retrieve failed for Registered state details");
        return new CustomerDataImpl();
    }

    @Override
    public boolean updateCustomerInformation(CustomerData customerData, ServiceErrors serviceErrors) {

        if(customerData.getRegisteredState() == null){
            logger.warn("Nothing to update for Registered State: Returning success from the service level");
            return true;
        }

        CorrelatedResponse correlatedResponse = null;
        CorrelatedResponse cachedCorrelatedResponse = cacheManagedCustomerDataManagementService.retrieveCustomerInformation(customerData.getRequest(), serviceErrors);
        RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse = (RetrieveDetailsAndArrangementRelationshipsForIPsResponse)cachedCorrelatedResponse.getResponseObject();

        ModifyOrganisationCustomerRequest requestPayload = GroupEsbRegStateRequestV10Builder.createStateModificationRequest(customerData,cachedResponse);
        cacheManagedCustomerDataManagementService.clearCustomerInformation(customerData.getRequest());
        try
        {
            correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
                    userSamlService.getSamlToken(), WebServiceProviderConfig.GROUP_ESB_UPDATE_REGISTER_STATE.getConfigName(), requestPayload, serviceErrors);
        }
        catch(SoapFaultClientException sfe)
        {
            ErrorHandlerUtil.parseSoapFaultException(serviceErrors, sfe, ServiceConstants.SERVICE_418);
            return false;
        }
        ModifyOrganisationCustomerResponse maintainIPContactMethodsResponse = (ModifyOrganisationCustomerResponse) correlatedResponse.getResponseObject();
        String status  = maintainIPContactMethodsResponse.getServiceStatus().getStatusInfo().get(0).getLevel().toString();
        if(!status.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE))
        {
            ErrorHandlerUtil.parseErrors(maintainIPContactMethodsResponse.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_339, correlatedResponse.getCorrelationIdWrapper(), cmsService);
            return false;
        }
        return true;
    }
}
