package com.bt.nextgen.api.client.service;

import com.bt.nextgen.service.cmis.CmisAbstractTest;
import com.bt.nextgen.service.cmis.annotation.ColumnAnnotationProcessor;
import com.bt.nextgen.service.cmis.converter.CmisPropertyIDConverter;
import com.bt.nextgen.service.cmis.converter.Converter;
import org.junit.Assert;
import org.junit.Test;
import org.oasis_open.docs.ns.cmis.core._200908.CmisProperty;

/**
 * Created by L062329 on 20/07/2015.
 */
public class ColumnAnnotationProcessorTest extends CmisAbstractTest {

    @Test
    public void testGetInstance() throws Exception {
        ColumnAnnotationProcessor processor = ColumnAnnotationProcessor.getInstance();
        Assert.assertTrue(processor == ColumnAnnotationProcessor.getInstance());
    }

    @Test
    public void testGetConverter() {
        CmisProperty property = map.get("cmis:objectId");
        ColumnAnnotationProcessor processor = ColumnAnnotationProcessor.getInstance();
        Converter converter = processor.getConverter(property);
        Assert.assertTrue(converter instanceof CmisPropertyIDConverter);
    }

    @Test //(expected = IllegalArgumentException.class)
    public void testGetBeanProperty() {
        ColumnAnnotationProcessor processor = ColumnAnnotationProcessor.getInstance();
        CmisProperty property = map.get("cmis:objectId");
        String propertyName = processor.getBeanProperty(property);
        Assert.assertEquals("documentKey", propertyName);
        String noSuchProperty = processor.getBeanProperty("nosuch property");
        Assert.assertEquals(null, noSuchProperty);

    }

}