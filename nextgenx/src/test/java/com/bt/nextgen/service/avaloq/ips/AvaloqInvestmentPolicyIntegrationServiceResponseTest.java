package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.client.BrokerKeyConverter;
import com.bt.nextgen.service.integration.ips.IpsSummaryDetails;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ConstructionType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioStatus;
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

import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class AvaloqInvestmentPolicyIntegrationServiceResponseTest {

    @InjectMocks
    DefaultResponseExtractor<IpsSummaryList> defaultResponseExtractor;
    @Mock
    ParsingContext parsingContext;
    @Mock
    ApplicationContext applicationContext;

    @Mock
    CodeConverter codeConverter;

    IpsKeyConverter ipsKeyConverter = new IpsKeyConverter();

    BrokerKeyConverter brokerKeyConverter = new BrokerKeyConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(IpsKeyConverter.class)).thenReturn(ipsKeyConverter);
        Mockito.when(applicationContext.getBean(BrokerKeyConverter.class)).thenReturn(brokerKeyConverter);

        Mockito.when(applicationContext.getBean(CodeConverter.class)).thenReturn(codeConverter);
        Mockito.when(codeConverter.convert("9510", "IPS_STATUS")).thenReturn("new");
        Mockito.when(codeConverter.convert("9511", "IPS_STATUS")).thenReturn("pend");
        Mockito.when(codeConverter.convert("9512", "IPS_STATUS")).thenReturn("opn");
        Mockito.when(codeConverter.convert("9514", "IPS_STATUS")).thenReturn("susp");
        Mockito.when(codeConverter.convert("9515", "IPS_STATUS")).thenReturn("ter");
        Mockito.when(codeConverter.convert("9520", "CONSTRUCTION_TYPE")).thenReturn("fixed");
    }

    @Test
    public void testToIpsSummaryDetails_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/ips/IpsSummaryListResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(IpsSummaryList.class);
        IpsSummaryList response = defaultResponseExtractor.extractData(content);

        // Response must be retrieved.
        assertNotNull(response);
        assertNotNull(response.getSummaryDetailsList());
        Assert.assertEquals(39, response.getSummaryDetailsList().size());

        // Validate data for the first IpsSummaryDetails.
        IpsSummaryDetails ips = response.getSummaryDetailsList().get(0);
        Assert.assertEquals("938403", ips.getInvestmentManagerId().getId());
        Assert.assertEquals("939135", ips.getModelKey().getId());
        Assert.assertEquals("supertmpmda", ips.getModelName());
        Assert.assertEquals("7518543", ips.getIpsOrderId());
        Assert.assertEquals("7245267", ips.getModelOrderId());
        Assert.assertEquals("SUTMP", ips.getModelCode());
        assertNotNull(ModelType.forId(ips.getAccountType()));
        Assert.assertEquals(ConstructionType.FIXED, ips.getModelConstruction());

        // Verify status
        assertNotNull(ips.getStatus());
        Assert.assertEquals(ModelPortfolioStatus.OPEN, ips.getStatus());
    }
}