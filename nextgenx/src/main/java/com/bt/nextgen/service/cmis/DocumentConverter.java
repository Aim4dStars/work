package com.bt.nextgen.service.cmis;

import com.bt.nextgen.service.cmis.annotation.ColumnAnnotationProcessor;
import com.bt.nextgen.service.cmis.constants.DocumentCategories;
import com.bt.nextgen.service.cmis.constants.DocumentConstants;
import com.bt.nextgen.service.cmis.converter.Converter;
import com.bt.nextgen.service.integration.financialdocument.Document;
import com.bt.nextgen.service.integration.financialdocument.DocumentKey;
import org.apache.commons.beanutils.PropertyUtils;
import org.oasis_open.docs.ns.cmis.core._200908.CmisObjectType;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertiesType;
import org.oasis_open.docs.ns.cmis.core._200908.CmisProperty;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyId;
import org.oasis_open.docs.ns.cmis.messaging._200908.*;
import org.oasis_open.docs.ns.cmis.messaging._200908.CmisContentStreamType;
import org.oasis_open.docs.ns.cmis.messaging._200908.GetAllVersionsResponse;
import org.oasis_open.docs.ns.cmis.messaging._200908.GetContentStreamResponse;
import org.oasis_open.docs.ns.cmis.messaging._200908.ObjectFactory;
import org.oasis_open.docs.ns.cmis.messaging._200908.QueryResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBElement;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Converter to convert CmisResponse to CmisDocumentImpl object.
 */
@Component
@SuppressWarnings("squid:S1200")
public class DocumentConverter {

    private static final Logger logger = LoggerFactory.getLogger(DocumentConverter.class);

    private ColumnAnnotationProcessor processor = ColumnAnnotationProcessor.getInstance();

    /**
     * Converts QueryResponse  @See(QueryResponse)
     *
     * @param response
     *
     * @return a Collection of Document
     */
    public Collection<Document> convert(QueryResponse response) {
        List<Document> documents = new ArrayList<>();
        for (CmisObjectType cmisObject : response.getObjects().getObjects()) {
            CmisDocumentImpl cmisDocument = new CmisDocumentImpl();
            for (CmisProperty cmisProperty : cmisObject.getProperties().getProperty()) {
                String beanProperty = processor.getBeanProperty(cmisProperty);
                if (beanProperty != null) {
                    Converter converter = processor.getConverter(cmisProperty);
                    setBeanProperty(cmisDocument, beanProperty, converter.convert(cmisProperty));
                }
            }
            documents.add(cmisDocument);
        }
        return documents;
    }

    /**
     * Converts QueryResponse  @See(QueryResponse)
     *
     * @param response
     *
     * @return a Collection of Document
     */
    public Collection<Document> convert(GetAllVersionsResponse response) {
        List<Document> documents = new ArrayList<>();
        for (CmisObjectType cmisObject : response.getObjects()) {
            CmisDocumentImpl cmisDocument = new CmisDocumentImpl();
            for (CmisProperty cmisProperty : cmisObject.getProperties().getProperty()) {
                String beanProperty = processor.getBeanProperty(cmisProperty);
                if (beanProperty != null) {
                    Converter converter = processor.getConverter(cmisProperty);
                    setBeanProperty(cmisDocument, beanProperty, converter.convert(cmisProperty));
                }
            }
            documents.add(cmisDocument);
        }
        return documents;
    }

    /**
     * @param document
     *
     * @return
     */
    public CmisPropertiesType getUpdatedProperties(Document document) {
        CmisPropertiesType cmisPropertiesType = new CmisPropertiesType();
        List<CmisProperty> properties = cmisPropertiesType.getProperty();
        createProperties(properties, document);
        return cmisPropertiesType;
    }

    /**
     * @param document
     *
     * @return
     */
    public CmisPropertiesType createDocumentProperties(Document document) {
        CmisPropertiesType cmisPropertiesType = new CmisPropertiesType();
        List<CmisProperty> properties = cmisPropertiesType.getProperty();
        CmisPropertyId cmisproperty = new CmisPropertyId();
        cmisproperty.setPropertyDefinitionId(DocumentConstants.COLUMN_OBJECT_TYPE_ID);
        List<String> values = cmisproperty.getValue();
        values.add(getDocumentClass(document));
        properties.add(cmisproperty);
        createProperties(properties, document);
        return cmisPropertiesType;
    }

    private String getDocumentClass(Document document) {
        DocumentCategories category = DocumentCategories.forCode(document.getDocumentType());
        if (DocumentCategories.INVESTMENTS.equals(category) && DocumentConstants.DOCUMENT_SUB_TYPE_ASSET_TRANSFER.equals(document.getDocumentSubType())) {
            return DocumentConstants.DOCUMENT_CLASS_OFFLINE;
        }
        if (DocumentCategories.STATEMENTS.equals(category) && null != document.getDocumentTitleCode()) {
            switch (document.getDocumentTitleCode().toUpperCase()) {
                case "SPCENT":
                case "SPFEPK":
                case "SPPWPK":
                case "SPRBEN":
                case "SPEXIT":
                case "SPCENA":
                    return DocumentConstants.DOCUMENT_CLASS_OFFLINE;
                default:
                    return category.getDocumentClass();
            }
        }
        return category.getDocumentClass();
    }

    /**
     * @param properties
     * @param document
     */
    public void createProperties(List<CmisProperty> properties, Document document) {
        Collection<String> beanProperties = processor.getBeanProperties();
        Map<String, Object> propertiesToUpdate = getProperties(document);
        for (String property : beanProperties) {
            if (processor.isUpdatable(property)) {
                Object value = propertiesToUpdate.get(property);
                if (value != null) {
                    CmisProperty cmisProperty = convertToCmisProperty(property, value);
                    properties.add(cmisProperty);
                }
            }
        }
    }

