package com.bt.nextgen.service.group.customer.groupesb.address.v10;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsRequest;
import com.bt.nextgen.config.WebServiceProviderConfig;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.service.ErrorHandlerUtil;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.address.CacheManagedCustomerDataManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.annotation.Resource;

/**
 * Created by F057654 on 30/07/2015.
 */
@Service("cacheManagedCustomerDataManagementv10Service")
public class CacheManagedCustomerDataManagementServiceV10Impl implements CacheManagedCustomerDataManagementService {

    private static final Logger logger = LoggerFactory.getLogger(CacheManagedCustomerDataManagementServiceV10Impl.class);

    @Autowired
    private WebServiceProvider provider;

    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;

    public CacheManagedCustomerDataManagementServiceV10Impl() {}

    public CacheManagedCustomerDataManagementServiceV10Impl(WebServiceProvider provider, BankingAuthorityService userSamlService) {
        this.provider = provider;
        this.userSamlService = userSamlService;
    }

    @Cacheable(key = "(#request.getInvolvedPartyRoleType().getDescription())+(#request.getCISKey().getId())+(#request.getOperationTypes().get(0).name())", value = "com.bt.nextgen.service.group.customer.groupesb", unless="#result == null")
    public CorrelatedResponse retrieveCustomerInformation(CustomerManagementRequest request, final ServiceErrors serviceErrors)
    {
        logger.info("Retrieving data CacheManagedCustomerDataManagementService.retrieveCustomerInformation for user{}", request.getCISKey());
        CorrelatedResponse correlatedResponse = null;
        RetrieveDetailsAndArrangementRelationshipsForIPsRequest requestPayload = GroupEsbUserDetailsRequestV10Builder.createRetrieveDetailsAndArrangementRelationships(request);
        try
        {
            correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
                    userSamlService.getSamlToken(), WebServiceProviderConfig.GROUP_ESB_RETRIEVE_CUSTOMER_DETAILS_V10.getConfigName(), requestPayload, serviceErrors);
        }
        catch(SoapFaultClientException sfe)
        {
            ErrorHandlerUtil.parseSoapFaultException(serviceErrors, sfe, ServiceConstants.SERVICE_258);
            return null;
        }

        logger.info("Completed retrieving data CacheManagedCustomerDataManagementService.retrieveCustomerInformation for user{}", request.getCISKey());
        return correlatedResponse;
    }

    @CacheEvict(key ="(#request.getInvolvedPartyRoleType().getDescription())+(#request.getCISKey().getId())+(#request.getOperationTypes().get(0).name())", value = "com.bt.nextgen.service.group.customer.groupesb")
    public void clearCustomerInformation(CustomerManagementRequest request)
    {
        logger.info("CustomerManagement cache has been cleared for user with CIS Key {}", request.getCISKey().getId());
    }
}
