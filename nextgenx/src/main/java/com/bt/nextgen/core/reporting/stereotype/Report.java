package com.bt.nextgen.core.reporting.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * Indicates that an annotated class is a "report". The annotation itself
 * is annotated with @Component and as such is picked up by spring's
 * scan for instantiated beans. These can be be autowired or obtained
 * directly from the spring context. 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Report
{

	/**
	 * Bean name for class instance when autodetected
	 * @return the suggested component name, if any
	 */
	String value();

	/**
	 * The filename to use in the client browser or file entry within a zip file
	 * @return the name of the output file
	 */
	String filename() default "";
}
