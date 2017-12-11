package com.bt.nextgen.core.reporting.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation specifies that the evaluation is to be sent to the report generation
 * engine for inclusion in templating process.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReportImage
{

	/**
	 * Key to use for population in the bean map.
	 * @return the bean key
	 */
	String value() default "";

}
