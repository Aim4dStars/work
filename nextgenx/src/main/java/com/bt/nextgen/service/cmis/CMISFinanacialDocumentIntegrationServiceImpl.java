package com.bt.nextgen.service.cmis;

import ch.lambdaj.Lambda;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocument;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentData;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentIntegrationService;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentKey;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType;
import com.bt.nextgen.service.integration.user.UserKey;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.oasis_open.docs.ns.cmis.core._200908.CmisObjectType;
import org.oasis_open.docs.ns.cmis.core._200908.CmisProperty;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyDateTime;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyId;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyInteger;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyString;
import org.oasis_open.docs.ns.cmis.messaging._200908.GetContentStream;
import org.oasis_open.docs.ns.cmis.messaging._200908.GetContentStreamResponse;
import org.oasis_open.docs.ns.cmis.messaging._200908.ObjectFactory;
import org.oasis_open.docs.ns.cmis.messaging._200908.Query;
import org.oasis_open.docs.ns.cmis.messaging._200908.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.annotation.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implements the document search service, querying the ICC supplied Oasis CMIS implmentation.
 * 
 * **IMPORTANT** The cmis system does not provide any kind of authorisation checks so we must query avaloq to ensure that the
 * current user has access to the accounts and documents being queried.
 * 
 */
@Service
public class CMISFinanacialDocumentIntegrationServiceImpl implements FinancialDocumentIntegrationService {
    private static final Logger logger = LoggerFactory.getLogger(CMISFinanacialDocumentIntegrationServiceImpl.class);
    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private WebServiceProvider provider;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;

    private static final String INVST_MGR = "INVST_MGR";
    private static final String REPOSITORY = Properties.getString("cmis.repository.id");

    private static final String ATTRIBUTE_OBJECT_ID = "cmis:objectId";
    private static final String ATTRIBUTE_TYPE = "PanoramaIPDocumentTitleCode";
    private static final String ATTRIBUTE_LENGTH = "cmis:contentStreamLength";
    private static final String ATTRIBUTE_PERIOD_START_DATE = "PanoramaIPStartDate";
    private static final String ATTRIBUTE_PERIOD_END_DATE = "PanoramaIPEndDate";
    private static final String ATTRIBUTE_ACCOUNT_ID = "PanoramaIPRelationshipID";
    private static final String ATTRIBUTE_GENERATION_DATE = "cmis:creationDate";
    private static final String ATTRIBUTE_FILE_EXTENSION_TYPE = "PanoramaIPDocumentFileExtension";
    private static final String ATTRIBUTE_NAME = "PanoramaIPDocumentName";

    private static final String SERVICE_TEMPLATE_QUERY = "cmisQuery";
    private static final String SEARCH_QUERY = "select cmis:objectId, PanoramaIPRelationshipID, PanoramaIPDocumentName, PanoramaIPDocumentTitleCode, cmis:contentStreamLength, PanoramaIPStartDate, PanoramaIPEndDate, PanoramaIPDocumentFileExtension, cmis:creationDate\n"
            + "from PanoramaIPStatement\n"
            + "where (PanoramaIPBusinessArea = 'PANORAMA')\n"
            + "and (PanoramaIPStartDate > timestamp '%s')\n"
            + "and (PanoramaIPEndDate < timestamp '%s')\n"
            + "and (PanoramaIPDocumentTitleCode in (%s))\n"
            + "and (PanoramaIPRelationshipType in (%s))\n"
            + "and (PanoramaIPRelationshipID in (%s))";

    private static final String OBJECT_ACCESS_QUERY = "select cmis:objectId, PanoramaIPRelationshipID, PanoramaIPDocumentTitleCode, cmis:contentStreamLength, PanoramaIPEndDate "
            + "from PanoramaIPStatement "
            + "where (PanoramaIPBusinessArea = 'PANORAMA') "
            + "and (PanoramaIPRelationshipType = '%s') " + "and (cmis:objectId = '%s' ) ";

    private static final String SERVICE_TEMPLATE_OBJECT = "cmisObject";

