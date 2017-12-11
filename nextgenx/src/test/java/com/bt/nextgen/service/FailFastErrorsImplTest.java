package com.bt.nextgen.service;


import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;

import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.bt.nextgen.clients.web.model.ClientModel;
import com.bt.nextgen.config.TestConfig;
import com.bt.nextgen.login.web.model.AccountStatusModel;


public class FailFastErrorsImplTest {

    private static final Logger logger = LoggerFactory.getLogger(FailFastErrorsImplTest.class);

    @Test
    public void testEmptyErrorCollectionToStringThrowsException() throws Exception
    {
        ServiceErrors se = new FailFastErrorsImpl();
        String result = null;
        boolean threwErrorForEmptyErrorCollection = false;
        try {
            result = se.toString();
        } catch (java.util.NoSuchElementException e) {
            threwErrorForEmptyErrorCollection = true;
        }
        assertThat(threwErrorForEmptyErrorCollection, is(false));
    }

}


