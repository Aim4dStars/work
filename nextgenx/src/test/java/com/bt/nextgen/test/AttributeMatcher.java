package com.bt.nextgen.test;

import org.apache.commons.beanutils.PropertyUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;


public class AttributeMatcher<T> extends BaseMatcher<T> {

    private String attributeName;
    private Object attributeValue;

    public AttributeMatcher(String attributeName, Object attributeValue) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    @Override
    public boolean matches(Object item) {
        try {
            if (attributeValue == null) {
                return PropertyUtils.getProperty(item, attributeName) == null;
            } else {
                return attributeValue.equals(PropertyUtils.getProperty(item, attributeName));
            }
        } catch (Exception e) {
            return false;
         }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Object with attribute "+attributeName+"="+attributeValue);
    }

}
