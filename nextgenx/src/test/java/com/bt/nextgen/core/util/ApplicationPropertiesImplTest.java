package com.bt.nextgen.core.util;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;


/**
 * Tests {@link ApplicationPropertiesImpl}.
 * 
 * @author Albert Hirawan
 */
public class ApplicationPropertiesImplTest extends BaseSecureIntegrationTest {
	private static final String PROPERTY_STRING_EXISTS_KEY = "database.liquibase.source";
	private static final String PROPERTY_STRING_EXISTS_VALUE = "persistence/db.changelog-master.xml";

	private static final String PROPERTY_INT_EXISTS_KEY = "jdbc.maxActive";
	private static final Integer PROPERTY_INT_EXISTS_VALUE = 20;

	private static final String PROPERTY_NOT_EXISTS_KEY = "no-such-property";
	
	
	@Autowired
	private ApplicationProperties properties;
	
	
	@Test
	public void getString() {
		String property;
		
		property = properties.get(PROPERTY_NOT_EXISTS_KEY);		
		assertThat("property does not exists", property, nullValue());
		
		property = properties.get(PROPERTY_STRING_EXISTS_KEY);		
		assertThat("property exists", property, notNullValue());
		assertThat("property value", property, equalTo(PROPERTY_STRING_EXISTS_VALUE));
	}
	
	
	@Test
	public void getStringWithDefaultValue() {
		final String defaultValue = "My default value";
		String property;
		
		property = properties.get(PROPERTY_NOT_EXISTS_KEY, defaultValue);		
		assertThat("property exists", property, notNullValue());
		assertThat("property value", property, equalTo(defaultValue));
		
		property = properties.get(PROPERTY_STRING_EXISTS_KEY, defaultValue);		
		assertThat("property exists", property, notNullValue());
		assertThat("property value", property, equalTo(PROPERTY_STRING_EXISTS_VALUE));
		assertThat("property value", property, not(equalTo(defaultValue)));
	}
	
	
	@Test
	public void getInteger() {
		Integer property;
		
		property = properties.getInteger(PROPERTY_NOT_EXISTS_KEY);
		assertThat("property does not exists", property, nullValue());
		
		property = properties.getInteger(PROPERTY_INT_EXISTS_KEY);		
		assertThat("property exists", property, notNullValue());
		assertThat("property value", property, equalTo(PROPERTY_INT_EXISTS_VALUE));
	}
	
	
	@Test(expected = NumberFormatException.class)
	public void getIntegerForStringProperty() {
		properties.getInteger(PROPERTY_STRING_EXISTS_KEY);
	}
	
	
	@Test
	public void getIntegerWithDefaultValue() {
		final Integer defaultValue = 12345;
		Integer property;
		
		property = properties.getInteger(PROPERTY_NOT_EXISTS_KEY, defaultValue);		
		assertThat("property exists", property, notNullValue());
		assertThat("property value", property, equalTo(defaultValue));
		
		property = properties.getInteger(PROPERTY_INT_EXISTS_KEY, defaultValue);		
		assertThat("property exists", property, notNullValue());
		assertThat("property value", property, equalTo(PROPERTY_INT_EXISTS_VALUE));
		assertThat("property value", property, not(equalTo(defaultValue)));
	}
}
