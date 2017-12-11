package com.bt.nextgen.service.group.customer.groupesb.taxresidencecountry.v11;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualip.v5.svc0338.ModifyIndividualIPRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualip.v5.svc0338.ModifyIndividualIPResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyorganisationip.v5.svc0339.ModifyOrganisationIPRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyorganisationip.v5.svc0339.ModifyOrganisationIPResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
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
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.annotation.Resource;
import java.util.List;

@Service("taxResiCountryManagementV11Service")
public class GroupEsbCustomerTaxResidenceCountryImpl implements CustomerDataManagementIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(GroupEsbCustomerTaxResidenceCountryImpl.class);

    @Autowired
    private WebServiceProvider provider;

    @Autowired
    private CmsService cmsService;

    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;

    @Override
    @SuppressWarnings({"squid:MethodCyclomaticComplexity"})
    public CustomerData retrieveCustomerInformation(CustomerManagementRequest request, List<String> operationTypes, ServiceErrors serviceErrors) {
        CustomerData customerData = new CustomerDataImpl();
        if (request != null && request.getInvolvedPartyRoleType() != null && !request.getOperationTypes().isEmpty() && request.getCISKey() != null) {
            logger.info("Inside retrieveCustomerInformation() : Proceeding to clear the cache of Preferred name for user{}", request.getCISKey());

            try {
                final RetrieveDetailsAndArrangementRelationshipsForIPsRequest requestPayload = GroupEsbUserDetailsRequestBuilder.createRetrieveDetailsAndArrangementRelationships(request);
                final CorrelatedResponse correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
                        userSamlService.getSamlToken(), WebServiceProviderConfig.GROUP_ESB_RETRIEVE_CUSTOMER_DETAILS_V11.getConfigName(), requestPayload, serviceErrors);
                final RetrieveDetailsAndArrangementRelationshipsForIPsResponse response = (RetrieveDetailsAndArrangementRelationshipsForIPsResponse) correlatedResponse.getResponseObject();
                final String status = response.getServiceStatus().getStatusInfo().get(0).getLevel().toString();
                if (!status.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                    ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_258, correlatedResponse.getCorrelationIdWrapper(), cmsService);
                    return null;
                }

                if (RoleType.INDIVIDUAL.equals(request.getInvolvedPartyRoleType())) {
                    customerData = CustomerManagementDataConverter.convertResponseToTaxResidenceCountryModelForIndividual(response);
                }
                else {
                    customerData = CustomerManagementDataConverter.convertResponseToTaxResidenceCountryModelOrganisation(response);
                }
            }
            catch (SoapFaultClientException sfe) {
                ErrorHandlerUtil.parseSoapFaultException(serviceErrors, sfe, ServiceConstants.SERVICE_258);
                return null;
            }
        }
        logger.error("Retrieve failed for preferred name details");
        return customerData;
    }

    @Override
    public boolean updateCustomerInformation(CustomerData updatedData, ServiceErrors serviceErrors) {
        boolean responseStatus = false;
        if (RoleType.INDIVIDUAL.equals(updatedData.getRequest().getInvolvedPartyRoleType())) {
            responseStatus = updateIndividualTINDetails(updatedData, serviceErrors);
        }
        else if (RoleType.ORGANISATION.equals(updatedData.getRequest().getInvolvedPartyRoleType())) {
            responseStatus = updateOrganisationTINDetails(updatedData, serviceErrors);
        }
        return responseStatus;
    }

    /**
     * This method would update Individual client TIN details
     *
     * @param updatedData   object of {@link CustomerData}
     * @param serviceErrors object of {@link ServiceErrors}
     *
     * @return boolean if status is updated successfully
     */
    private boolean updateIndividualTINDetails(CustomerData updatedData, ServiceErrors serviceErrors) {
        CorrelatedResponse correlatedResponse = null;
        final ModifyIndividualIPRequest individualIPRequestPayload = GroupEsbIndividualTaxResidenceUpdateRequestBuilder.
                createModifyIndividualIPRequest(updatedData);
        try {
            logger.info("Updating Individual TIN details in UCM for user{}", updatedData.getRequest().getCISKey());
            correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
                    userSamlService.getSamlToken(), WebServiceProviderConfig.GROUP_ESB_UPDATE_CUSTOMER_DETAILS_INDIVIDUAL_V5.getConfigName(),
                    individualIPRequestPayload, serviceErrors);
            logger.info("Updating Individual TIN details completed in UCM for user{}", updatedData.getRequest().getCISKey());
        }
        catch (SoapFaultClientException sfe) {
            ErrorHandlerUtil.parseSoapFaultException(serviceErrors, sfe, ServiceConstants.SERVICE_338);
            return false;
        }
        if (correlatedResponse != null) {
            final ModifyIndividualIPResponse response = (ModifyIndividualIPResponse) correlatedResponse.getResponseObject();
            final String status = response.getServiceStatus().getStatusInfo().get(0).getLevel().toString();
            if (!status.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_338,
                        correlatedResponse.getCorrelationIdWrapper(), cmsService);
            }
            return status.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE) ? true : false;
        }
        return false;
    }

    private boolean updateOrganisationTINDetails(CustomerData updatedData, ServiceErrors serviceErrors) {
        CorrelatedResponse correlatedResponse = null;
        final ModifyOrganisationIPRequest organisationIPRequest = GroupEsbOrganisationTaxResidenceUpdateRequestBuilder.
                createModifyOrganisationIPRequest(updatedData);
        try {
            logger.info("Updating Organisation TIN details in UCM for user{}", updatedData.getRequest().getCISKey());
            correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
                    userSamlService.getSamlToken(), WebServiceProviderConfig.GROUP_ESB_UPDATE_CUSTOMER_DETAILS_ORGANISATION_V5.getConfigName(),
                    organisationIPRequest, serviceErrors);
            logger.info("Updating Organisation TIN details completed in UCM for user{}", updatedData.getRequest().getCISKey());
        }
        catch (SoapFaultClientException sfe) {
            ErrorHandlerUtil.parseSoapFaultException(serviceErrors, sfe, ServiceConstants.SERVICE_339);
            return false;
        }
        if (correlatedResponse != null) {
            final ModifyOrganisationIPResponse response = (ModifyOrganisationIPResponse) correlatedResponse.getResponseObject();
            final String status = response.getServiceStatus().getStatusInfo().get(0).getLevel().toString();
            if (!status.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_339,
                        correlatedResponse.getCorrelationIdWrapper(), cmsService);
            }
            return status.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE) ? true : false;
        }
        return false;
    }

}
