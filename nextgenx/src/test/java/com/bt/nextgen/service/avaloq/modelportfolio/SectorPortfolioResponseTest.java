package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.core.conversion.BigIntegerConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio.SectorPortfolio;
import com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio.SectorPortfolioResponseImpl;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class SectorPortfolioResponseTest {

    @InjectMocks
    DefaultResponseExtractor<SectorPortfolioResponseImpl> defaultResponseExtractor;

    @Mock
    ParsingContext parsingContext;

    @Mock
    ApplicationContext applicationContext;

    DateTimeTypeConverter dateTimeTypeConverter = new DateTimeTypeConverter();

    BigIntegerConverter bigIntegerConverter = new BigIntegerConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);
        Mockito.when(applicationContext.getBean(BigIntegerConverter.class)).thenReturn(bigIntegerConverter);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testXPathMappings() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/modelportfolio/SectorPortfolioResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(SectorPortfolioResponseImpl.class);
        SectorPortfolioResponseImpl response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);
        assertEquals(1, response.getSectorPortfolios().size());

        SectorPortfolio sectorPortfolio = response.getSectorPortfolios().get(0);
        assertNotNull(sectorPortfolio);

        assertEquals("111746", sectorPortfolio.getId());
        assertEquals("TEST MPF", sectorPortfolio.getName());
        assertEquals("TEST", sectorPortfolio.getCode());
        assertEquals("123456", sectorPortfolio.getInvestmentManagerId());
        assertEquals("4444", sectorPortfolio.getAssetClass());
        assertEquals("CATEGORY", sectorPortfolio.getCategory());
        assertEquals("5555", sectorPortfolio.getProductType());
        assertEquals("6666", sectorPortfolio.getStatus());
        assertEquals(BigInteger.valueOf(3), sectorPortfolio.getIpsCount());
        assertEquals(new DateTime("2015-09-29"), sectorPortfolio.getLastModifiedDate());
        assertEquals("USER", sectorPortfolio.getLastModifiedBy());
    }

}