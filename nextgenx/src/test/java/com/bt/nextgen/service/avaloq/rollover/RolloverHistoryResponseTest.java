package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.client.AccountKeyConverter;
import com.bt.nextgen.service.integration.rollover.RolloverHistory;
import com.bt.nextgen.service.integration.rollover.RolloverHistoryResponse;
import com.bt.nextgen.service.integration.rollover.RolloverOption;
import com.bt.nextgen.service.integration.rollover.RolloverStatus;
import com.bt.nextgen.service.integration.rollover.RolloverType;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class RolloverHistoryResponseTest {

    @InjectMocks
    DefaultResponseExtractor<RolloverHistoryResponseImpl> defaultResponseExtractor;

    @Mock
    ParsingContext parsingContext;

    @Mock
    ApplicationContext applicationContext;

    @Mock
    CodeConverter codeConverter;

    AccountKeyConverter accountKeyConverter = new AccountKeyConverter();

    BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();

    DateTimeTypeConverter dateTimeTypeConverter = new DateTimeTypeConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(AccountKeyConverter.class)).thenReturn(accountKeyConverter);
        Mockito.when(applicationContext.getBean(BigDecimalConverter.class)).thenReturn(bigDecimalConverter);
        Mockito.when(applicationContext.getBean(DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);
        Mockito.when(applicationContext.getBean(CodeConverter.class)).thenReturn(codeConverter);

        Mockito.when(codeConverter.convert("1", "ROLLOVER_OPTION")).thenReturn("btfg$part_rlov");
        Mockito.when(codeConverter.convert("2", "ROLLOVER_OPTION")).thenReturn("btfg$full_rlov");
        Mockito.when(codeConverter.convert("1", "ROLLOVER_TYPE")).thenReturn("btfg$rlov_cash");
        Mockito.when(codeConverter.convert("2", "ROLLOVER_TYPE")).thenReturn("btfg$rlov_asset");
        Mockito.when(codeConverter.convert("1", "ROLLOVER_STATUS")).thenReturn("btfg$req_na");
        Mockito.when(codeConverter.convert("2", "ROLLOVER_STATUS")).thenReturn("btfg$req_pending");
        Mockito.when(codeConverter.convert("3", "ROLLOVER_STATUS")).thenReturn("btfg$req_complete");
        Mockito.when(codeConverter.convert("21", "ROLLOVER_STATUS")).thenReturn("btfg$req_submitted");
        Mockito.when(codeConverter.convert("22", "ROLLOVER_STATUS")).thenReturn("btfg$req_notsubmitted");
    }

    @Test
    public void testXPathMappings() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/rollover/RolloverHistoryResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(RolloverHistoryResponseImpl.class);
        RolloverHistoryResponse response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);
        assertEquals(5, response.getRolloverHistory().size());

        RolloverHistory historyItem = response.getRolloverHistory().get(0);
        assertEquals("1000", historyItem.getRolloverId());
        assertEquals("A super fund", historyItem.getFundName());
        assertEquals("Fund ABN", historyItem.getFundAbn());
        assertEquals("Fund USI", historyItem.getFundUsi());
        assertEquals("12345", historyItem.getFundMemberId());
        assertEquals("12345", historyItem.getFundId());
        assertEquals(new DateTime("2016-10-25"), historyItem.getDateRequested());
        assertEquals(RolloverStatus.PENDING, historyItem.getRequestStatus());
        assertEquals(BigDecimal.valueOf(100), historyItem.getAmount());
        assertEquals(RolloverOption.FULL, historyItem.getRolloverOption());
        assertEquals(RolloverType.CASH_ROLLOVER, historyItem.getRolloverType());
        assertEquals(Boolean.TRUE, historyItem.getInitiatedByPanorama());

        RolloverHistory historyItem2 = response.getRolloverHistory().get(1);
        assertEquals("1001", historyItem2.getRolloverId());
        assertEquals("External super fund", historyItem2.getFundName());
        assertEquals("123", historyItem2.getFundMemberId());
        assertEquals("123", historyItem2.getFundId());
        assertEquals(RolloverStatus.SUBMITTED, historyItem2.getRequestStatus());
        assertEquals(BigDecimal.valueOf(2000), historyItem2.getAmount());
        assertEquals(RolloverOption.PARTIAL, historyItem2.getRolloverOption());
        assertEquals(RolloverType.ASSET_ROLLOVER, historyItem2.getRolloverType());
        assertEquals(Boolean.FALSE, historyItem2.getInitiatedByPanorama());

        RolloverHistory historyItem3 = response.getRolloverHistory().get(2);
        assertEquals("1002", historyItem3.getRolloverId());
        assertEquals("Pending external super fund", historyItem3.getFundName());
        assertEquals("321", historyItem3.getFundMemberId());
        assertEquals("321", historyItem3.getFundId());
        assertEquals(RolloverStatus.NA, historyItem3.getRequestStatus());
        assertEquals(BigDecimal.valueOf(2000), historyItem3.getAmount());
        assertEquals(RolloverOption.PARTIAL, historyItem3.getRolloverOption());
        assertEquals(RolloverType.CASH_ROLLOVER, historyItem3.getRolloverType());
        assertEquals(Boolean.FALSE, historyItem3.getInitiatedByPanorama());

        RolloverHistory historyItem4 = response.getRolloverHistory().get(3);
        assertEquals("1003", historyItem4.getRolloverId());
        assertEquals("Saved super fund", historyItem4.getFundName());
        assertEquals("412", historyItem4.getFundMemberId());
        assertEquals("412", historyItem4.getFundId());
        assertEquals(RolloverStatus.NOT_SUBMITTED, historyItem4.getRequestStatus());
        assertEquals(BigDecimal.valueOf(2000), historyItem4.getAmount());
        assertEquals(RolloverOption.FULL, historyItem4.getRolloverOption());
        assertEquals(RolloverType.ASSET_ROLLOVER, historyItem4.getRolloverType());
        assertEquals(Boolean.TRUE, historyItem4.getInitiatedByPanorama());

        RolloverHistory historyItem5 = response.getRolloverHistory().get(4);
        assertEquals("1004", historyItem5.getRolloverId());
        assertEquals("Completed super fund", historyItem5.getFundName());
        assertEquals("412", historyItem5.getFundMemberId());
        assertEquals("412", historyItem5.getFundId());
        assertEquals(RolloverStatus.COMPLETE, historyItem5.getRequestStatus());
        assertEquals(BigDecimal.valueOf(2000), historyItem5.getAmount());
        assertEquals(RolloverOption.FULL, historyItem5.getRolloverOption());
        assertEquals(RolloverType.ASSET_ROLLOVER, historyItem5.getRolloverType());
        assertEquals(Boolean.TRUE, historyItem5.getInitiatedByPanorama());
    }

}