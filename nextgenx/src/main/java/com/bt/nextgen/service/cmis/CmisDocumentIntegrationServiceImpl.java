package com.bt.nextgen.service.cmis;

import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.cmis.constants.DocumentCategories;
import com.bt.nextgen.service.cmis.constants.DocumentConstants;
import com.bt.nextgen.service.integration.financialdocument.Criteria;
import com.bt.nextgen.service.integration.financialdocument.Document;
import com.bt.nextgen.service.integration.financialdocument.DocumentIntegrationService;
import com.bt.nextgen.service.integration.financialdocument.DocumentKey;
import com.bt.nextgen.service.integration.financialdocument.QueryBuilder;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.lang3.StringUtils;
import org.oasis_open.docs.ns.cmis.core._200908.CmisProperty;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyId;
import org.oasis_open.docs.ns.cmis.messaging._200908.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements DocumentIntegrationService, It connects Document management system(Filenet) via CMIS interface web services
 */
@Service
@SuppressWarnings("squid:S1200")
public class CmisDocumentIntegrationServiceImpl implements DocumentIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentConverter.class);

    @Autowired
    private WebServiceProvider provider;

    @Autowired
    private DocumentConverter converter;

    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;

    private static Map<String, String> folderIdMap =  new HashMap<>();

    private static String parentFolderId = null;

    @Override
    public Collection<Document> getDocuments(Criteria criteria) {
        QueryBuilder queryBuilder = new CmisQueryBuilderImpl();
        queryBuilder.setCriteria(criteria);
        return executeQuery(queryBuilder.getCmisQueryObject());
    }

    @Override
    public Document getDocumentData(DocumentKey documentKey) throws IOException {
        ObjectFactory of = new ObjectFactory();
        GetContentStream contentRequest = of.createGetContentStream();
        contentRequest.setRepositoryId(DocumentConstants.REPOSITORY);
        contentRequest.setObjectId(documentKey.getId());
        GetContentStreamResponse contentResponse = (GetContentStreamResponse) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                DocumentConstants.SERVICE_TEMPLATE_OBJECT,
                contentRequest);
        return converter.getDocumentFromContentResponse(documentKey, contentResponse);
    }

    @Override
    public Document updateDocument(Document document) {
        UpdateProperties updateProperties = converter.getUpdatePropertiesObject(document);
        UpdatePropertiesResponse response = (UpdatePropertiesResponse) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                DocumentConstants.SERVICE_TEMPLATE_UPDATE, updateProperties);
        CmisDocumentImpl responseDocument = new CmisDocumentImpl();
        responseDocument.setChangeToken(response.getChangeToken());
        responseDocument.setDocumentKey(document.getDocumentKey());
        return responseDocument;
    }

    @Override
    public Document createNewDocument(Document document) {
        logger.info("CmisDocumentIntegrationServiceImpl::createNewDocument: method invoked");
        String folderId = getFolderId(DocumentCategories.forCode(document.getDocumentType()),document);
        CreateDocument createDocument = converter.getCreateDocumentObject(document, folderId);
        CmisDocumentImpl responseDocument = new CmisDocumentImpl();
        logger.info("CmisDocumentIntegrationServiceImpl::createNewDocument: invoke cmisCreateObject");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CreateDocumentResponse response = (CreateDocumentResponse) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                DocumentConstants.SERVICE_TEMPLATE_OBJECT_CREATE, createDocument);
        stopWatch.stop();
        logger.info("CmisDocumentIntegrationServiceImpl::createNewDocument: cmisCreateObject complete time taken = {} ms", stopWatch.getTime());
        responseDocument.setDocumentKey(DocumentKey.valueOf(response.getObjectId()));
        logger.info("CmisDocumentIntegrationServiceImpl::createNewDocument: method complete");
        return responseDocument;
    }

    @Override
    public Document uploadNewVersionOfDocument(Document document) {
        logger.info("CmisDocumentIntegrationServiceImpl::uploadNewVersionOfDocument: method invoked");
        String checkoutDocId = null;
        try {
            CheckOut checkOut = converter.getCheckOutDocumentObject(document);
            logger.info("CmisDocumentIntegrationServiceImpl::uploadNewVersionOfDocument: invoke cmisCheckout");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            CheckOutResponse checkOutResponse = (CheckOutResponse) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                    DocumentConstants.CHECKOUT_DOCUMENT, checkOut);
            logger.info("CmisDocumentIntegrationServiceImpl::uploadNewVersionOfDocument: cmisCheckout complete");
            checkoutDocId = checkOutResponse.getObjectId();
            //get the checkin object
            CheckIn checkIn = converter.getCheckInDocumentObject(document);
            checkIn.setObjectId(checkoutDocId);
            logger.info("CmisDocumentIntegrationServiceImpl::uploadNewVersionOfDocument: invoke cmisCheckin");
            CheckInResponse checkInResponse = (CheckInResponse) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                    DocumentConstants.CHECKIN_DOCUMENT, checkIn);
            stopWatch.stop();
            logger.info("CmisDocumentIntegrationServiceImpl::uploadNewVersionOfDocument: cmisCheckin complete , total time taken = {} ms", stopWatch.getTime());
            CmisDocumentImpl responseDocument = new CmisDocumentImpl();
            responseDocument.setDocumentKey(DocumentKey.valueOf(checkInResponse.getObjectId()));
            responseDocument.setRelationshipId(document.getRelationshipId());
            logger.info("CmisDocumentIntegrationServiceImpl::uploadNewVersionOfDocument: method complete");
            return responseDocument;
        } catch (SoapFaultClientException ex) {
            logger.error("Error while uploading new verion for document id " + document.getDocumentKey().getId(), ex);
            if (checkoutDocId != null) {
                cancelCheckout(checkoutDocId);
            }
            throw ex;
        }
    }

    private Collection<Document> executeQuery(Query query) {
        QueryResponse response = (QueryResponse) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                DocumentConstants.SERVICE_TEMPLATE_QUERY, query);
        return converter.convert(response);
    }

    private String getFolderId(String folderPath) {
        ObjectFactory objectFactory = new ObjectFactory();
        GetObjectByPath getObjectByPath = objectFactory.createGetObjectByPath();
        getObjectByPath.setRepositoryId(DocumentConstants.REPOSITORY);
        getObjectByPath.setPath(folderPath);
        GetObjectByPathResponse folderIdResponse = (GetObjectByPathResponse) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                DocumentConstants.SERVICE_TEMPLATE_GET_PATH, getObjectByPath);
        List<CmisProperty> propertList = folderIdResponse.getObject().getProperties().getProperty();
        for (CmisProperty property : propertList) {
            if (DocumentConstants.OBJECT_ID_COLUMN.equalsIgnoreCase(property.getPropertyDefinitionId())) {
                return ((CmisPropertyId) property).getValue().get(0);
            }
        }
        return StringUtils.EMPTY;
    }

    private String getFolderId(DocumentCategories category ,Document document) {
        String folderPath ="";
        if( !isFolderPathException(document)){
           folderPath = category.getFolder();
        }
        else {
            folderPath = Properties.getString("cmis.folder.default");
        }
        String id = folderIdMap.get(folderPath);
        if (id == null) {
            id = getFolderId(folderPath);
            if (StringUtils.isEmpty(id)) {
                logger.error("Filenet folder not found for " + folderPath);
                throw new IllegalStateException("Filenet folder not found " + folderPath);
            }
            folderIdMap.put(folderPath, id);
        }
        return id;
    }
     //Add a condition here for un conventional folder ID requirements
    private boolean isFolderPathException(Document document) {
        if (null == document.getDocumentTitleCode()) {
            return false;
        } else {
            String documentTitleCode = document.getDocumentTitleCode().toUpperCase();
            switch (documentTitleCode){
                case "SPCENT":
                case "SPFEPK":
                case "SPPWPK":
                case "SPRBEN":
                case "SPEXIT":
                case "SPCENA":
                case "CMASTM":
                    return true;
                default:
                    return false;
            }
        }
    }

    @Override
    public Collection<Document> getDocumentVersions(DocumentKey documentKey) {
        ObjectFactory objectFactory = new ObjectFactory();
        GetAllVersions versions = objectFactory.createGetAllVersions();
        versions.setRepositoryId(DocumentConstants.REPOSITORY);
        versions.setObjectId(documentKey.getId());
        GetAllVersionsResponse response = (GetAllVersionsResponse) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                DocumentConstants.DOCUMENT_VERSIONS, versions);
        return converter.convert(response);
    }


    /**
     * Cancel checkout operation on CMIS
     *
     * @param documentId
     */
    public void cancelCheckout(String documentId) {
        try {
            ObjectFactory objectFactory = new ObjectFactory();
            CancelCheckOut request = objectFactory.createCancelCheckOut();
            request.setRepositoryId(DocumentConstants.REPOSITORY);
            request.setObjectId(documentId);
            provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(), DocumentConstants.CANCEL_CHECKOUT, request);
        } catch (SoapFaultClientException ex) {
            logger.error("Cancel Checkout failed for document id : " + documentId, ex);
            throw ex;
        }
    }

    @Override
    public boolean deleteDocument(DocumentKey documentKey) {
        boolean isDeleted = false;
        try {
            DeleteObject deleteObject = converter.getDeleteDocumentObject(documentKey);
            DeleteObjectResponse deleteObjectResponse = (DeleteObjectResponse) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                    DocumentConstants.DELETE_DOCUMENT, deleteObject);
            if(null != deleteObjectResponse){
                isDeleted = true;
            }
        } catch (SoapFaultClientException e) {
            logger.error("Error while deleting document for document id " + documentKey.getId(), e);
        }
        return isDeleted;
    }

    @Override
    public String getParentFolderId() {
        if(parentFolderId == null) {
            parentFolderId  = getFolderId(DocumentConstants.FOLDER_PATH);
        }
        return parentFolderId;
    }
}
