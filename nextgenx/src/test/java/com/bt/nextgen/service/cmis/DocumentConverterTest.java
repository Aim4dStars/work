package com.bt.nextgen.service.cmis;

import com.bt.nextgen.service.cmis.annotation.ColumnAnnotationProcessor;
import com.bt.nextgen.service.cmis.constants.DocumentCategories;
import com.bt.nextgen.service.integration.financialdocument.Document;
import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertiesType;
import org.oasis_open.docs.ns.cmis.core._200908.CmisProperty;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyBoolean;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyDateTime;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyId;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyInteger;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyString;
import org.oasis_open.docs.ns.cmis.messaging._200908.CmisContentStreamType;
import org.oasis_open.docs.ns.cmis.messaging._200908.CreateDocument;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

/**
 * Created by L062329 on 20/07/2015.
 */
public class DocumentConverterTest extends CmisAbstractTest {

    private DocumentConverter converter = new DocumentConverter();

    @Test
    public void createDocumentPropertiesTest() {
        CmisDocumentImpl document = new CmisDocumentImpl();
        document.setDocumentType("STM");
        document.setDocumentTitleCode("SPCENT");
        CmisPropertiesType property = converter.createDocumentProperties(document);
        Assert.assertNotNull(property);
    }

    @Test
    public void testConvert() throws Exception {
        Collection<Document> documentList = converter.convert(response);
        Assert.assertEquals(1, documentList.size());
        Document document = documentList.iterator().next();

        Assert.assertEquals("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E0", document.getDocumentKey().getId());
        Assert.assertEquals("STMANN", document.getDocumentTitleCode());
        Assert.assertEquals("accountNumber", document.getRelationshipId());
        Assert.assertEquals(BigInteger.valueOf(134257), document.getSize());
        //Test for date
        //Assert.assertEquals(134257, document.getSize());
    }

