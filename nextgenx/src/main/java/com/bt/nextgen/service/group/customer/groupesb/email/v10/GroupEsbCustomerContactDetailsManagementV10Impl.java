package com.bt.nextgen.service.group.customer.groupesb.email.v10;

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
import com.bt.nextgen.service.group.customer.groupesb.phone.v10.CustomerPhoneV10Converter;
import com.bt.nextgen.service.group.customer.groupesb.v10.CustomerManagementDataV10Converter;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.annotation.Resource;

import java.util.List;

@Service("contactDetailsManagementv10Service")
public class GroupEsbCustomerContactDetailsManagementV10Impl implements CustomerDataManagementIntegrationService {

    @Autowired
    private WebServiceProvider provider;

    @Autowired
    private CmsService cmsService;

    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;

    @Autowired
    @Qualifier("cacheManagedCustomerDataManagementv10Service")
    private CacheManagedCustomerDataManagementService cacheManagedCustomerDataManagementService;

    private static final Logger logger = LoggerFactory.getLogger(GroupEsbCustomerContactDetailsManagementV10Impl.class);

    @Override
    public CustomerData retrieveCustomerInformation(CustomerManagementRequest req, List<String> operationTypes, ServiceErrors serviceErrors) {

        //Clear the cache before proceeding for retrieve
        if(req != null && req.getInvolvedPartyRoleType()!= null && !req.getOperationTypes().isEmpty() && req.getCISKey()!=null) {
            logger.info("Inside retrieveCustomerInformation() : Proceeding to clear the cache of ContactDetails for user{}", req.getCISKey());
            cacheManagedCustomerDataManagementService.clearCustomerInformation(req);

            CorrelatedResponse correlatedResponse = cacheManagedCustomerDataManagementService.retrieveCustomerInformation(req, serviceErrors);

            RetrieveDetailsAndArrangementRelationshipsForIPsResponse response = (RetrieveDetailsAndArrangementRelationshipsForIPsResponse) correlatedResponse.getResponseObject();
            String status = response.getServiceStatus().getStatusInfo().get(0).getLevel().toString();
            if (!status.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_258, correlatedResponse.getCorrelationIdWrapper(), cmsService);
                return null;
            }

            CustomerData customerData = new CustomerDataImpl();
            customerData.setEmails(CustomerManagementDataV10Converter.convertResponseInEmailModel(response).getEmails());
            customerData.setPhoneNumbers(CustomerPhoneV10Converter.convertResponseInPhone(response, req).getPhoneNumbers());
            return customerData;
        }
        logger.error("Retrieve failed for contact details");
        return new CustomerDataImpl();
    }

    @Override
    public boolean updateCustomerInformation(CustomerData updatedData, ServiceErrors serviceErrors) {

        if(updatedData.getEmails().isEmpty() && updatedData.getPhoneNumbers().isEmpty()){
            logger.warn("Nothing to update for Phone/Email : Returning success from the service level");
            return true;
        }

        CorrelatedResponse correlatedResponse = null;
        CorrelatedResponse cachedCorrelatedResponse = cacheManagedCustomerDataManagementService.retrieveCustomerInformation(updatedData.getRequest(), serviceErrors);
        RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse = (RetrieveDetailsAndArrangementRelationshipsForIPsResponse)cachedCorrelatedResponse.getResponseObject();

        MaintainIPContactMethodsRequest requestPayload = GroupEsbCustomerContactDetailsRequestV10Builder.createContactDetailsModificationRequest(updatedData, cachedResponse);

        logger.info("Proceeding to clear the cache of ContactDetails for user{}", updatedData.getRequest().getCISKey());
        cacheManagedCustomerDataManagementService.clearCustomerInformation(updatedData.getRequest());
        try
        {
            correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
                    userSamlService.getSamlToken(), WebServiceProviderConfig.GROUP_ESB_UPDATE_CUSTOMER_ADDRESS_DETAILS.getConfigName(), requestPayload, serviceErrors);
        }
        catch(SoapFaultClientException sfe)
        {
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
     
        return true;
    }
}
