package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.client.AccountKeyConverter;
import com.bt.nextgen.service.integration.externalasset.builder.IsoDateTimeConverter;
import com.bt.nextgen.service.integration.rollover.RolloverReceived;
import com.bt.nextgen.service.integration.rollover.RolloverType;
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
public class RolloverReceivedResponseTest {

    @InjectMocks
    DefaultResponseExtractor<RolloverReceivedResponseImpl> defaultResponseExtractor;

    @Mock
    ParsingContext parsingContext;

    @Mock
    ApplicationContext applicationContext;

    @Mock
    CodeConverter codeConverter;

    AccountKeyConverter accountKeyConverter = new AccountKeyConverter();

    BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();

    IsoDateTimeConverter dateTimeTypeConverter = new IsoDateTimeConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(AccountKeyConverter.class)).thenReturn(accountKeyConverter);
        Mockito.when(applicationContext.getBean(BigDecimalConverter.class)).thenReturn(bigDecimalConverter);
        Mockito.when(applicationContext.getBean(IsoDateTimeConverter.class)).thenReturn(dateTimeTypeConverter);
        Mockito.when(applicationContext.getBean(CodeConverter.class)).thenReturn(codeConverter);

        Mockito.when(codeConverter.convert("1", "ROLLOVER_TYPE")).thenReturn("btfg$rlov_cash");
        Mockito.when(codeConverter.convert("2", "ROLLOVER_TYPE")).thenReturn("btfg$rlov_asset");
        Mockito.when(codeConverter.convert("2", "ROLLOVER_STATUS")).thenReturn("btfg$req_pending");
        Mockito.when(codeConverter.convert("3", "ROLLOVER_STATUS")).thenReturn("btfg$req_complete");
    }

    @Test
    public void testXPathMappings() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/rollover/RolloverReceivedResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(RolloverReceivedResponseImpl.class);
        RolloverReceivedResponseImpl response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);
        assertEquals(4, response.getReceivedSuperfunds().size());

        RolloverReceived rollover = response.getReceivedSuperfunds().get(0);
        assertEquals("4357048", rollover.getFundId());
        assertEquals("11 223 491 505", rollover.getFundAbn());
        assertEquals(null, rollover.getFundUsi());
        assertEquals("fund", rollover.getFundName());
        assertEquals(new DateTime("2016-11-11T15:55:28+11:00"), rollover.getReceivedDate());
        assertEquals(BigDecimal.valueOf(100000), rollover.getAmount());
        assertEquals(RolloverType.CASH_ROLLOVER, rollover.getRolloverType());
    }

}