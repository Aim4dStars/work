package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.client.BrokerKeyConverter;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ConstructionType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioStatus;
import com.bt.nextgen.service.integration.modelportfolio.detail.OfferDetail;
import com.bt.nextgen.service.integration.modelportfolio.detail.TargetAllocation;
import org.junit.After;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class TbrIpsDetailsTest {

    @InjectMocks
    DefaultResponseExtractor<IpsDetailList> defaultResponseExtractor;
    @Mock
    ParsingContext parsingContext;
    @Mock
    ApplicationContext applicationContext;

    @Mock
    private CodeConverter codeConverter;

    private BrokerKeyConverter brokerKeyConverter = new BrokerKeyConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(CodeConverter.class)).thenReturn(codeConverter);
        Mockito.when(applicationContext.getBean(BrokerKeyConverter.class)).thenReturn(brokerKeyConverter);

        Mockito.when(codeConverter.convert("9510", "IPS_STATUS")).thenReturn("new");
        Mockito.when(codeConverter.convert("8071", "MODEL_STRUCT")).thenReturn("8071");
        Mockito.when(codeConverter.convert("8062", "MP_TYPE")).thenReturn("8062");
        Mockito.when(codeConverter.convert("9600", "IPS_INVESTMENT_STYLE")).thenReturn("9600");
        Mockito.when(codeConverter.convert("9650", "IPS_ASSET_CLASS")).thenReturn("9650");
        Mockito.when(codeConverter.convert("9520", "CONSTRUCTION_TYPE")).thenReturn("fixed");
        Mockito.when(codeConverter.convert("60661", "ASSET_CLASS")).thenReturn("eq_au");
        Mockito.when(codeConverter.convert("60671", "ASSET_CLASS")).thenReturn("cash");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void directContainerShouldContainBTCashAsset() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/IpsDetailsResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(IpsDetailList.class);
        IpsDetailList response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);
        assertEquals(1, response.getIpsList().size());
        IpsDetails ips = response.getIpsList().get(0);

        assertEquals("859910", ips.getId());
        assertEquals("xx test 3", ips.getName());
        assertEquals("20170213-1", ips.getSymbol());
        assertEquals("110200", ips.getInvestmentManagerId().getId());
        assertEquals("859815", ips.getAalId());
        assertEquals("8062", ips.getModelType());
        assertEquals("9650", ips.getModelAssetClass());
        assertEquals("9600", ips.getInvestmentStyle());
        assertEquals("8071", ips.getModelStructure());
        assertEquals(ConstructionType.FIXED, ips.getModelConstruction());
        assertEquals(ModelPortfolioStatus.NEW, ips.getStatus());
        assertEquals(ModelType.INVESTMENT.getId(), ips.getAccountType());
        assertEquals(BigDecimal.valueOf(1000), ips.getMinimumInvestment());
        assertEquals("doot", ips.getModelDescription());
        assertEquals(BigDecimal.ZERO, ips.getMinimumTradeAmount());
        assertEquals(BigDecimal.ONE, ips.getMinimumTradePercent());
        assertEquals("other investment style", ips.getInvestmentStyleDesc());

        assertEquals(2, ips.getTargetAllocations().size());
        validateTargetAllocations(ips.getTargetAllocations());

        assertEquals(1, ips.getOfferDetails().size());
        validateOfferDetails(ips.getOfferDetails());

        assertNull(ips.getMpSubType());
    }

    private void validateTargetAllocations(List<TargetAllocation> taaList) {
        TargetAllocation taa = taaList.get(0);
        assertEquals("eq_au", taa.getAssetClass());
        assertEquals("111662", taa.getIndexAssetId());
        assertEquals(BigDecimal.valueOf(98), taa.getMaximumWeight());
        assertEquals(BigDecimal.valueOf(97), taa.getMinimumWeight());
        assertEquals(BigDecimal.valueOf(97.5), taa.getNeutralPos());
    }

    private void validateOfferDetails(List<OfferDetail> offList) {
        OfferDetail off = offList.get(0);
        assertEquals("108285", off.getOfferId());
    }
}