    private static final String SERVICE_OPS_SEARCH_QUERY = "select cmis:objectId, PanoramaIPRelationshipID, PanoramaIPDocumentTitleCode, cmis:contentStreamLength, PanoramaIPStartDate, PanoramaIPEndDate, PanoramaIPDocumentFileExtension, cmis:creationDate "
            + "from PanoramaIPDocument "
            + "where (PanoramaIPBusinessArea = 'PANORAMA') "
            + "and (PanoramaIPRelationshipType = '%s') " + "and (PanoramaIPRelationshipID = '%s')";

    private static final String SEARCH_XLS = "select cmis:objectId, PanoramaIPRelationshipID, PanoramaIPDocumentTitleCode, cmis:contentStreamLength, PanoramaIPStartDate, PanoramaIPEndDate, PanoramaIPDocumentFileExtension, cmis:creationDate%n "
            + "from PanoramaIPIMModelReport %n "
            + "where (PanoramaIPBusinessArea = 'PANORAMA') "
            + "and (PanoramaIPEndDate <= timestamp '%s') "
            + "and (PanoramaIPDocumentTitleCode = '%s') "
            + "and (PanoramaIPRelationshipType = '%s') "
            + "and (PanoramaIPRelationshipID = '%s') "
            + "and (PanoramaIPDocumentFileExtension = 'XLS')%n ";

    @Override
    public Collection<FinancialDocument> loadDocuments(Collection<AccountKey> accountKeys,
            Collection<FinancialDocumentType> documentTypes, DateTime fromDate, DateTime toDate, String relationshipType,
            ServiceErrors serviceErrors) {
        if (hasAccess(accountKeys, serviceErrors)) {
            ObjectFactory of = new ObjectFactory();
            Query query = of.createQuery();
            query.setRepositoryId(REPOSITORY);

            // *IMPORTANT* Be careful with strings here. We're currently
            // replacing account key which has been checked with
            // avaloq, data times which have been verified for format
            // correctness and document types which are restricted
            // to restricted enum values. If you add extra parameters you _must_
            // ensure you have validated it for
            // correctness or you will expose an sql-injection style
            // vunerablity.
            query.setStatement(String.format(SEARCH_QUERY, fromDate.toString(), toDate.toString(), toTypesString(documentTypes),
                    relationshipType, toAccountListStr(accountKeys, serviceErrors)));
            logger.info("CMIS Service query START:" + query.getStatement());
            QueryResponse response = (QueryResponse) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                    SERVICE_TEMPLATE_QUERY, query);
            logger.info("CMIS Service query END:" + query.getStatement());
            return toFinancialDocuments(response, relationshipType, serviceErrors);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Collection<FinancialDocument> loadDocuments(UserKey userKey, Collection<FinancialDocumentType> documentTypes,
            DateTime fromDate, DateTime toDate, String relationshipType, ServiceErrors serviceErrors) {
        logger.info("CMISFinanacialDocumentIntegrationServiceImpl:loadDocuments:userKey:start");
        ObjectFactory of = new ObjectFactory();
        Query query = of.createQuery();
        query.setRepositoryId(REPOSITORY);

        query.setStatement(String.format(SEARCH_QUERY, fromDate.toString(), toDate.toString(), toTypesString(documentTypes),
                toTypeString(Collections.singletonList(relationshipType)),
                toTypeString(Collections.singletonList(userKey.getId()))));
        QueryResponse response = (QueryResponse) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                SERVICE_TEMPLATE_QUERY, query);
        logger.info("CMISFinanacialDocumentIntegrationServiceImpl:loadDocuments:userKey:query:" + query.getStatement());
        return toFinancialDocuments(response, relationshipType, serviceErrors);
    }

    @Override
    public Collection<FinancialDocument> loadDocuments(BrokerKey brokerKey, Collection<FinancialDocumentType> documentTypes,
            DateTime fromDate, DateTime toDate, List<String> relationshipTypes, ServiceErrors serviceErrors) {

        return loadDocumentsForBrokers(Collections.singletonList(brokerKey), documentTypes, fromDate, toDate, relationshipTypes,
                serviceErrors);
    }

    @Override
    public Collection<FinancialDocument> loadDocuments(AccountKey accountKey, Collection<FinancialDocumentType> documentTypes,
            DateTime fromDate, DateTime toDate, String relationshipType, ServiceErrors serviceErrors) {
        return loadDocuments(Collections.singletonList(accountKey), documentTypes, fromDate, toDate, relationshipType,
                serviceErrors);
    }

