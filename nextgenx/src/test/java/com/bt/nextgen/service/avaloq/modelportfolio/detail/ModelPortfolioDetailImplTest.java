package com.bt.nextgen.service.avaloq.modelportfolio.detail;

import com.bt.nextgen.core.conversion.BigIntegerConverter;
import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.client.BrokerKeyConverter;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.detail.ConstructionType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioStatus;
import com.bt.nextgen.service.integration.modelportfolio.detail.TargetAllocation;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;
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
import java.math.BigDecimal;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class ModelPortfolioDetailImplTest {

    @InjectMocks
    DefaultResponseExtractor<ModelPortfolioDetailImpl> defaultResponseExtractor;

    @Mock
    ParsingContext parsingContext;

    @Mock
    ApplicationContext applicationContext;

    @Mock
    CodeConverter codeConverter;

    DateTimeTypeConverter dateTimeTypeConverter = new DateTimeTypeConverter();

    BigIntegerConverter bigIntegerConverter = new BigIntegerConverter();

    BrokerKeyConverter brokerKeyConverter = new BrokerKeyConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(CodeConverter.class)).thenReturn(codeConverter);
        Mockito.when(applicationContext.getBean(DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);
        Mockito.when(applicationContext.getBean(BigIntegerConverter.class)).thenReturn(bigIntegerConverter);
        Mockito.when(applicationContext.getBean(BrokerKeyConverter.class)).thenReturn(brokerKeyConverter);

        Mockito.when(codeConverter.convert("9510", "IPS_STATUS")).thenReturn("new");
        Mockito.when(codeConverter.convert("8061", "IPS_MP_TYPE")).thenReturn("mp_single");
        Mockito.when(codeConverter.convert("9523", "CONSTRUCTION_TYPE")).thenReturn("fixed_flo");
        Mockito.when(codeConverter.convert("9602", "IPS_INVESTMENT_STYLE")).thenReturn("bald");
        Mockito.when(codeConverter.convert("9652", "IPS_ASSET_CLASS")).thenReturn("btfg$fi_au");
        Mockito.when(codeConverter.convert("8071", "MODEL_STRUCT")).thenReturn("simple");
        Mockito.when(codeConverter.convert("60663", "ASSET_CLASS")).thenReturn("fi_au");
        Mockito.when(codeConverter.convert("60665", "ASSET_CLASS")).thenReturn("realest_au");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testXPathMappings() throws Exception {

        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/modelportfolio/ModelPortfolioCreateResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(ModelPortfolioDetailImpl.class);
        ModelPortfolioDetailImpl response = defaultResponseExtractor.extractData(content);

        Assert.assertNotNull(response);

        Assert.assertEquals("12345", response.getId());
        Assert.assertEquals("bald", response.getInvestmentStyle());
        Assert.assertEquals(BigDecimal.valueOf(25000), response.getMinimumInvestment());
        Assert.assertEquals("btfg$fi_au", response.getModelAssetClass());
        Assert.assertEquals(ConstructionType.FIXED_AND_FLOATING, response.getModelConstruction());
        Assert.assertEquals("simple", response.getModelStructure());
        // TODO: Get a test value from the new static code table mp_type
        // Assert.assertEquals("mp_single", response.getModelType());
        Assert.assertEquals("Model Portfolio Test", response.getName());
        Assert.assertEquals(new DateTime("2016-02-03"), response.getOpenDate());
        Assert.assertEquals(BigDecimal.valueOf(0.1), response.getPortfolioConstructionFee());
        Assert.assertEquals(ModelPortfolioStatus.NEW, response.getStatus());
        Assert.assertEquals("TEST0001TMP", response.getSymbol());
        Assert.assertEquals(BrokerKey.valueOf("99796"), response.getInvestmentManagerId());
        Assert.assertEquals("doot", response.getModelDescription());
        Assert.assertEquals(BigDecimal.ZERO, response.getMinimumTradeAmount());
        Assert.assertEquals(BigDecimal.ONE, response.getMinimumTradePercent());

        Assert.assertEquals(2, response.getTargetAllocations().size());

        TargetAllocation taa = response.getTargetAllocations().get(0);
        Assert.assertEquals("fi_au", taa.getAssetClass());
        Assert.assertEquals(BigDecimal.valueOf(80), taa.getMaximumWeight());
        Assert.assertEquals(BigDecimal.valueOf(50), taa.getMinimumWeight());
        Assert.assertEquals(BigDecimal.valueOf(40), taa.getNeutralPos());
        Assert.assertEquals("99773", taa.getIndexAssetId());

        TargetAllocation taa2 = response.getTargetAllocations().get(1);
        Assert.assertEquals("realest_au", taa2.getAssetClass());
        Assert.assertEquals(BigDecimal.valueOf(30), taa2.getMaximumWeight());
        Assert.assertEquals(BigDecimal.valueOf(10), taa2.getMinimumWeight());
        Assert.assertEquals(BigDecimal.valueOf(40), taa2.getNeutralPos());
        Assert.assertEquals("111636", taa2.getIndexAssetId());

        Assert.assertEquals(1, response.getWarnings().size());

        TransactionValidation val = response.getWarnings().get(0);
        Assert.assertEquals("Neutral position must sum up to 100%", val.getErrorMessage());
    }
}