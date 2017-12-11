package com.bt.nextgen.service.avaloq.basil;

import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.FilterDocImageIndexPropsType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.FilterDocumentClassesType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.FilterIndexPropsType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.ImageRequestContextType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.ImageSearchFilterType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.InvertedIndexPropType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.KeyIndexPropType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.ObjectFactory;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.SearchDocPropsType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.SearchImagesRequestMsgType;
import ns.btfin_com.sharedservices.bpm.image.v1_0.DocImageIndexDatePropValueType;
import ns.btfin_com.sharedservices.bpm.image.v1_0.DocImageIndexPropType;
import ns.btfin_com.sharedservices.bpm.image.v1_0.DocImageIndexPropValuesType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * This class builds the SOAP request object of Basil document library
 * Created by M035995 on 27/09/2016.
 */
public class BasilRequestBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasilRequestBuilder.class);

    private static final String DOCUMENT_CLASS = "PRD_PLC_DOC";

    private static final String DOCUMENT_TYPE = "DocumentType";

    private static final String EFFECTIVE_DATE = "EffectiveDate";

    /**
     * Create request to retrieve document list from Basil service.
     *
     * @param propertyName  name of the property
     * @param propertyValue value of the property
     * @param gcmId         GCM ID
     * @param documentTypes the various document types used by BASIL
     *
     * @return SearchImagesRequestMsgType
     */

    public SearchImagesRequestMsgType getBasilRequest(String propertyName, String propertyValue,
                                                      String gcmId, String... documentTypes) {
        LOGGER.info("Started: Retrieving Basil images for GCMId {}", gcmId);
        final ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.ObjectFactory objectFactory = new ObjectFactory();

        final SearchDocPropsType searchDocPropsType = new SearchDocPropsType();
        searchDocPropsType.setKeyIndexProperty(setPolicyDetails(propertyName, propertyValue));
        searchDocPropsType.setFilterDocumentProperties(setFilterIndexProperties(documentTypes));

        final ImageSearchFilterType imageSearchFilterType = new ImageSearchFilterType();
        imageSearchFilterType.setDocumentProperties(searchDocPropsType);

        final SearchImagesRequestMsgType searchImagesRequestMsgType = objectFactory.createSearchImagesRequestMsgType();
        searchImagesRequestMsgType.setContext(setContext(gcmId));
        searchImagesRequestMsgType.setSearchFilter(imageSearchFilterType);
        LOGGER.info("Finished: Retrieving Basil images for GCMId: {}", gcmId);
        return searchImagesRequestMsgType;
    }

    private FilterIndexPropsType setFilterIndexProperties(String... documentTypes) {
        final FilterDocImageIndexPropsType filterDocImageIndexPropsType = new FilterDocImageIndexPropsType();

        DocImageIndexPropType docImageIndexPropType = new DocImageIndexPropType();
        docImageIndexPropType.setDocumentIndexPropertyName(DOCUMENT_TYPE);
        final DocImageIndexPropValuesType propValuesType = new DocImageIndexPropValuesType();
        propValuesType.getDocumentIndexStringPropertyValue().addAll(Arrays.asList(documentTypes));
        docImageIndexPropType.setDocumentIndexPropertyValues(propValuesType);
        filterDocImageIndexPropsType.getDocumentIndexProperty().add(docImageIndexPropType);


        docImageIndexPropType = new DocImageIndexPropType();
        docImageIndexPropType.setDocumentIndexPropertyName(EFFECTIVE_DATE);
        final DocImageIndexPropValuesType valuesType = new DocImageIndexPropValuesType();
        DocImageIndexDatePropValueType datePropValueType = new DocImageIndexDatePropValueType();

        try {
            final GregorianCalendar calendar = new GregorianCalendar();
            final DatatypeFactory df = DatatypeFactory.newInstance();
            final XMLGregorianCalendar dateTime = df.newXMLGregorianCalendar(calendar);
            datePropValueType.setDocumentIndexEndDateValue(dateTime);
            // Index date value should always be set to 11-Sept-2011
            calendar.set(2011, Calendar.SEPTEMBER, 11);
            datePropValueType.setDocumentIndexDateValue(df.newXMLGregorianCalendar(calendar));
        }
        catch (DatatypeConfigurationException e) {
            LOGGER.warn("Error while setting date" + e);
        }

        valuesType.getDocumentIndexDatePropertyValue().add(datePropValueType);
        docImageIndexPropType.setDocumentIndexPropertyValues(valuesType);

        filterDocImageIndexPropsType.getDocumentIndexProperty().add(docImageIndexPropType);

        final FilterDocumentClassesType filterDocumentClassesType = new FilterDocumentClassesType();
        filterDocumentClassesType.getDocumentClass().add(DOCUMENT_CLASS);

        final FilterIndexPropsType filterIndexPropsType = new FilterIndexPropsType();
        filterIndexPropsType.setDocumentClasses(filterDocumentClassesType);
        filterIndexPropsType.setDocumentIndexProperties(filterDocImageIndexPropsType);

        return filterIndexPropsType;
    }

    private ImageRequestContextType setContext(String gcmId) {
        // Setting Context
        final ImageRequestContextType imageRequestContextType = new ImageRequestContextType();
        imageRequestContextType.setVersion("1_0");
        imageRequestContextType.setResponseVersion("1_0");
        imageRequestContextType.setSubmitter("NEXTGEN");
        imageRequestContextType.setRequester(gcmId);
        imageRequestContextType.setRequestingSystem("PANORAMA");
        imageRequestContextType.setTrackingID(UUID.randomUUID().toString());
        return imageRequestContextType;
    }

    private KeyIndexPropType setPolicyDetails(String propertyType, String propertyValue) {
        // Sets property name to sPolicyId or sNumber
        final InvertedIndexPropType invertedIndexPropType = new InvertedIndexPropType();
        invertedIndexPropType.setDocumentIndexPropertyName(propertyType);

        final DocImageIndexPropValuesType imageIndexPropValuesType = new DocImageIndexPropValuesType();
        imageIndexPropValuesType.getDocumentIndexStringPropertyValue().add(propertyValue);
        invertedIndexPropType.setDocumentIndexPropertyValues(imageIndexPropValuesType);

        final KeyIndexPropType keyIndexPropType = new KeyIndexPropType();
        keyIndexPropType.setInvertedKeyIndexProperty(invertedIndexPropType);

        return keyIndexPropType;
    }

}
