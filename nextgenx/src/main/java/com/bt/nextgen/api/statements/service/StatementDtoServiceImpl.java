package com.bt.nextgen.api.statements.service;

import com.bt.nextgen.api.statements.model.StatementDto;
import com.bt.nextgen.api.statements.model.StatementKey;
import com.bt.nextgen.api.statements.model.SupplimentaryDocument;
import com.bt.nextgen.api.statements.util.DocumentsDtoServiceUtil;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.NotAllowedException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocument;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentIntegrationService;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
class StatementDtoServiceImpl implements StatementDtoService {
    @Autowired
    private FinancialDocumentIntegrationService documentService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    private BrokerHelperService brokerHelperService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private InvestmentPolicyStatementIntegrationService invPolicyService;

    @Override
    public List<StatementDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        List<FinancialDocumentType> documentTypes = new ArrayList();
        List<StatementDto> statements = new ArrayList<>();

        DateTime startDate = null, endDate = null;
        AccountKey accountKey = null;
        for (ApiSearchCriteria criteria : criteriaList) {
            if (Attribute.ACCOUNT_ID.equals(criteria.getProperty())) {
                accountKey = AccountKey.valueOf(EncodedString.toPlainText(criteria.getValue()));
            }
            // This is messed up. It should have period end date > criteria.getProperty
            else if (Attribute.START_DATE.equals(criteria.getProperty())) {
                startDate = new DateTime(criteria.getValue());
            }
            // and period end date < criteria.getProperty
            else if (Attribute.END_DATE.equals(criteria.getProperty())) {
                endDate = new DateTime(criteria.getValue());
            } else if (Attribute.DOCUMENT_TYPES.equals(criteria.getProperty())) {
                // should have a list type rather than using string
                String docTypes = criteria.getValue();
                if (!StringUtils.isBlank(docTypes)) {
                    List<String> docTypeList = Arrays.asList(docTypes.split(","));
                    for (String docTypeStr : docTypeList) {
                        documentTypes.add(FinancialDocumentType.forCode(docTypeStr));
                    }
                }
            }
        }

        // if docTypes is not specified, query all doc types
        // **This will not work as every type of document needs different relationship type(i.e accountid, GCMID)
        // To be discussed
        if (CollectionUtils.isEmpty(documentTypes)) {
            documentTypes = new ArrayList<>(Arrays.asList(FinancialDocumentType.values()));
        }

        if (documentTypes.contains(FinancialDocumentType.FEE_REVENUE_STATEMENT)) {
            statements.addAll(getFeeRevenueStatements(startDate, endDate, documentTypes, serviceErrors));

            // remove fee revenue from the list to process account type request
            documentTypes.remove(FinancialDocumentType.FEE_REVENUE_STATEMENT);
        }

        if (!CollectionUtils.isEmpty(documentTypes)) {
            statements.addAll(getAccountStatements(startDate, endDate, accountKey, documentTypes, serviceErrors));
        }

