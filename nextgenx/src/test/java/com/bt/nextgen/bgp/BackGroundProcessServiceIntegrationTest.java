package com.bt.nextgen.bgp;

import com.bt.nextgen.config.IntegrationTestExecutionListener;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.config.TestConfig;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.bgp.BackGroundProcess;
import com.bt.nextgen.service.integration.bgp.BackGroundProcessIntegrationService;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { TestConfig.class })
@TestExecutionListeners(listeners = { IntegrationTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        ServletTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class })
@SecureTestContext(username="adviser", jobRole = "ADVISER" , customerId = "297129090", jobId="",  profileId = "")
public class BackGroundProcessServiceIntegrationTest  {

    @Autowired
    BackGroundProcessIntegrationService backGroundProcessService;

    @Autowired
    ParsingContext context;

    @Test
    @SecureTestContext
    public void testBackGroundProcessTime() throws Exception
    {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        DateTime currentTime = backGroundProcessService.getCurrentTime(serviceErrors);
        assertNotNull(currentTime);
    }

    @Test
    @SecureTestContext
    public void testBackGroundProcess() throws Exception
    {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        List<BackGroundProcess> backGroundProcesses = backGroundProcessService.getBackGroundProcesses(serviceErrors);
        assertNotNull(backGroundProcesses);

        assert(backGroundProcesses.size() > 0);

        BackGroundProcess backGroundProcess = backGroundProcesses.get(0);
        assertNotNull(backGroundProcess.getBGPName());
    }
}
