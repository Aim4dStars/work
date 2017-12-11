package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.client.ProductKeyConverter;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.product.ProductKey;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class IpsProductListHolderTest {

    @InjectMocks
    private DefaultResponseExtractor<IpsProductListHolder> defaultResponseExtractor;
    @Mock
    private ParsingContext parsingContext;
    @Mock
    private ApplicationContext applicationContext;

    private IpsKeyConverter ipsKeyConverter = new IpsKeyConverter();
    private ProductKeyConverter productKeyConverter = new ProductKeyConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(IpsKeyConverter.class)).thenReturn(ipsKeyConverter);
        Mockito.when(applicationContext.getBean(ProductKeyConverter.class)).thenReturn(productKeyConverter);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void directContainerShouldContainBTCashAsset() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/IpsProductList_UT.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(IpsProductListHolder.class);
        IpsProductListHolder response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);
        assertEquals(3, response.getIpsList().size());

        IpsProductImpl ips = response.getIpsList().get(0);

        assertEquals(IpsKey.valueOf("57442"), ips.getIpsKey());
        assertEquals(ProductKey.valueOf("40214"), ips.getProductKey());
    }
}