package com.bt.nextgen.core.util;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.ConfigurationContextProvider;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by L070815 on 20/05/2015.
 */
public class ConfigurationTest extends BaseSecureIntegrationTest{

    @Autowired
    ConfigurationContextProvider configurationContextProvider;

    @Test
    public void testGetConfig() throws Exception {

        Configuration config = configurationContextProvider.getApplicationContext().getBean (Configuration.class);
        String res = config.getString("adviser.partial.url");
        assertThat(res, is("adviser"));
        res = config.getString("PAST_TRANSACTIONS.FROM_DATE.PARAM_NAME");
        assertThat(res,is("trx_date_from"));


    }
}