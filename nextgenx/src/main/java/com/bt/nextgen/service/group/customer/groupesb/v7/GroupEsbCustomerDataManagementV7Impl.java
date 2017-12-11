package com.bt.nextgen.service.group.customer.groupesb.v7;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.InvolvedPartyArrangementRole;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.utility.xsd.pagination.v1.PaginationInstruction;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.WebServiceProviderConfig;
import com.bt.nextgen.core.repository.WestpacProduct;
import com.bt.nextgen.core.repository.WestpacProductsRepository;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.service.ErrorHandlerUtil;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerDataImpl;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.phone.v7.CustomerPhoneV7Converter;
import com.btfin.panorama.service.integration.account.BankAccount;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by F057654 on 23/07/2015.
 */
@Deprecated
@Service("customerDataManagementV7Service")
public class GroupEsbCustomerDataManagementV7Impl implements CustomerDataManagementIntegrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupEsbCustomerDataManagementV7Impl.class);
    private static final String BANK_ACCOUNT = "BANK_ACCOUNT";
    private static final String CONSUMER_TRANSACTION_ACCOUNT = "Consumer Transaction Accounts";
    private static final String CASH_MANAGEMENT_ACCOUNT = "Cash Management Accounts";
    private static final String EVERGREEN = "EVERGREEN";

    @Autowired
    private WebServiceProvider provider;

    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;

    @Autowired
    private CmsService cmsService;

    @Autowired
    private WestpacProductsRepository westpacProductsRepository;

    @Override
    public CustomerData retrieveCustomerInformation(CustomerManagementRequest request, List<String> operationTypes, ServiceErrors serviceErrors) {
        RetrieveDetailsAndArrangementRelationshipsForIPsRequest requestPayload = GroupEsbUserDetailsRequestV7Builder.createRetrieveDetailsAndArrangementRelationships(request);
        RetrieveDetailsAndArrangementRelationshipsForIPsResponse response = retrieveCustomerDetailsFromWebservice(requestPayload, serviceErrors);
        return createResultFromServiceResponse(response, request, operationTypes, serviceErrors);
    }

    private RetrieveDetailsAndArrangementRelationshipsForIPsResponse retrieveCustomerDetailsFromWebservice(
        RetrieveDetailsAndArrangementRelationshipsForIPsRequest requestPayload, ServiceErrors serviceErrors) {
        CorrelatedResponse correlatedResponse;
        try {
            LOGGER.info("Calling web service to retrieve customer details and arrangements.");
            correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
                WebServiceProviderConfig.GROUP_ESB_RETRIEVE_CUSTOMER_DETAILS.getConfigName(), requestPayload, serviceErrors);
        } catch (SoapFaultClientException sfe) {
            ErrorHandlerUtil.parseSoapFaultException(serviceErrors, sfe, ServiceConstants.SERVICE_258);
            return null;
        }
        RetrieveDetailsAndArrangementRelationshipsForIPsResponse response = (RetrieveDetailsAndArrangementRelationshipsForIPsResponse)
            correlatedResponse.getResponseObject();
        String status = response.getServiceStatus().getStatusInfo().get(0).getLevel().toString();

        if (!status.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
            LOGGER.error("GroupEsbCustomerDataManagementImpl.retrieveCustomerInformation returning failure");
            ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_258,
                correlatedResponse.getCorrelationIdWrapper(), cmsService);
            return null;
        }
        LOGGER.info("Successful response returned when retrieving customer details and arrangements.");
        return response;
    }

    private CustomerData createResultFromServiceResponse(RetrieveDetailsAndArrangementRelationshipsForIPsResponse response,
        CustomerManagementRequest request, List<String> operationTypes, ServiceErrors serviceErrors) {
        LOGGER.info("Converting results into response object.");
        CustomerData data = new CustomerDataImpl();
        for (CustomerManagementOperation operation : request.getOperationTypes()) {
            switch (operation) {
                case ADDRESS_UPDATE:
                    data.setAddress(CustomerManagementDataV7Converter.convertResponseInAddressModel(response, request).getAddress());
                    break;
                case PREFERRED_NAME_UPDATE:
                    data.setPreferredName(CustomerManagementDataV7Converter.convertResponseToPreferredNameModel(response).getPreferredName());
                    break;
                case CONTACT_DETAILS_UPDATE:
                    data.setEmails(CustomerManagementDataV7Converter.convertResponseInEmailModel(response).getEmails());
                    data.setPhoneNumbers(CustomerPhoneV7Converter.convertResponseInPhone(response, request).getPhoneNumbers());
                    break;
                case ARRANGEMENTS:
                    if (operationTypes.contains(BANK_ACCOUNT)) {
                        setPaginatedArrangements(response, request, data, serviceErrors);
                    }
                    break;
                case INDIVIDUAL_DETAILS:
                    data.setIndividualDetails(CustomerManagementDataV7Converter.convertResponseToIndividualDetailsModel(response).getIndividualDetails());
                    break;
                default:
                    break;
            }
        }
        LOGGER.info("Returning converted results.");
        return data;
    }

    /**
     * Arrangement results are paginated if there are more than 55 results. This method recursively calls for the next set of results until
     * there are no more
     *
     * @param response
     * @param request
     * @param data
     * @param serviceErrors
     */
    private void setPaginatedArrangements(RetrieveDetailsAndArrangementRelationshipsForIPsResponse response, CustomerManagementRequest request,
       CustomerData data, ServiceErrors serviceErrors) {
        RetrieveDetailsAndArrangementRelationshipsForIPsResponse responseData = response;
        final List<BankAccount> bankAccounts = getBankAccountList(responseData.getIndividual().getIsPlayingRoleInArrangement());
        data.getBankAccounts().addAll(bankAccounts);
        if (null != responseData.getArrangementPaginationInstruction() && responseData.getArrangementPaginationInstruction().isIsMoreRecordsAvailable()) {
            PaginationInstruction arrangementPagination = responseData.getArrangementPaginationInstruction();
            LOGGER.info("{} records returned out of {}. Retrieving the next set of records.", arrangementPagination.getNumberOfRecordsReturned(), arrangementPagination.getTotalNumberOfRecords());
            request.setOperationTypes(Arrays.asList(CustomerManagementOperation.ARRANGEMENTS));
            RetrieveDetailsAndArrangementRelationshipsForIPsRequest requestPayload =
                GroupEsbUserDetailsRequestV7Builder.createPaginatedRetrieveDetailsAndArrangementRelationships(request, arrangementPagination);
            responseData = retrieveCustomerDetailsFromWebservice(requestPayload, serviceErrors);
            setPaginatedArrangements(responseData, request, data, serviceErrors);
        }
    }

    /**
     * Filter list of accounts to only return account which are bank accounts i.e. savings transaction accounts
     *
     * @param accountList
     * @return
     */
    private List<BankAccount> getBankAccountList(List<InvolvedPartyArrangementRole> accountList) {
        LOGGER.info("Converting bank account result list.");
        List<BankAccount> bankAccountList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(accountList)) {
            for (InvolvedPartyArrangementRole account : accountList) {
                WestpacProduct westpacProduct = westpacProductsRepository.load(getCpcForAccount(account));
                if (isBankAccountType(westpacProduct)) {
                    bankAccountList.add(CustomerManagementDataV7Converter.convertResponseToBankAccountModel(account, westpacProduct.getName()));
                }
            }
        }
        return bankAccountList;
    }

    /**
     * Get cpc for the account
     *
     * @param account
     * @return
     */
    private String getCpcForAccount(InvolvedPartyArrangementRole account) {
        if (account.getHasForContext() != null && account.getHasForContext().getAccountArrangementIdentifier() != null) {
            return account.getHasForContext().getAccountArrangementIdentifier().getCanonicalProductCode();
        }
        LOGGER.info("No account or CPC details found.");
        return null;
    }

    /**
     * Determine if an account is a savings transaction account
     *
     * @param westpacProduct
     * @return
     */
    private boolean isBankAccountType(WestpacProduct westpacProduct) {
        return westpacProduct != null && !EVERGREEN.equalsIgnoreCase(westpacProduct.getCategoryProductSystem())
            && "Y".equalsIgnoreCase(westpacProduct.getFundsTransferFrom()) && isTransactionAccountCategory(westpacProduct);
    }

    /**
     * Determine if an account's category is a consumer transaction account
     *
     * @param westpacProduct
     * @return
     */
    private boolean isTransactionAccountCategory(WestpacProduct westpacProduct) {
        return CONSUMER_TRANSACTION_ACCOUNT.equalsIgnoreCase(westpacProduct.getCategory())
            || CASH_MANAGEMENT_ACCOUNT.equalsIgnoreCase(westpacProduct.getCategory());
    }

    @Override
    public boolean updateCustomerInformation(CustomerData updatedData, ServiceErrors serviceErrors) {
        //Update should be handled separately as update service for each operation is different
        /* P.S. GroupEsbCustomerAddressManagementImpl
         */
        throw new UnsupportedOperationException();
    }
}
