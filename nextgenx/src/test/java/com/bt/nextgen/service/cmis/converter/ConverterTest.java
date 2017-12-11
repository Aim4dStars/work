package com.bt.nextgen.service.cmis.converter;

import com.bt.nextgen.service.cmis.CmisAbstractTest;
import com.bt.nextgen.service.integration.financialdocument.DocumentKey;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.oasis_open.docs.ns.cmis.core._200908.CmisProperty;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyDateTime;

import java.math.BigInteger;

/**
 * Test case to cover Unit testing of Converter classes for CMIS data type
 */
public class ConverterTest extends CmisAbstractTest {


    @Test
    public void testCmisPropertyIDConverter() {
        CmisProperty property = map.get("cmis:objectId");
        Converter converter = new CmisPropertyIDConverter();
        Object object = converter.convert(property);
        Assert.assertTrue(object instanceof DocumentKey);
        Assert.assertEquals("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E0", ((DocumentKey) object).getId());

    }

    @Test
    public void testCmisPropertyStringConverter() {
        CmisProperty property = map.get("PanoramaIPRelationshipID");
        Converter converter = new CmisPropertyStringConverter();
        Assert.assertEquals("accountNumber", converter.convert(property));

    }

    @Test
    public void testCmisPropertyDateTimeConverter() {
        CmisProperty property = map.get("PanoramaIPStartDate");
        Converter converter = new CmisPropertyDateTimeConverter();
        DateTime date = new DateTime(((CmisPropertyDateTime) property).getValue().get(0).toGregorianCalendar().getTime());
        Assert.assertEquals(date, converter.convert(property));
    }

    @Test
    public void testCmisPropertyIntegerConverter() {
        CmisProperty property = map.get("cmis:contentStreamLength");
        Converter converter = new CmisPropertyIntegerConverter();
        Assert.assertEquals(BigInteger.valueOf(134257), converter.convert(property));
    }

    @Test
    public void testCmisPropertyBooleanConverter() {
        CmisProperty property = map.get("Audit");
        Converter converter = new CmisPropertyBooleanConverter();
        Assert.assertEquals(true, converter.convert(property));
    }

    @Test
    public void testConverterMapper() {
        //TODO: implement test case for ConverterMapper
    }
}