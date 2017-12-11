package com.bt.nextgen.core.webservice.provider;

import com.bt.nextgen.config.TestConfig;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Barker (m035652)
 * Date: 14/08/13
 * Time: 9:03 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { TestConfig.class })
public class UddiDestinationProviderExternalTest
{
    @Autowired
    WebServiceProvider provider;



}