    @Override
    public Collection<FinancialDocument> loadDocuments(Collection<FinancialDocumentType> documentTypes, DateTime fromDate,
            DateTime toDate, String relationshipType, ServiceErrors serviceErrors) {
        Collection<AccountKey> availableAccounts = accountService.loadWrapAccountWithoutContainers(serviceErrors).keySet();
        return loadDocuments(availableAccounts, documentTypes, fromDate, toDate, relationshipType, serviceErrors);
    }

    @Override
    public FinancialDocumentData loadDocument(FinancialDocumentKey documentKey, String relationshipType,
            ServiceErrors serviceErrors) {
        if (hasAccess(documentKey, relationshipType, serviceErrors)) {
            try {
                ObjectFactory of = new ObjectFactory();
                GetContentStream contentRequest = of.createGetContentStream();
                contentRequest.setRepositoryId(REPOSITORY);
                contentRequest.setObjectId(documentKey.getId());

                GetContentStreamResponse contentResponse = (GetContentStreamResponse) provider.sendWebServiceWithSecurityHeader(
                        userSamlService.getSamlToken(), SERVICE_TEMPLATE_OBJECT, contentRequest);
                return toFinancialDocumentData(documentKey, contentResponse);
            } catch (IOException e) {
                ServiceErrorImpl error = new ServiceErrorImpl();
                error.setReason("Unable to process document stream for " + documentKey.getId());
                error.setException(e);
                serviceErrors.addError(error);
            }
        }
        return null;
    }

    private Collection<FinancialDocument> toFinancialDocuments(QueryResponse response, String relationshipType,
            ServiceErrors serviceErrors) {
        List<FinancialDocument> result = new ArrayList<>();

        for (CmisObjectType documentMatch : response.getObjects().getObjects()) {
            FinancialDocumentImpl document = new FinancialDocumentImpl();
            for (CmisProperty property : documentMatch.getProperties().getProperty()) {
                switch (property.getPropertyDefinitionId()) {
                    case ATTRIBUTE_OBJECT_ID:
                        document.setDocumentKey(FinancialDocumentKey.valueOf(((CmisPropertyId) property).getValue().get(0)));
                        break;
                    case ATTRIBUTE_ACCOUNT_ID:
                        if (Constants.RELATIONSHIP_TYPE_DG.equals(relationshipType)) {
                            document.setDealerGroupKey(BrokerKey.valueOf(((CmisPropertyString) property).getValue().get(0)));
                        } else if (Constants.RELATIONSHIP_TYPE_ADVISER.equals(relationshipType)) {
                            document.setCustomerKey(UserKey.valueOf(((CmisPropertyString) property).getValue().get(0)));
                        } else if (INVST_MGR.equals(relationshipType)) {
                            document.setGcmId(((CmisPropertyString) property).getValue().get(0));
                        } else {
                            Map<AccountKey, WrapAccount> accountMap = accountService
                                    .loadWrapAccountWithoutContainers(serviceErrors);
                            Map<String, WrapAccount> accountNumberMap = Lambda.index(accountMap.values(),
                                    Lambda.on(WrapAccount.class).getAccountNumber());
                            WrapAccount account = accountNumberMap.get(((CmisPropertyString) property).getValue().get(0));
                            document.setAccountKey(account == null ? null : account.getAccountKey());
                        }
                        break;
                    case ATTRIBUTE_NAME:
                        document.setDocumentName(((CmisPropertyString) property).getValue().get(0));
                        break;
                    case ATTRIBUTE_TYPE:
                        document.setDocumentType(FinancialDocumentType.forCode(((CmisPropertyString) property).getValue().get(0)));
                        break;
                    case ATTRIBUTE_LENGTH:
                        document.setSize(((CmisPropertyInteger) property).getValue().get(0));
                        break;
                    case ATTRIBUTE_PERIOD_END_DATE:
                        document.setPeriodEndDate(new DateTime(((CmisPropertyDateTime) property).getValue().get(0)
                                .toGregorianCalendar().getTime()));
                        break;
                    case ATTRIBUTE_PERIOD_START_DATE:
                        document.setPeriodStartDate(new DateTime(((CmisPropertyDateTime) property).getValue().get(0)
                                .toGregorianCalendar().getTime()));
                        break;
                    case ATTRIBUTE_GENERATION_DATE:
                        document.setGenerationDate(new DateTime(((CmisPropertyDateTime) property).getValue().get(0)
                                .toGregorianCalendar().getTime()));
                        break;
                    case ATTRIBUTE_FILE_EXTENSION_TYPE:
                        document.setExtensionType(((CmisPropertyString) property).getValue().get(0));
                        break;
                    default:
                        break;
                }
            }
            result.add(document);
        }
        return result;
    }

