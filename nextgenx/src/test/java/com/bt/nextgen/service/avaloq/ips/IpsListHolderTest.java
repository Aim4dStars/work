package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class IpsListHolderTest {

    @InjectMocks
    DefaultResponseExtractor<IpsListHolder> defaultResponseExtractor;
    @Mock
    ParsingContext parsingContext;
    @Mock
    ApplicationContext applicationContext;

    IpsKeyConverter keyConverter = new IpsKeyConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(IpsKeyConverter.class)).thenReturn(keyConverter);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void directContainerShouldContainBTCashAsset() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/Ips_UT.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(IpsListHolder.class);
        IpsListHolder response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);
        assertEquals(48, response.getIpsList().size());

        InvestmentPolicyStatementInterface ips = response.getIpsList().get(0);

        assertEquals("Bennelong Aus Equities Portfolio", ips.getInvestmentName());
        assertEquals("BEN001AUS", ips.getCode());
        assertEquals("WFS0556AU", ips.getApirCode());
        assertEquals("9650", ips.getAssetClassId());
        assertEquals("9600", ips.getInvestmentStyleId());
        assertEquals(BigDecimal.valueOf(.0075), ips.getPercentage());
        assertEquals(BigDecimal.valueOf(25000), ips.getMinInitInvstAmt());
        assertEquals("110184", ips.getInvestmentManagerPersonId());
        assertEquals(true, ips.getTaxAssetDomicile());

    }
}