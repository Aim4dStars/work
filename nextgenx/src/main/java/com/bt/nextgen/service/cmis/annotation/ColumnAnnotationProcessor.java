package com.bt.nextgen.service.cmis.annotation;

import com.bt.nextgen.service.cmis.CmisDocumentImpl;
import com.bt.nextgen.service.cmis.converter.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.oasis_open.docs.ns.cmis.core._200908.CmisProperty;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Annotation processor for @See Column annotation. Currently it is used only with CmisDocumentImpl class, if Column
 * annotation needs to be used than it should behave as factory(Changes are required to support that)
 */
public final class ColumnAnnotationProcessor {

    private Map<String, Column> banPropertyMap;

    private Map<String, String> cmisColumnMap;

    private static ColumnAnnotationProcessor processor;

    private ColumnAnnotationProcessor() {
        setAnnotations();
    }

    public static ColumnAnnotationProcessor getInstance() {
        if (processor == null) {
            processor = new ColumnAnnotationProcessor();
        }
        return processor;
    }

    private void setAnnotations() {
        this.banPropertyMap = new HashMap<>();
        this.cmisColumnMap = new HashMap<>();
        Field[] fields = CmisDocumentImpl.class.getDeclaredFields();
        for (Field field : fields) {
            String propertyName = field.getName();
            Column annotation = field.getAnnotation(Column.class);
            if (annotation != null) {
                banPropertyMap.put(propertyName, annotation);
                cmisColumnMap.put(annotation.name().toUpperCase(), propertyName);
            }
        }
    }

    public Converter getConverter(CmisProperty cmisProperty) {
        Column annotation = banPropertyMap.get(getBeanProperty(cmisProperty));
        return annotation.converter().getConverter();
    }

    public Converter getConverter(String beanProperty){
        Column annotation = banPropertyMap.get(beanProperty);
        return annotation.converter().getConverter();
    }

    public String getBeanProperty(String columnName) {
        return cmisColumnMap.get(columnName.toUpperCase());
    }

    public String getBeanProperty(CmisProperty cmisProperty) {
        return getBeanProperty(cmisProperty.getPropertyDefinitionId());
    }

    public String getColumn(String beanProperty){
        Column annotation = banPropertyMap.get(beanProperty);
        return annotation.name();
    }

    public Collection<Column> getColumns(){
        return banPropertyMap.values();
    }

    public Collection<String> getBeanProperties() {
        return this.banPropertyMap.keySet();
    }

    public boolean isUpdatable(String beanProperty) {
        Column annotation = banPropertyMap.get(beanProperty);
        return annotation.updatable();
    }
}
