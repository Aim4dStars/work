package com.bt.nextgen.service.cmis.annotation;

import com.bt.nextgen.service.cmis.converter.ConverterMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to map database to object property mapping
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    String name() default "";

    ConverterMapper converter() default ConverterMapper.CmisPropertyString;

    boolean updatable() default true;

}