    private Collection<FinancialDocument> toFinancialDocuments(QueryResponse response, List<String> relationshipTypes,
            ServiceErrors serviceErrors) {
        List<FinancialDocument> result = new ArrayList<>();

        for (CmisObjectType documentMatch : response.getObjects().getObjects()) {
            FinancialDocumentImpl document = new FinancialDocumentImpl();
            for (CmisProperty property : documentMatch.getProperties().getProperty()) {
                populateFinancialDocument(relationshipTypes, serviceErrors, document, property);
            }
            result.add(document);
        }
        return result;
    }

    private void populateFinancialDocument(List<String> relationshipTypes, ServiceErrors serviceErrors,
            FinancialDocumentImpl document, CmisProperty property) {
        switch (property.getPropertyDefinitionId()) {
            case ATTRIBUTE_OBJECT_ID:
                document.setDocumentKey(FinancialDocumentKey.valueOf(((CmisPropertyId) property).getValue().get(0)));
                break;
            case ATTRIBUTE_ACCOUNT_ID:
                setRelationshipTypeKey(relationshipTypes, serviceErrors, document, property);
                break;
            case ATTRIBUTE_NAME:
                document.setDocumentName(((CmisPropertyString) property).getValue().get(0));
                break;
            case ATTRIBUTE_TYPE:
                document.setDocumentType(FinancialDocumentType.forCode(((CmisPropertyString) property).getValue().get(0)));
                break;
            case ATTRIBUTE_LENGTH:
                document.setSize(((CmisPropertyInteger) property).getValue().get(0));
                break;
            case ATTRIBUTE_PERIOD_END_DATE:
                document.setPeriodEndDate(new DateTime(((CmisPropertyDateTime) property).getValue().get(0).toGregorianCalendar()
                        .getTime()));
                break;
            case ATTRIBUTE_PERIOD_START_DATE:
                document.setPeriodStartDate(new DateTime(((CmisPropertyDateTime) property).getValue().get(0)
                        .toGregorianCalendar().getTime()));
                break;
            case ATTRIBUTE_GENERATION_DATE:
                document.setGenerationDate(new DateTime(((CmisPropertyDateTime) property).getValue().get(0).toGregorianCalendar()
                        .getTime()));
                break;
            case ATTRIBUTE_FILE_EXTENSION_TYPE:
                document.setExtensionType(((CmisPropertyString) property).getValue().get(0));
                break;
            default:
                break;
        }
    }

