package com.bt.nextgen.core.reporting.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation helps in initialising the service data . The annotated method
 * will be called first while invoking the methods from the reporting controller
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReportInitializer {
    /**
     * Key to use for population in the bean map.
     * 
     * @return the bean key
     */
    String value() default "initializedData";
}