    @Test
    public void testConvertToCmisProperty() {
        //Test String property type conversion
        CmisProperty property = converter.convertToCmisProperty("documentType", "Test");
        Assert.assertTrue(property instanceof CmisPropertyString);
        Assert.assertEquals("PanoramaIPDocumentCategory", property.getPropertyDefinitionId());
        Assert.assertTrue(((CmisPropertyString) property).getValue().contains("Test"));


        //Test Boolean property type conversion
        property = converter.convertToCmisProperty("audit", true);
        Assert.assertTrue(property instanceof CmisPropertyBoolean);
        Assert.assertEquals("PanoramaIPAudit", property.getPropertyDefinitionId());
        Assert.assertTrue(((CmisPropertyBoolean) property).getValue().contains(true));

        //Test DateTime property type conversion
        DateTime dateTime = new DateTime();
        XMLGregorianCalendar dealCloseDate = null;

        try {
            dealCloseDate = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(dateTime.toGregorianCalendar());
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        property = converter.convertToCmisProperty("uploadedDate", dateTime);
        Assert.assertTrue(property instanceof CmisPropertyDateTime);
        Assert.assertEquals("DateCheckedIn", property.getPropertyDefinitionId());
        Assert.assertTrue(((CmisPropertyDateTime) property).getValue().contains(dealCloseDate));

        //Test Integer property type conversion
        final BigInteger intValue = BigInteger.valueOf(12);
        property = converter.convertToCmisProperty("size",intValue);
        Assert.assertTrue(property instanceof CmisPropertyInteger);
        Assert.assertEquals("cmis:contentStreamLength", property.getPropertyDefinitionId());
        Assert.assertTrue(((CmisPropertyInteger) property).getValue().contains(intValue));
    }

    @Test
    public void testGetUpdatedProperties() {
        CmisDocumentImpl document = new CmisDocumentImpl();
        document.setDocumentName("Test");
        document.setAudit(true);
        document.setUploadedDate(new DateTime());
        document.setSize(BigInteger.valueOf(1212));
        CmisPropertiesType cmisPropertiesType = converter.getUpdatedProperties(document);
        List<CmisProperty> properties = cmisPropertiesType.getProperty();
        Assert.assertEquals(3, properties.size());
    }

    @Test
    public void testGetContentStreamType() {
        CmisDocumentImpl cmisDocument = new CmisDocumentImpl();
        cmisDocument.setMimeType("application/octet-stream");
        cmisDocument.setSize(BigInteger.valueOf(12345));
        cmisDocument.setDocumentName("test.pdf");
        CmisContentStreamType cmisContentStreamType = converter.getContentStreamType(cmisDocument);
        Assert.assertNotNull(cmisContentStreamType);
        Assert.assertEquals(cmisDocument.getMimeType(), cmisContentStreamType.getMimeType());
        Assert.assertEquals(cmisDocument.getSize(), cmisContentStreamType.getLength());
        Assert.assertEquals(cmisDocument.getDocumentName(), cmisContentStreamType.getFilename());
    }

    @Test
    public void setBeanProperty() {
        CmisDocumentImpl cmisDocument = new CmisDocumentImpl();
        converter.setBeanProperty(cmisDocument, "documentType", null);
        Assert.assertNull(cmisDocument.getDocumentTitleCode());
    }


    @Test
    public void testGetCreateDocumentObject() {
        CmisDocumentImpl document = new CmisDocumentImpl();
        document.setRelationshipId("120003611");
        document.setFileName("AssetTest.docx");
        document.setUpdatedByID("201602159");
        document.setPanoramaipRelationshipType("ACCT");
        document.setPanoramaipBusinessArea("PANORAMA");
        document.setFileExtension("DOCX");
        String folderId = "idf_7A83CE13-5E4E-4633-823E-FD229DE93A6E";
        document.setDocumentName("AssetTest.docx");
        document.setAudit(true);
        document.setUploadedDate(new DateTime());
        document.setSize(BigInteger.valueOf(1212));
        document.setFinancialYear("2015/2016");
        document.setDocumentType("INV");
        document.setDocumentSubType("Asset Transfers");
        CreateDocument createDocument = converter.getCreateDocumentObject(document, folderId);
        Assert.assertNotNull(createDocument);
        CmisPropertiesType cmsProperties = createDocument.getProperties();
        List<CmisProperty> properties = cmsProperties.getProperty();
        for (CmisProperty property : properties) {
            if (property.getPropertyDefinitionId().equalsIgnoreCase("PanoramaIPDocumentSubCategory1")) {
                Assert.assertTrue(((CmisPropertyString) property).getValue().contains("Asset Transfers"));
            }
            if (property.getPropertyDefinitionId().equalsIgnoreCase("PanoramaIPRelationshipID")) {
                Assert.assertTrue(((CmisPropertyString) property).getValue().contains("120003611"));
            }
            if (property.getPropertyDefinitionId().equalsIgnoreCase("PanoramaIPUpdatedByID")) {
                Assert.assertTrue(((CmisPropertyString) property).getValue().contains("201602159"));
            }
            if (property.getPropertyDefinitionId().equalsIgnoreCase("PanoramaIPFinancialYear")) {
                Assert.assertTrue(((CmisPropertyString) property).getValue().contains("2015/2016"));
            }
            if (property.getPropertyDefinitionId().equalsIgnoreCase("PanoramaIPDocumentFileExtension")) {
                Assert.assertTrue(((CmisPropertyString) property).getValue().contains("DOCX"));
            }
            if (property.getPropertyDefinitionId().equalsIgnoreCase("PanoramaIPDocumentName")) {
                Assert.assertTrue(((CmisPropertyString) property).getValue().contains("AssetTest.docx"));
            }
            if (property.getPropertyDefinitionId().equalsIgnoreCase("PanoramaIPDocumentCategory")) {
                Assert.assertTrue(((CmisPropertyString) property).getValue().contains("INV"));
            }
            if (property.getPropertyDefinitionId().equalsIgnoreCase("PanoramaIPRelationshipType")) {
                Assert.assertTrue(((CmisPropertyString) property).getValue().contains("ACCT"));
            }
            if (property.getPropertyDefinitionId().equalsIgnoreCase("PanoramaIPBusinessArea")) {
                Assert.assertTrue(((CmisPropertyString) property).getValue().contains("PANORAMA"));
            }
        }
    }

}