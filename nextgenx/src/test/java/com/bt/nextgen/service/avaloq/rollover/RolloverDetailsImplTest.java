package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.client.AccountKeyConverter;
import com.bt.nextgen.service.integration.rollover.RolloverOption;
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
public class RolloverDetailsImplTest {

    @InjectMocks
    DefaultResponseExtractor<RolloverDetailsImpl> defaultResponseExtractor;

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
        Mockito.when(codeConverter.convert("1", "ROLLOVER_TYPE")).thenReturn("btfg$rlov_cash");
    }

    @Test
    public void testXPathMappings() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/rollover/RolloverInResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(RolloverDetailsImpl.class);
        RolloverDetailsImpl response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);

        assertEquals("505948", response.getAccountKey().getId());
        assertEquals("54321", response.getRolloverId());
        assertEquals("12345", response.getFundId());
        assertEquals("CAMPBELL SUPERANNUATION FUND", response.getFundName());
        assertEquals("55696972656", response.getFundAbn());
        assertEquals("55696972656001", response.getFundUsi());
        assertEquals(BigDecimal.valueOf(123.34), response.getAmount());
        assertEquals(Boolean.TRUE, response.getPanInitiated());
        assertEquals(new DateTime("2016-10-13"), response.getRequestDate());
        assertEquals("1321651465", response.getAccountNumber());
        assertEquals(RolloverOption.PARTIAL, response.getRolloverOption());
        assertEquals(RolloverType.CASH_ROLLOVER, response.getRolloverType());
        assertEquals(Boolean.FALSE, response.getIncludeInsurance());
    }

}