        return statements;
    }

    /**
     * @param startDate
     * @param endDate
     * @param accountKey
     * @param documentTypes
     * @param serviceErrors
     * @return List of statements related to account
     */
    protected List<StatementDto> getAccountStatements(DateTime startDate, DateTime endDate, AccountKey accountKey,
                                                      List<FinancialDocumentType> documentTypes, ServiceErrors serviceErrors) {
        Collection<FinancialDocument> documents = Collections.EMPTY_LIST;
        if (accountKey != null) {
            documents = documentService.loadDocuments(accountKey, documentTypes, startDate, endDate,
                    Constants.RELATIONSHIP_TYPE_ACCOUNT, serviceErrors);
        } else {
            documents = documentService.loadDocuments(documentTypes, startDate, endDate, Constants.RELATIONSHIP_TYPE_ACCOUNT,
                    serviceErrors);
        }
        return toStatementDto(documents, serviceErrors);
    }

    /**
     * @param startDate
     * @param endDate
     * @param documentTypes
     * @param serviceErrors
     * @return List of statements for fee revenue
     */
    protected List<StatementDto> getFeeRevenueStatements(DateTime startDate, DateTime endDate,
                                                         List<FinancialDocumentType> documentTypes, ServiceErrors serviceErrors) {

        Collection<FinancialDocument> documents = Collections.emptyList();
        if (userProfileService.isAdviser()) {
            Broker broker = brokerService.getBroker(BrokerKey.valueOf(userProfileService.getPositionId()), serviceErrors);
            documents = documentService.loadDocuments(broker.getBankReferenceKey(), documentTypes, startDate, endDate,
                    Constants.RELATIONSHIP_TYPE_ADVISER, serviceErrors);
        } else if (userProfileService.isDealerGroup() || userProfileService.isInvestmentManager()) {
            // TODO - UPS REFACTOR1 - We need to pass in more information from the JSON API about the context of the dealergroup
            // (whether it is adviser or account specific).
            BrokerKey dealerGroupKey = userProfileService.getDealerGroupBroker().getKey();
            final List<BrokerKey> brokerKeyList = new ArrayList<>();
            brokerKeyList.add(dealerGroupKey);

            if (userProfileService.isDealerGroup()) {
                // Retrieve the IMs for all models accessible by this dealer-group.
                brokerKeyList.addAll(invPolicyService.getInvestmentManagerFromModel(dealerGroupKey, serviceErrors));
            }
            List<String> relationshipTypes = new ArrayList<>();
            relationshipTypes.add(Constants.RELATIONSHIP_TYPE_DG);
            relationshipTypes.add(Constants.RELATIONSHIP_TYPE_INV_MGR);
            documents = documentService.loadDocumentsForBrokers(brokerKeyList, documentTypes, startDate, endDate,
                    relationshipTypes, serviceErrors);
        } else if (userProfileService.isPortfolioManager()) {
            final List<BrokerKey> brokerKeyList = new ArrayList<>();
            brokerKeyList.add(userProfileService.getInvestmentManager(serviceErrors).getKey());
            documents = documentService.loadDocumentsForBrokers(brokerKeyList, documentTypes, startDate, endDate,
                    Collections.singletonList(Constants.RELATIONSHIP_TYPE_INV_MGR), serviceErrors);
        } else {

            throw new NotAllowedException(ApiVersion.CURRENT_VERSION, "No Access for the user");
        }
        return toStatementDtoForFeeRevenue(documents);
    }

    protected List<StatementDto> toStatementDto(Collection<FinancialDocument> documents, ServiceErrors serviceErrors) {
        final List<StatementDto> statements = new ArrayList<>();
        for (FinancialDocument document : documents) {
            final WrapAccount wrapAccount = accountService.loadWrapAccount(document.getAccountKey(), serviceErrors);
            final UserExperience userExperience = brokerHelperService.getUserExperience(wrapAccount, serviceErrors);
            final List<SupplimentaryDocument> supplementaryDocuments = DocumentsDtoServiceUtil.getSupplementaryDocuments(document, wrapAccount, userExperience);
            final String productName = retrieveProductName(serviceErrors, wrapAccount);
            final String adviserName = retrieveAdviserName(serviceErrors, wrapAccount);

            statements.add(new StatementDto(new com.bt.nextgen.api.account.v1.model.AccountKey(EncodedString.fromPlainText(
                    document.getAccountKey().getId()).toString()), new StatementKey(document.getDocumentKey().getId()), document
                    .getDocumentName(), document.getDocumentType().getDescription(), document.getSize(), null,
                    supplementaryDocuments, wrapAccount.getAccountKey().getId(), wrapAccount.getAccountNumber(), wrapAccount
                    .getAccountName(), wrapAccount.getAccountType().toString(), wrapAccount.getAdviserPersonId().getId(),
                    adviserName, wrapAccount.getProductKey().getId(), productName, document.getPeriodStartDate(), document
                    .getPeriodEndDate(), document.getGenerationDate(), document.getExtensionType()));
        }
        return statements;
    }

    protected List<StatementDto> toStatementDtoForFeeRevenue(Collection<FinancialDocument> documents) {
        List<StatementDto> statements = new ArrayList<>();
        for (FinancialDocument document : documents) {
            statements.add(new StatementDto(null, new StatementKey(document.getDocumentKey().getId()),
                    document.getDocumentName(), document.getDocumentType().getDescription(), document.getSize(), null, null,
                    null, null, null, null, null, null, null, null, document.getPeriodStartDate(), document.getPeriodEndDate(),
                    document.getGenerationDate(), document.getExtensionType()));
        }
        return statements;
    }

    /**
     * @param serviceErrors
     * @param wrapAccount
     * @return adviserName
     */
    private String retrieveAdviserName(ServiceErrors serviceErrors, WrapAccount wrapAccount) {
        String adviserName = null;
        if (null != wrapAccount.getAdviserPositionId()) {
            UserKey userKey = UserKey.valueOf(wrapAccount.getAdviserPositionId().getId());
            BrokerUser broker = brokerService.getBrokerUser(userKey, serviceErrors);
            if (broker != null) {
                adviserName = broker.getLastName() + ", " + broker.getFirstName();
            }
        }
        return adviserName;
    }

    /**
     * @param serviceErrors
     * @param wrapAccount
     * @return productName
     */
    private String retrieveProductName(ServiceErrors serviceErrors, WrapAccount wrapAccount) {
        String productName = null;

        if (null != wrapAccount.getProductKey()) {
            Product product = productIntegrationService.getProductDetail(wrapAccount.getProductKey(), serviceErrors);
            if (product != null) {
                productName = product.getProductName();
            }
        }
        return productName;
    }
}
