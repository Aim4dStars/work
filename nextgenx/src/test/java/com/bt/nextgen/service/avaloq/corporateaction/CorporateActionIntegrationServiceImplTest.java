package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.core.conversion.AssetKeyConverter;
import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.client.AccountKeyConverter;
import com.bt.nextgen.service.integration.corporateaction.CorporateAction;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionResponse;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
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

import java.io.InputStreamReader;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class CorporateActionIntegrationServiceImplTest {

    @InjectMocks
    private DefaultResponseExtractor<CorporateActionResponseImpl> defaultResponseExtractor;

    @Mock
    private ParsingContext parsingContext;
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private CodeConverter codeConverter;

    private AccountKeyConverter accountKeyConverter = new AccountKeyConverter();

    private AssetKeyConverter assetKeyConverter = new AssetKeyConverter();

    private DateTimeTypeConverter dateTimeTypeConverter = new DateTimeTypeConverter();

    private BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);
        Mockito.when(applicationContext.getBean(BigDecimalConverter.class)).thenReturn(bigDecimalConverter);
        Mockito.when(applicationContext.getBean(AssetKeyConverter.class)).thenReturn(assetKeyConverter);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test_loadVoluntaryCorporateActionsTest() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/corporateactions/CorporateActionLoadVoluntary.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(CorporateActionResponseImpl.class);
        CorporateActionResponse response = defaultResponseExtractor.extractData(content);

        Assert.assertNotNull(response);

        Assert.assertEquals(15, response.getCorporateActions().size());
        CorporateAction corporateAction = response.getCorporateActions().get(0);

        Assert.assertNotNull(corporateAction);

        Assert.assertEquals("6650944", corporateAction.getOrderNumber());
        Assert.assertEquals("110744", corporateAction.getAssetId());
        Assert.assertNotNull(corporateAction.getCloseDate());
        Assert.assertNotNull(corporateAction.getAnnouncementDate());
        Assert.assertNotNull(corporateAction.getExDate());
    }

    @Test
    public void test_loadMandatoryCorporateActionsTest() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/corporateactions/CorporateActionLoadMandatory.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(CorporateActionResponseImpl.class);
        CorporateActionResponse response = defaultResponseExtractor.extractData(content);

        Assert.assertNotNull(response);

        Assert.assertEquals(20, response.getCorporateActions().size());
        CorporateAction corporateAction = response.getCorporateActions().get(0);

        Assert.assertNotNull(corporateAction);

        Assert.assertEquals("6665321", corporateAction.getOrderNumber());
        Assert.assertEquals("531371", corporateAction.getAssetId());
        Assert.assertNotNull(corporateAction.getPayDate());
        Assert.assertNotNull(corporateAction.getAnnouncementDate());
        Assert.assertNotNull(corporateAction.getExDate());
    }
}