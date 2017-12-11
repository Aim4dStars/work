package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.client.AccountKeyConverter;
import com.bt.nextgen.service.integration.externalasset.builder.IsoDateTimeConverter;
import com.bt.nextgen.service.integration.rollover.ContributionReceived;
import com.bt.nextgen.service.integration.rollover.RolloverContributionStatus;
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
public class ContributionReceivedResponseTest {

    @InjectMocks
    DefaultResponseExtractor<ContributionReceivedResponseImpl> defaultResponseExtractor;

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

        Mockito.when(codeConverter.convert("1", "CONTRIBUTION_STATUS")).thenReturn("btfg$in_progress");
        Mockito.when(codeConverter.convert("2", "CONTRIBUTION_STATUS")).thenReturn("btfg$rcvd");
    }

    @Test
    public void testXPathMappings() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/rollover/ContributionReceivedResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(ContributionReceivedResponseImpl.class);
        ContributionReceivedResponseImpl response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);
        assertEquals(1, response.getContributionReceived().size());

        ContributionReceived contri = response.getContributionReceived().get(0);
        assertEquals("4355860", contri.getContributionId());
        assertEquals("Rollover in", contri.getDescription());
        assertEquals(new DateTime("2016-11-10T15:19:47+11:00"), contri.getPaymentDate());
        assertEquals(BigDecimal.valueOf(100000), contri.getAmount());
        assertEquals(RolloverContributionStatus.IN_PROGRESS, contri.getContributionStatus());
    }

}