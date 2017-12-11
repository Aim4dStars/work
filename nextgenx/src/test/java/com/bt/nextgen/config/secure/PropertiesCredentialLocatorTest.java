package com.bt.nextgen.config.secure;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class PropertiesCredentialLocatorTest {

    @Test
    public void testLocateByName() throws Exception {

        PropertiesCredentialLocator locator = new PropertiesCredentialLocator();
        assertThat(locator.locateByName("jms"), CoreMatchers.notNullValue());
    }
}