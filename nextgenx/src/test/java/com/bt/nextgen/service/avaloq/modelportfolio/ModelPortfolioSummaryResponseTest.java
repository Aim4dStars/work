package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.core.conversion.BooleanConverter;
import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.ips.IpsKeyConverter;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;
import com.bt.nextgen.service.integration.modelportfolio.common.IpsStatus;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelRebalanceStatus;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
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

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class ModelPortfolioSummaryResponseTest
{
    @InjectMocks
    DefaultResponseExtractor<ModelPortfolioSummaryResponse> defaultResponseExtractor;

    @Mock
    ParsingContext parsingContext;

    @Mock
    ApplicationContext applicationContext;

    @Mock
    CodeConverter codeConverter;

    @Mock
    IpsAssetClassConverter ipsAssetClassConverter;
    @Mock
    IpsInvestmentStyleConverter ipsInvestmentStyleConverter;

    BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();
    
    DateTimeTypeConverter dateTimeTypeConverter = new DateTimeTypeConverter();

    BooleanConverter booleanConverter = new BooleanConverter();
    
    IpsKeyConverter ipsKeyConverter = new IpsKeyConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(BigDecimalConverter.class)).thenReturn(bigDecimalConverter);
        Mockito.when(applicationContext.getBean(CodeConverter.class)).thenReturn(codeConverter);
        Mockito.when(applicationContext.getBean(DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);
        Mockito.when(applicationContext.getBean(BooleanConverter.class)).thenReturn(booleanConverter);
        Mockito.when(applicationContext.getBean(IpsKeyConverter.class)).thenReturn(ipsKeyConverter);

        Mockito.when(applicationContext.getBean(IpsAssetClassConverter.class)).thenReturn(ipsAssetClassConverter);
        Mockito.when(applicationContext.getBean(IpsInvestmentStyleConverter.class)).thenReturn(ipsInvestmentStyleConverter);

        when(ipsInvestmentStyleConverter.convert("9600")).thenReturn("Active");
        when(ipsAssetClassConverter.convert("9650")).thenReturn("Australian Shares");

        when(codeConverter.convert("9650", "IPS_ASSET_CLASS")).thenReturn("btfg$eq_au");
        when(codeConverter.convert("20611", "IPS_INVESTMENT_STYLE")).thenReturn("I");

        when(codeConverter.convert("9511", "IPS_STATUS")).thenReturn(IpsStatus.OPEN.toString());
        when(codeConverter.convert("9512", "IPS_STATUS")).thenReturn(IpsStatus.PENDING.toString());

        when(codeConverter.convert("2", "IPS_REBAL_STATUS")).thenReturn(ModelRebalanceStatus.COMPLETE.toString());

        when(codeConverter.convert("8121", "IPS_MODEL_TYPE")).thenReturn(ModelType.INVESTMENT.getIntlId());
    }

    @Test
    public void testXPathMappings_whenSummaryServiceResponse() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/modelportfolio/ModelPortfolioSummaryResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(ModelPortfolioSummaryResponse.class);
        ModelPortfolioSummaryResponse response = defaultResponseExtractor.extractData(content);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getSummary());

        // Data size. Null ipsId data should have been ignored.
        Assert.assertEquals(17, response.getSummary().size());

        // Retrieve first summary: from ips_list
        ModelPortfolioSummary summary = response.getSummary().get(0);
        Assert.assertEquals("9922518", summary.getIpsOrderId());
        Assert.assertEquals("Active", summary.getInvestmentStyle());
        Assert.assertEquals("Australian Shares", summary.getAssetClass());
        Assert.assertEquals("36704.98", summary.getFum().toString());
        Assert.assertEquals(Boolean.FALSE, summary.getHasScanTrigger());
        Assert.assertEquals(Boolean.FALSE, summary.getHasMandatoryCorporateActions());
        Assert.assertEquals("CANEW", summary.getModelCode());
        Assert.assertEquals("1266357", summary.getModelKey().getId());
        Assert.assertEquals("CANEWTMP", summary.getModelName());
        Assert.assertEquals("9906941", summary.getModelOrderId());
        Assert.assertEquals(Integer.valueOf(1), summary.getNumAccounts());
        Assert.assertEquals(ModelType.INVESTMENT, summary.getAccountType());
        Assert.assertEquals(ModelRebalanceStatus.COMPLETE, summary.getRebalanceStatus());
        Assert.assertEquals(IpsStatus.OPEN, summary.getStatus());

        // Retrieve last summary: from report_foot_list
        summary = response.getSummary().get(response.getSummary().size() - 1);
        Assert.assertEquals("9908282", summary.getIpsOrderId());
        Assert.assertEquals("Active", summary.getInvestmentStyle());
        Assert.assertEquals("Australian Shares", summary.getAssetClass());
        Assert.assertEquals(BigDecimal.ZERO, summary.getFum());
        Assert.assertEquals(Boolean.FALSE, summary.getHasScanTrigger());
        Assert.assertEquals(Boolean.FALSE, summary.getHasMandatoryCorporateActions());
        Assert.assertEquals("EMAILTP", summary.getModelCode());
        Assert.assertEquals("1261771", summary.getModelKey().getId());
        Assert.assertEquals("Emailtp", summary.getModelName());
        Assert.assertEquals("9879005", summary.getModelOrderId());
        Assert.assertEquals(Integer.valueOf(0), summary.getNumAccounts());
        Assert.assertNull(summary.getRebalanceStatus());
        Assert.assertEquals(IpsStatus.OPEN, summary.getStatus());
    }

    @Test
    public void testXPathMappings_whenEmptyResponse() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/modelportfolio/ModelPortfolioSummaryEmptyResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));
        defaultResponseExtractor = new DefaultResponseExtractor<>(ModelPortfolioSummaryResponse.class);
        ModelPortfolioSummaryResponse response = defaultResponseExtractor.extractData(content);

        Assert.assertNotNull(response);
        Assert.assertNull(response.getSummary());
    }

    @Test
    public void testModelSummaryResponse_setter() {
        ModelPortfolioSummaryResponse response = new ModelPortfolioSummaryResponse();
        response.setSummary(null);
        
        Assert.assertNull(response.getSummary());
    }
}