    /**
     * @param property
     * @param value
     *
     * @return
     */
    public CmisProperty convertToCmisProperty(String property, Object value) {
        Converter converter = processor.getConverter(property);
        CmisProperty cmisProperty = (CmisProperty) converter.convertTo(value);
        cmisProperty.setPropertyDefinitionId(processor.getColumn(property));
        return cmisProperty;
    }


    public Document getDocumentFromContentResponse(DocumentKey documentKey, GetContentStreamResponse response) throws IOException {
        DataHandler dataHandler = response.getContentStream().getStream();
        InputStream in = dataHandler.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int n;
        while ((n = in.read()) > -1) {
            baos.write(n);
        }
        CmisDocumentImpl data = new CmisDocumentImpl();
        data.setDocumentKey(documentKey);
        data.setDocumentName(response.getContentStream().getFilename());
        data.setMimeType(response.getContentStream().getMimeType());
        data.setSize(response.getContentStream().getLength());
        data.setData(baos.toByteArray());
        return data;
    }

    private Map<String, Object> getProperties(Document financialDocument) {
        Map<String, Object> propertiesMap = null;
        try {
            propertiesMap = PropertyUtils.describe(financialDocument);
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error("Unable to process Document bean", e);
        }
        return propertiesMap;
    }

    public void setBeanProperty(CmisDocumentImpl cmisDocument, String beanProperty, Object property) {
        try {
            PropertyUtils.setProperty(cmisDocument, beanProperty, property);
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error("Unable to set " + beanProperty + " property of " + beanProperty.getClass().getName(), e);
        }
    }

    public CmisContentStreamType getContentStreamType(Document document) {
        ObjectFactory objectFactory = new ObjectFactory();
        CmisContentStreamType cmisContentStreamType = objectFactory.createCmisContentStreamType();
        DataSource dataSource = new ByteArrayDataSource(document.getData(), "application/octet-stream");
        DataHandler dataHandler = new DataHandler(dataSource);
        cmisContentStreamType.setStream(dataHandler);
        cmisContentStreamType.setMimeType(document.getMimeType());
        cmisContentStreamType.setLength(document.getSize());
        cmisContentStreamType.setFilename(document.getDocumentName());
        return cmisContentStreamType;
    }


    public CheckOut getCheckOutDocumentObject(Document document) {
        ObjectFactory objectFactory = new ObjectFactory();
        CheckOut checkOut = objectFactory.createCheckOut();
        checkOut.setRepositoryId(DocumentConstants.REPOSITORY);
        checkOut.setObjectId(document.getDocumentKey().getId());
        return checkOut;
    }

    public DeleteObject getDeleteDocumentObject(DocumentKey documentKey) {
        ObjectFactory objectFactory = new ObjectFactory();
        DeleteObject deleteObject = objectFactory.createDeleteObject();
        deleteObject.setObjectId(documentKey.getId());
        //setting allVersions to false - for current document deletion
        JAXBElement<Boolean> isAllVersions = objectFactory.createGetAllVersionsIncludeAllowableActions(false);
        deleteObject.setAllVersions(isAllVersions);
        deleteObject.setRepositoryId(DocumentConstants.REPOSITORY);

        return deleteObject;
    }

    public CheckIn getCheckInDocumentObject(Document document) {
        ObjectFactory objectFactory = new ObjectFactory();
        CheckIn checkIn = objectFactory.createCheckIn();
        checkIn.setRepositoryId(DocumentConstants.REPOSITORY);
        CmisPropertiesType cmisPropertiesType = getUpdatedProperties(document);
        JAXBElement<CmisPropertiesType> propertiesTypeJaxbElement = objectFactory.createCheckInProperties(cmisPropertiesType);
        checkIn.setProperties(propertiesTypeJaxbElement);
        //set content Stream
        JAXBElement<CmisContentStreamType> contentStreamTypeJaxbElement = objectFactory.createCheckInContentStream(getContentStreamType(document));
        checkIn.setContentStream(contentStreamTypeJaxbElement);
        return checkIn;
    }

    public UpdateProperties getUpdatePropertiesObject(Document document) {
        ObjectFactory objectFactory = new ObjectFactory();
        UpdateProperties updateProperties = new UpdateProperties();
        updateProperties.setProperties(getUpdatedProperties(document));
        updateProperties.setRepositoryId(DocumentConstants.REPOSITORY);
        updateProperties.setObjectId(document.getDocumentKey().getId());
        String changeToken = document.getChangeToken();
        JAXBElement<String> changeTokenJaxb = objectFactory.createUpdatePropertiesChangeToken(changeToken);
        updateProperties.setChangeToken(changeTokenJaxb);
        return updateProperties;
    }

    public CreateDocument getCreateDocumentObject(Document document, String folderId) {
        ObjectFactory objectFactory = new ObjectFactory();
        CreateDocument createDocument = objectFactory.createCreateDocument();
        createDocument.setRepositoryId(DocumentConstants.REPOSITORY);
        //set contentstream
        JAXBElement<CmisContentStreamType> contentStreamTypeJaxbElement = objectFactory.createCheckInContentStream(getContentStreamType(document));
        createDocument.setContentStream(contentStreamTypeJaxbElement);
        //set properties
        createDocument.setProperties(createDocumentProperties(document));
        createDocument.setFolderId(objectFactory.createCreateDocumentFolderId(folderId));
        return createDocument;
    }
}
