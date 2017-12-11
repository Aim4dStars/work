package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.bt.nextgen.service.integration.movemoney.ContributionType;
import com.bt.nextgen.service.integration.movemoney.DepositStatus;
import com.bt.nextgen.service.integration.movemoney.OrderType;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
public class DepositsHolderTest {
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    @InjectMocks
    DefaultResponseExtractor<DepositHolderImpl> depositResponseExtractor;

    @Mock
    ParsingContext parsingContext;
    @Mock
    ApplicationContext applicationContext;

    @Mock
    CodeConverter codeConverter;

    DateTimeTypeConverter dateTimeTypeConverter = new DateTimeTypeConverter();

    BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(CodeConverter.class)).thenReturn(codeConverter);
        Mockito.when(applicationContext.getBean(DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);
        Mockito.when(applicationContext.getBean(BigDecimalConverter.class)).thenReturn(bigDecimalConverter);

        Mockito.when(codeConverter.convert("43", "ORDER_STATUS")).thenReturn("activ");
        Mockito.when(codeConverter.convert("101", "ORDER_STATUS")).thenReturn("nsubm");

        Mockito.when(codeConverter.convert("61", "SUPER_CONTRIBUTIONS_TYPE")).thenReturn("prsnl_sav_nclaim");
        Mockito.when(codeConverter.convert("11", "SUPER_CONTRIBUTIONS_TYPE")).thenReturn("spouse");

        Mockito.when(codeConverter.convert("1050", "ORDER_TYPE")).thenReturn("inpay#super_contri");
        Mockito.when(codeConverter.convert("1054", "ORDER_TYPE")).thenReturn("lsv_at#stord_new_sa_contri_in");

        Mockito.when(codeConverter.convert("23", "CODES_PAYMENT_FREQUENCIES")).thenReturn("rm");
        Mockito.when(codeConverter.convert("24", "CODES_PAYMENT_FREQUENCIES")).thenReturn("rq");
        Mockito.when(codeConverter.convert("25", "CODES_PAYMENT_FREQUENCIES")).thenReturn("rs");
        Mockito.when(codeConverter.convert("26", "CODES_PAYMENT_FREQUENCIES")).thenReturn("ry");
        Mockito.when(codeConverter.convert("65", "CODES_PAYMENT_FREQUENCIES")).thenReturn("weekly");
        Mockito.when(codeConverter.convert("66", "CODES_PAYMENT_FREQUENCIES")).thenReturn("weekly_2");
        Mockito.when(codeConverter.convert("5000", "CODES_PAYMENT_FREQUENCIES")).thenReturn("btfg$once");
    }

    @Test
    public void testExtractDepositData_whenValidResponse_thenDepositPopulated() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/SavedDepositsLoadResponse_UT.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        depositResponseExtractor = new DefaultResponseExtractor<>(DepositHolderImpl.class);
        DepositHolderImpl response = depositResponseExtractor.extractData(content);

        assertNotNull(response);
        assertNotNull(response.getDeposits());
        assertEquals(6, response.getDeposits().size());

        assertEquals(RecurringDepositDetailsImpl.class, response.getDeposits().get(0).getClass());
        assertEquals("6440061", response.getDeposits().get(0).getDepositId());
        assertEquals("5", response.getDeposits().get(0).getTransactionSeq());
        assertEquals(DepositStatus.NOT_SUBMITTED, response.getDeposits().get(0).getStatus());
        assertEquals(BigDecimal.valueOf(250), response.getDeposits().get(0).getDepositAmount());
        assertEquals("description test", response.getDeposits().get(0).getDescription());
        assertEquals("2017-01-27", formatter.print(response.getDeposits().get(0).getTransactionDate()));
        assertEquals("2017-10-24", formatter.print(((RecurringDepositDetails) response.getDeposits().get(0)).getEndDate()));
        assertEquals(4, ((RecurringDepositDetails) response.getDeposits().get(0)).getMaxCount().intValue());
        assertEquals(RecurringFrequency.Quarterly, response.getDeposits().get(0).getRecurringFrequency());
        assertEquals(ContributionType.PERSONAL, response.getDeposits().get(0).getContributionType());
        assertEquals("ACCT TEST", response.getDeposits().get(0).getPayerName());
        assertEquals("036081", response.getDeposits().get(0).getPayerBsb());
        assertEquals("123456782", response.getDeposits().get(0).getPayerAccount());
        assertEquals(OrderType.SUPER_RECURRING_CONTRIBUTION, response.getDeposits().get(0).getOrderType());

        assertEquals(DepositDetailsImpl.class, response.getDeposits().get(1).getClass());
        assertEquals("6668933", response.getDeposits().get(1).getDepositId());
        assertEquals("2", response.getDeposits().get(1).getTransactionSeq());
        assertEquals(DepositStatus.NOT_SUBMITTED, response.getDeposits().get(1).getStatus());
        assertEquals(BigDecimal.valueOf(70), response.getDeposits().get(1).getDepositAmount());
        assertEquals("description", response.getDeposits().get(1).getDescription());
        assertEquals("2017-02-21", formatter.print(response.getDeposits().get(1).getTransactionDate()));
        assertEquals(RecurringFrequency.Once, response.getDeposits().get(1).getRecurringFrequency());
        assertEquals(ContributionType.PERSONAL, response.getDeposits().get(1).getContributionType());
        assertEquals("test payer", response.getDeposits().get(1).getPayerName());
        assertEquals("036081", response.getDeposits().get(1).getPayerBsb());
        assertEquals("897324978", response.getDeposits().get(1).getPayerAccount());
        assertEquals(OrderType.SUPER_ONE_OFF_CONTRIBUTION, response.getDeposits().get(1).getOrderType());
    }
}