    private void setRelationshipTypeKey(List<String> relationshipTypes, ServiceErrors serviceErrors,
            FinancialDocumentImpl document, CmisProperty property) {
        if (relationshipTypes.contains(Constants.RELATIONSHIP_TYPE_DG)
                || relationshipTypes.contains(Constants.RELATIONSHIP_TYPE_INV_MGR)) {
            document.setDealerGroupKey(BrokerKey.valueOf(((CmisPropertyString) property).getValue().get(0)));
        } else if (relationshipTypes.contains(Constants.RELATIONSHIP_TYPE_ADVISER)) {
            document.setCustomerKey(UserKey.valueOf(((CmisPropertyString) property).getValue().get(0)));
        } else if (relationshipTypes.contains(INVST_MGR)) {
            document.setGcmId(((CmisPropertyString) property).getValue().get(0));
        } else {
            Map<AccountKey, WrapAccount> accountMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);
            Map<String, WrapAccount> accountNumberMap = Lambda.index(accountMap.values(), Lambda.on(WrapAccount.class)
                    .getAccountNumber());
            WrapAccount account = accountNumberMap.get(((CmisPropertyString) property).getValue().get(0));
            document.setAccountKey(account == null ? null : account.getAccountKey());
        }
    }

    private FinancialDocumentData toFinancialDocumentData(FinancialDocumentKey documentKey, GetContentStreamResponse response)
            throws IOException {
        DataHandler dataHandler = response.getContentStream().getStream();

        InputStream in = dataHandler.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int n;
        while ((n = in.read()) > -1) {
            baos.write(n);
        }

        FinancialDocumentDataImpl data = new FinancialDocumentDataImpl();
        data.setDocumentKey(documentKey);
        data.setData(baos.toByteArray());
        return data;
    }

    private String toTypesString(Collection<FinancialDocumentType> documentTypes) {
        Iterator<FinancialDocumentType> i = documentTypes.iterator();
        StringBuilder builder = new StringBuilder();
        builder.append('\'');
        builder.append(i.next().getCode());
        builder.append('\'');
        while (i.hasNext()) {
            builder.append(',');
            builder.append('\'');
            builder.append(i.next().getCode());
            builder.append('\'');
        }
        return builder.toString();
    }

    private String toTypeString(List<String> inputValues) {
        Iterator<String> i = inputValues.iterator();
        StringBuilder builder = new StringBuilder();
        builder.append('\'');
        builder.append(i.next());
        builder.append('\'');

        while (i.hasNext()) {
            builder.append(',');
            builder.append('\'');
            builder.append(i.next());
            builder.append('\'');
        }

        return builder.toString();
    }

    private String toAccountListStr(Collection<AccountKey> accountKeys, ServiceErrors serviceErrors) {
        Map<AccountKey, WrapAccount> accountMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);

        Iterator<AccountKey> i = accountKeys.iterator();
        StringBuilder builder = new StringBuilder();
        builder.append('\'');
        builder.append(accountMap.get(i.next()).getAccountNumber());
        builder.append('\'');
        while (i.hasNext()) {
            builder.append(',');
            builder.append('\'');
            builder.append(accountMap.get(i.next()).getAccountNumber());
            builder.append('\'');
        }
        return builder.toString();
    }

    private boolean hasAccess(Collection<AccountKey> accountKeys, ServiceErrors serviceErrors) {
        // returns false if the user doesn't have access to any account in the
        // list.
        // if the current user can load an account from the account service
        // then they are allowed to view thier documents
        Map<AccountKey, WrapAccount> accountMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);

        for (AccountKey key : accountKeys) {
            if (accountMap.get(key) == null) {
                return false;
            }
        }
        return true;
    }

    private boolean hasAccess(FinancialDocumentKey documentKey, String relationshipType, ServiceErrors serviceErrors) {
        // to determine if the user has access to a requested document we must
        // query the account
        // associated with it and then see if avaloq allows access to that
        // account
        ObjectFactory of = new ObjectFactory();
        Query query = of.createQuery();
        query.setRepositoryId(REPOSITORY);

        query.setStatement(String.format(OBJECT_ACCESS_QUERY, relationshipType, documentKey.getId()));

        QueryResponse response = (QueryResponse) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                SERVICE_TEMPLATE_QUERY, query);
        Collection<FinancialDocument> docs = toFinancialDocuments(response, relationshipType, serviceErrors);
        if (Constants.RELATIONSHIP_TYPE_ACCOUNT.equals(relationshipType)) {
            return CollectionUtils.isNotEmpty(docs)
                    && hasAccess(Collections.singletonList(docs.iterator().next().getAccountKey()), serviceErrors);
        } else if (Constants.RELATIONSHIP_TYPE_ADVISER.equals(relationshipType)
                || Constants.RELATIONSHIP_TYPE_DG.equals(relationshipType)) {
            return CollectionUtils.isNotEmpty(docs) && hasAccess(docs.iterator().next(), serviceErrors);
        } else if (INVST_MGR.equals(relationshipType) || Constants.RELATIONSHIP_TYPE_INV_MGR.equals(relationshipType)) {
            return !userProfileService.isEmulating();
        }
        return false;
    }

    private boolean hasAccess(FinancialDocument document, ServiceErrors serviceErrors) {
        if (document.getDealerGroupKey() != null) {
            // TODO - UPS REFACTOR1 - this needs to be account specific
            BrokerKey key = userProfileService.getDealerGroupBroker().getKey();
            return document.getDealerGroupKey().equals(key);
        } else {
            Broker broker = brokerService.getBroker(BrokerKey.valueOf(userProfileService.getPositionId()), serviceErrors);
            return broker.getBankReferenceKey().getId().equals(document.getCustomerKey().getId());
        }
    }

    @Override
    public Collection<FinancialDocument> loadAllDocuments(String accountNumber, String relationshipType,
            ServiceErrors serviceErrors) {
        ObjectFactory of = new ObjectFactory();
        Query query = of.createQuery();
        query.setRepositoryId(REPOSITORY);

        query.setStatement(String.format(SERVICE_OPS_SEARCH_QUERY, relationshipType, accountNumber));

        logger.info("CMIS Service query START:" + query.getStatement());
        QueryResponse response = (QueryResponse) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                SERVICE_TEMPLATE_QUERY, query);
        logger.info("CMIS Service query END:" + query.getStatement());
        return toFinancialDocuments(response, relationshipType, serviceErrors);
    }

    @Override
    public FinancialDocumentData loadDocumentContent(FinancialDocumentKey documentKey, ServiceErrors serviceErrors) {
        try {
            ObjectFactory of = new ObjectFactory();
            GetContentStream contentRequest = of.createGetContentStream();
            contentRequest.setRepositoryId(REPOSITORY);
            contentRequest.setObjectId(documentKey.getId());

            GetContentStreamResponse contentResponse = (GetContentStreamResponse) provider.sendWebServiceWithSecurityHeader(
                    userSamlService.getSamlToken(), SERVICE_TEMPLATE_OBJECT, contentRequest);
            return toFinancialDocumentData(documentKey, contentResponse);
        } catch (IOException e) {
            ServiceErrorImpl error = new ServiceErrorImpl();
            error.setReason("Unable to process document stream for " + documentKey.getId());
            error.setException(e);
            serviceErrors.addError(error);
            return null;
        }
    }

    @Override
    public FinancialDocumentData loadIMDocument(String documentType, String modelId, DateTime toDate, String relationshipId,
            String relationshipType, ServiceErrors serviceErrors) {

        ObjectFactory of = new ObjectFactory();
        Query query = of.createQuery();
        query.setRepositoryId(REPOSITORY);

        String qryString = SEARCH_XLS;
        if (modelId != null) {
            qryString += " and (PanoramaIPModelId = '%s') order by PanoramaIPEndDate DESC ";
            query.setStatement(String.format(qryString, toDate.toString(), documentType, relationshipType, relationshipId,
                    modelId));
        } else {
            qryString += "order by PanoramaIPEndDate DESC ";
            query.setStatement(String.format(qryString, toDate.toString(), documentType, relationshipType, relationshipId));
        }

        logger.info("CMIS Service for query (XLS) START:" + query.getStatement());
        QueryResponse response = (QueryResponse) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                SERVICE_TEMPLATE_QUERY, query);
        logger.info("CMIS Service query (XLS) END:" + query.getStatement());
        Collection<FinancialDocument> results = toFinancialDocuments(response, relationshipType, serviceErrors);

        if (!results.isEmpty()) {
            FinancialDocumentKey docKey = results.iterator().next().getDocumentKey();
            return loadDocument(docKey, relationshipType, serviceErrors);
        } else {
            logger.warn("No results retrieved from CMIS service.");
        }
        return null;
    }

    @Override
    public Collection<FinancialDocument> loadDocumentsForBrokers(List<BrokerKey> brokerKeys,
            Collection<FinancialDocumentType> documentTypes, DateTime fromDate, DateTime toDate, List<String> relationshipTypes,
            ServiceErrors serviceErrors) {
        ObjectFactory of = new ObjectFactory();
        Query query = of.createQuery();
        query.setRepositoryId(REPOSITORY);

        query.setStatement(String.format(SEARCH_QUERY, fromDate.toString(), toDate.toString(), toTypesString(documentTypes),
                toTypeString(relationshipTypes), toTypeString(Lambda.extract(brokerKeys, Lambda.on(BrokerKey.class).getId()))));
        QueryResponse response = (QueryResponse) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                SERVICE_TEMPLATE_QUERY, query);
        return toFinancialDocuments(response, relationshipTypes, serviceErrors);
    }
}
