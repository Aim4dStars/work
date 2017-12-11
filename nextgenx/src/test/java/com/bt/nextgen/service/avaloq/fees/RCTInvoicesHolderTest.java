package com.bt.nextgen.service.avaloq.fees;

import static org.junit.Assert.assertNotNull;

import java.io.InputStreamReader;
import java.math.BigDecimal;

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

import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.integration.fees.RCTInvoicesFee;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class RCTInvoicesHolderTest {

    @InjectMocks
    private DefaultResponseExtractor<RCTInvoicesImpl> defaultResponseExtractor;
    
    @Mock
    private ParsingContext parsingContext;
    
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private CodeConverter codeConverter;

    private DateTimeTypeConverter dateTimeTypeConverter = new DateTimeTypeConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);
        Mockito.when(applicationContext.getBean(CodeConverter.class)).thenReturn(codeConverter);
        Mockito.when(codeConverter.convert("1054", "ORDER_TYPE")).thenReturn("avsr_estab");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void CashMovementsHolderTest_whenPopulatedResponse_shouldReturnTaxInvoices() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/RecipientCreatedTaxInvoicesResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(RCTInvoicesImpl.class);
        RCTInvoices response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);
        assertNotNull(response.getRCTInvoicesFees());

        Assert.assertEquals("L Westpac Financial Planning (Private Bank)", response.getSupplier());
        Assert.assertEquals("FL 8 33 Pitt St Sydney NSW 2000 AUSTRALIA", response.getSupplierAddress());
        Assert.assertEquals("12004044937", response.getSupplierABN());
        Assert.assertEquals("L BT Funds Management Limited", response.getRecipient());
        Assert.assertEquals("FL 8 33 Pitt St Sydney NSW 2000 AUSTRALIA", response.getRecipientAddress());
        Assert.assertEquals("12004044937", response.getRecipientABN());
        Assert.assertEquals(BigDecimal.valueOf(0.91), response.getGST());
        Assert.assertEquals(BigDecimal.valueOf(9.09), response.getFeeExcludingGST());
        Assert.assertEquals(BigDecimal.valueOf(10), response.getFeeIncludingGST());
        Assert.assertEquals(response.getRCTInvoicesFees().size(), 1);

        RCTInvoicesFee taxInvoice = response.getRCTInvoicesFees().get(0);
        
        Assert.assertEquals(OrderType.ESTABLISHMENT_FEE, taxInvoice.getOrderType());
        Assert.assertEquals(new DateTime("2017-06-07"), taxInvoice.getInvoiceDate());
        Assert.assertEquals(BigDecimal.valueOf(0.91), taxInvoice.getGST());
        Assert.assertEquals(BigDecimal.valueOf(9.09), taxInvoice.getFeeExcludingGST());
        Assert.assertEquals(BigDecimal.valueOf(10), taxInvoice.getFeeIncludingGST());
    }

    @Test
    public void CashMovementsTest_whenEmptyResponse_shouldReturnEmptyList() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/RecipientCreatedTaxInvoicesEmptyResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(RCTInvoicesImpl.class);
        RCTInvoices response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);
        assertNotNull(response.getRCTInvoicesFees());

        Assert.assertEquals(0, response.getRCTInvoicesFees().size());
    }

}