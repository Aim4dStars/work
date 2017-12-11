package com.bt.nextgen.service.avaloq.drawdownstrategy;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.core.conversion.BigIntegerConverter;
import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.integration.drawdownstrategy.AssetExclusionDetails;
import com.bt.nextgen.service.integration.drawdownstrategy.AssetPriorityDetails;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategy;
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
import java.math.BigInteger;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class DrawdownStrategyDetailsImplTest {

    @InjectMocks
    DefaultResponseExtractor<DrawdownStrategyDetailsImpl> defaultResponseExtractor;

    @Mock
    ParsingContext parsingContext;

    @Mock
    ApplicationContext applicationContext;

    @Mock
    CodeConverter codeConverter;

    BigIntegerConverter bigIntegerConverter = new BigIntegerConverter();

    BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(BigIntegerConverter.class)).thenReturn(bigIntegerConverter);
        Mockito.when(applicationContext.getBean(BigDecimalConverter.class)).thenReturn(bigDecimalConverter);
        Mockito.when(applicationContext.getBean(CodeConverter.class)).thenReturn(codeConverter);

        Mockito.when(codeConverter.convert("660461", "DRAWDOWN_STRATEGY")).thenReturn("prorata");

    }

    @Test
    public void testXPathMappings_whenPreferenceServiceResponse_thenPriorityAndExclusionListPopulated() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/drawdown/DrawdownRetrievePriorityList.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(DrawdownStrategyDetailsImpl.class);
        DrawdownStrategyDetailsImpl response = defaultResponseExtractor.extractData(content);

        Assert.assertNotNull(response);

        List<AssetPriorityDetails> priorityList = response.getAssetPriorityDetails();
        Assert.assertEquals(2, priorityList.size());

        Assert.assertEquals(Integer.valueOf(1), priorityList.get(0).getDrawdownPriority());
        Assert.assertEquals(BigDecimal.TEN, priorityList.get(0).getDrawdownPercentage());
        Assert.assertEquals("110523", priorityList.get(0).getAssetId());

        Assert.assertEquals(Integer.valueOf(2), priorityList.get(1).getDrawdownPriority());
        Assert.assertEquals(BigDecimal.valueOf(20), priorityList.get(1).getDrawdownPercentage());
        Assert.assertEquals("110524", priorityList.get(1).getAssetId());

        List<AssetExclusionDetails> exclusionList = response.getAssetExclusionDetails();
        Assert.assertEquals(1, exclusionList.size());
        Assert.assertEquals("110523", exclusionList.get(0).getAssetId());

        Assert.assertNull(response.getErrorMessage());
        Assert.assertFalse(response.isErrorResponse());
        Assert.assertNull(response.getWarnings());

        Assert.assertEquals("110523", response.getLocListItem(0));
        Assert.assertEquals(BigInteger.valueOf(2), response.getLocItemIndex("110524"));

        // Fields not mapped in XPATH
        Assert.assertNull(response.getAccountKey());
        Assert.assertNull(response.getValidationErrors());
    }

    @Test
    public void testXPathMappings_whenContResponse_thenAllPopulated() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/drawdown/DrawdownContResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(DrawdownStrategyDetailsImpl.class);
        DrawdownStrategyDetailsImpl response = defaultResponseExtractor.extractData(content);

        Assert.assertNotNull(response);

        Assert.assertEquals(DrawdownStrategy.PRORATA, response.getDrawdownStrategy());

        List<AssetPriorityDetails> priorityList = response.getAssetPriorityDetails();
        Assert.assertEquals(2, priorityList.size());

        Assert.assertEquals(Integer.valueOf(1), priorityList.get(0).getDrawdownPriority());
        Assert.assertEquals(BigDecimal.TEN, priorityList.get(0).getDrawdownPercentage());
        Assert.assertEquals("12345", priorityList.get(0).getAssetId());

        Assert.assertEquals(Integer.valueOf(2), priorityList.get(1).getDrawdownPriority());
        Assert.assertEquals(BigDecimal.valueOf(20), priorityList.get(1).getDrawdownPercentage());
        Assert.assertEquals("54321", priorityList.get(1).getAssetId());

        List<AssetExclusionDetails> exclusionList = response.getAssetExclusionDetails();
        Assert.assertEquals(1, exclusionList.size());
        Assert.assertEquals("321321", exclusionList.get(0).getAssetId());

        Assert.assertNull(response.getErrorMessage());
        Assert.assertFalse(response.isErrorResponse());
        Assert.assertEquals(1, response.getWarnings().size());

        Assert.assertEquals("12345", response.getLocListItem(0));
        Assert.assertEquals(BigInteger.valueOf(2), response.getLocItemIndex("54321"));

        // Fields not mapped in XPATH
        Assert.assertNull(response.getAccountKey());
        Assert.assertNull(response.getValidationErrors());
    }

    @Test
    public void testGetIndexes_whenAssetPriorityListEmpty_thenNullValuesReturned() {
        DrawdownStrategyDetailsImpl empty = new DrawdownStrategyDetailsImpl();
        Assert.assertNull(empty.getLocItemIndex("assetId"));
        Assert.assertNull(empty.getLocListItem(1));
    }

}