package com.bt.nextgen.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class contains method for case conversion of string properties
 */
public class CaseConverterUtil {

    private static final Logger logger = LoggerFactory.getLogger(CaseConverterUtil.class);

    private enum Case {
        UPPER,
        LOWER,
        TITLE
    }

    /**
     * This method will convert all the fields including inherited fields of a class to uppercase.
     *
     * @param t          Object to be converted
     * @param <T>
     * @param <P>
     */
    public static final <T, P> void convertToUpperCase(T t) {
        if (t == null) {
            return;
        }
        convertCase(t, Case.UPPER, false, Collections.<String>emptyList());
    }

    /**
     * This method will convert all the fields including inherited fields of a class to uppercase and excludes properties defined.
     *
     * @param t            Object to be converted
     * @param excludeProps excluding property names
     * @param <T>
     * @param <P>
     */
    public static final <T, P> void convertToUpperCase(T t, String... excludeProps) {
        if (t == null || excludeProps == null) {
            return;
        }
        convertCase(t, Case.UPPER, false, Arrays.asList(excludeProps));
    }

    /**
     * This method will convert all the fields including inherited fields of a class to title case.
     *
     * @param t          Object to be converted
     * @param <T>
     * @param <P>
     */
    public static final <T, P> void convertToTitleCase(T t) {
        if (t == null) {
            return;
        }
        convertCase(t, Case.TITLE, false, Collections.<String>emptyList());
    }

    /**
     * This method will convert all the fields including inherited fields of a class to uppercase and excludes properties defined.
     *
     * @param t            Object to be converted
     * @param excludeProps excluding property names
     * @param <T>
     * @param <P>
     */
    public static final <T, P> void convertToTitleCase(T t, String... excludeProps) {
        if (t == null || excludeProps == null) {
            return;
        }
        convertCase(t, Case.TITLE, false, Arrays.asList(excludeProps));
    }

    /**
     * This method will convert all the fields including inherited fields to lowercase.
     *
     * @param t          Object to be converted
     * @param <T>
     * @param <P>
     */
    public static final <T, P> void convertToLowerCase(final T t) {
        if (t == null) {
            return;
        }
        convertCase(t, Case.LOWER, false, Collections.<String>emptyList());
    }

    /**
     * This method will convert all the fields including inherited fields to lowercase and excludes the properties defined.
     *
     * @param t            Object to be converted
     * @param excludeProps
     * @param <T>
     * @param <P>
     */
    public static final <T, P> void convertToLowerCase(T t, String... excludeProps) {
        if (t == null || excludeProps == null) {
            return;
        }
        convertCase(t, Case.LOWER, false, Arrays.asList(excludeProps));
    }

    /**
     * This method will not convert Title Case Strings, It will only convert stings in lowercase.
     * For Ex It will not convert following string "This is a Title String" but "this is not a title string"
     * Also it will only permit to have space in the string literal but not special characters.
     *
     * @param t          Object to be converted
     * @param <T>
     */
    public static final <T, P> void safeConvertToUpperCase(T t) {
        if (t == null) {
            return;
        }
        convertCase(t, Case.UPPER, true, Collections.<String>emptyList());
    }

    /**
     * This method will not convert Title Case Strings, It will only convert stings in lowercase only.
     * For Ex It will not following string "This is a Title String" but this "THIS IS NOT A TITLE STRING"
     * Also it will only permit to have space in the string literal but not special characters.
     *
     * @param t          Object to be converted
     * @param <T>
     * @param <P>
     */
    public static final <T, P> void safeConvertToLowerCase(T t) {
        if (t == null) {
            return;
        }
        convertCase(t, Case.LOWER, true, Collections.<String>emptyList());
    }

    @SuppressWarnings("squid:MethodCyclomaticComplexity")
    private static <T, P> void convertCase(final T t, final Case toCase, final boolean safeConversion, List<String> excludeProps) {

        try {
            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(t.getClass()).getPropertyDescriptors()) {
                Method method = propertyDescriptor.getReadMethod();
                if (null != method && null != excludeProps && !excludeProps.contains(propertyDescriptor.getName()) &&
                        !Modifier.isFinal(method.getModifiers())) {
                    Class<?> returnType = method.getReturnType();
                    Object returnValue = method.invoke(t);
                    if (String.class.isAssignableFrom(returnType)) {
                        setToCase(t, propertyDescriptor, toCase, safeConversion);
                    } else if (null != returnType.getPackage() && (returnType.getPackage().getName().startsWith("com.bt.nextgen") || returnType.getPackage().getName().startsWith("au.com.westpac"))) {
                        if (null != method.invoke(t)) {
                            convertCase(method.invoke(t), toCase, safeConversion, excludeProps);
                        }
                    } else if (returnValue instanceof Collection) {
                        for (Object o : ((Collection) returnValue).toArray()) {
                            convertCase(o, toCase, safeConversion, excludeProps);
                        }
                    } else {
                        logger.debug("property return type is neither collection nor instance of a BaseDto");
                    }
                }
            }
        } catch (IntrospectionException e) {
            logger.error("Exception occurred while case conversion", e);
        } catch (InvocationTargetException e) {
            logger.error("Exception occurred while case conversion", e);
        } catch (IllegalAccessException e) {
            logger.error("Exception occurred while case conversion", e);
        }
    }

    @SuppressWarnings("squid:MethodCyclomaticComplexity")
    private static <T> void setToCase(T t, PropertyDescriptor propertyDescriptor, Case toCase, boolean safeConversion)
            throws IllegalAccessException, InvocationTargetException {

        Method setterMethod = propertyDescriptor.getWriteMethod();
        String returnValue = (String) propertyDescriptor.getReadMethod().invoke(t);
        if (null != setterMethod && !StringUtils.isEmpty(returnValue)) {
            if (Case.UPPER.equals(toCase) && ((safeConversion && StringUtils.isAllLowerCase(returnValue.replaceAll(" ", ""))) || !safeConversion)) {
                setterMethod.invoke(t, StringUtils.upperCase(returnValue));
            } else if (Case.LOWER.equals(toCase) && ((safeConversion && StringUtils.isAllUpperCase(returnValue.replaceAll(" ", ""))) || !safeConversion)) {
                setterMethod.invoke(t, StringUtils.lowerCase(returnValue));
            } else if (Case.TITLE.equals(toCase)) {
                setterMethod.invoke(t, WordUtils.capitalizeFully(returnValue));
            }
        }
    }
}
