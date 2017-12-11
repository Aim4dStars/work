package com.bt.nextgen.bgp;

import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.bgp.BackGroundProcessServiceImpl;
import com.bt.nextgen.service.integration.bgp.BackGroundProcess;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class BackGroundProcessServiceImplTest {

    @InjectMocks
    DefaultResponseExtractor<BackGroundProcessServiceImpl> defaultResponseExtractor;

    @Mock
    ParsingContext parsingContext;

    @Mock
    ApplicationContext applicationContext;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
    }

    @Test
    public void testLoadClientStatements() throws Exception
    {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/bgp/BGP_Response_UT.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(BackGroundProcessServiceImpl.class);
        BackGroundProcessServiceImpl response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);

        List<BackGroundProcess> bgps = response.getBackGroundProcesses();
        assertNotNull(bgps);

        for (BackGroundProcess bgp : bgps)
        {
            assertNotNull(bgp.getBGPName());
            assertNotNull(bgp.getBGPId());
            assertNotNull(bgp.getBGPInstance());
            //assertNotNull(bgp.getSID());
            //assertNotNull(bgp.getCurrentTime());
            assertNotNull(bgp.isBGPValid());
        }
    }
}
