package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import org.junit.After;
import org.junit.Assert;
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

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class RebalanceSubmitResponseImplTest {

    @InjectMocks
    private DefaultResponseExtractor<ModelPortfolioSubmitResponseImpl> defaultResponseExtractor;
    @Mock
    private ParsingContext parsingContext;
    @Mock
    private ApplicationContext applicationContext;


    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testXPathMappings() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/RebalanceSubmit_UT.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(ModelPortfolioSubmitResponseImpl.class);
        ModelPortfolioSubmitResponseImpl response = defaultResponseExtractor.extractData(content);
        Assert.assertNull(response.getErrors());
    }

    @Test
    public void testXPathMappings_whenValidationErrors_thenErrorsIsPopulated() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/RebalanceSubmitValidation_UT.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(ModelPortfolioSubmitResponseImpl.class);
        ModelPortfolioSubmitResponseImpl response = defaultResponseExtractor.extractData(content);
        Assert.assertNotNull(response.getErrors());
        Assert.assertEquals(1, response.getErrors().size());
        Assert.assertEquals("app", response.getErrors().get(0).getErrorType());
    }

    @Test
    public void testXPathMappings_whenFatalErrors_thenErrorsIsPopulated() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/RebalanceSubmitFatal_UT.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(ModelPortfolioSubmitResponseImpl.class);
        ModelPortfolioSubmitResponseImpl response = defaultResponseExtractor.extractData(content);
        Assert.assertNotNull(response.getErrors());
        Assert.assertEquals(2, response.getErrors().size());
        Assert.assertEquals("fa", response.getErrors().get(0).getErrorType());
    }

}