package com.bt.nextgen.service.avaloq.transfer.transfergroup;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.client.AccountKeyConverter;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.externalasset.builder.DateTimeConverter;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.transfer.BeneficialOwnerChangeStatus;
import com.bt.nextgen.service.integration.transfer.TransferType;
import org.joda.time.DateTime;
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
import java.math.BigInteger;



@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class TransferGroupDetailsImplTest {

    @InjectMocks
    DefaultResponseExtractor<TransferGroupDetailsImpl> defaultResponseExtractor;

    @Mock
    ParsingContext parsingContext;

    @Mock
    ApplicationContext applicationContext;
    
    @Mock
    CodeConverter codeConverter;
    
    AccountKeyConverter accountKeyConverter = new AccountKeyConverter();

    BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();

    DateTimeConverter dateTimeConverter = new DateTimeConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(DateTimeConverter.class)).thenReturn(dateTimeConverter);
        Mockito.when(applicationContext.getBean(CodeConverter.class)).thenReturn(codeConverter);

        Mockito.when(codeConverter.convert("9092", "ORDER_TYPE")).thenReturn("form#btfg$xfer_bdl_in_specie_o");
        Mockito.when(codeConverter.convert("1", "INSPECIE_TRANSFER_TYPE")).thenReturn("btfg$ls_broker_sponsor");
        Mockito.when(codeConverter.convert("2", "CHANGE_BENEFICIAL_OWNERSHIP")).thenReturn("no");
        Mockito.when(codeConverter.convert("660464", "INCOME_PREFERENCE")).thenReturn("reinvest");
    }

    @Test
    public void testXpathMapping() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/TransferBundleResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(TransferGroupDetailsImpl.class);
        TransferGroupDetailsImpl response = defaultResponseExtractor.extractData(content);

        Assert.assertNotNull(response);
        Assert.assertNull(response.getErrorMessage());
        Assert.assertEquals(false, response.isErrorResponse());
        
        Assert.assertEquals(new DateTime("2017-02-01"), response.getTransferDate());
        Assert.assertEquals(OrderType.IN_SPECIE_TRANSFER, response.getOrderType());
        Assert.assertEquals("662757", response.getSourceAccountId());
        Assert.assertEquals("727245", response.getSourceContainerId());
        Assert.assertEquals("662756", response.getTargetAccountId());
        Assert.assertEquals("727244", response.getDestContainerId());
        Assert.assertEquals(false, response.getCloseAfterTransfer());
        Assert.assertEquals(TransferType.LS_BROKER_SPONSORED, response.getExternalTransferType());
        Assert.assertEquals(BeneficialOwnerChangeStatus.NO, response.getChangeOfBeneficialOwnership());
        Assert.assertEquals(IncomePreference.REINVEST, response.getIncomePreference());
        Assert.assertEquals(false, response.isChangeOfBeneficialOwnership());
        Assert.assertEquals(Integer.valueOf(30), response.getDrawdownDelayDays());
        Assert.assertEquals(1, response.getWarnings().size());
        Assert.assertEquals(1, response.getTransferAssets().size());

        Assert.assertEquals("110320", response.getLocListItem(0));
        Assert.assertEquals(BigInteger.valueOf(1), response.getLocItemIndex("110320"));
    }
}

