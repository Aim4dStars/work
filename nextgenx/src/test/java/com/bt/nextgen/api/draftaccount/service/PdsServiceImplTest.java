package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class PdsServiceImplTest {

    @Mock
    private CmsService cmsService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @InjectMocks
    private PdsServiceImpl pdsService;

    @Test
    public void shouldRetrieveUrlFromCMSUsingAdvisersDealerGroupIdAndCPCCode() throws Exception {
        String expectedUrl = "Some URL";
        String cpcCode = "MY_CPC_CODE";
        ProductKey productKey = ProductKey.valueOf("MY_PRODUCT_ID");
        BrokerKey adviserPositionId = BrokerKey.valueOf("SOME_ADVISER_POSITION_ID");

        Product product = mock(Product.class);

        when(product.getCpcCode()).thenReturn(cpcCode);
        when(productIntegrationService.getProductDetail(eq(productKey), any(ServiceErrors.class))).thenReturn(product);

        Broker adviserBroker = mock(Broker.class);
        when(adviserBroker.getDealerKey()).thenReturn(BrokerKey.valueOf("MY_DEALER_GROUP"));
        when(brokerIntegrationService.getBroker(eq(adviserPositionId), any(ServiceErrors.class))).thenReturn(adviserBroker);

        String contentId = PdsService.PDS_CMS_KEY_PREFIX + adviserBroker.getDealerKey().getId() + "_" + cpcCode;
        when(cmsService.getContent(contentId)).thenReturn(expectedUrl);

        String url = pdsService.getUrl(productKey, adviserPositionId, mock(ServiceErrors.class));
        assertThat(url, is(expectedUrl));
        verify(cmsService, times(1)).getContent(contentId);
        verify(cmsService, times(0)).getContent(PdsService.PDS_CMS_KEY_PREFIX + cpcCode);
    }

    @Test
    public void shouldRetrieveUrlFromCMSUsingCPCCode() throws Exception {
        String expectedUrl = "Some URL";
        String cpcCode = "MY_CPC_CODE";
        String keyWithCpcCode = PdsService.PDS_CMS_KEY_PREFIX + cpcCode;
        ProductKey productKey = ProductKey.valueOf("MY_PRODUCT_ID");
        BrokerKey adviserPositionId = BrokerKey.valueOf("SOME_ADVISER_POSITION_ID");

        Product product = mock(Product.class);

        when(product.getCpcCode()).thenReturn(cpcCode);
        when(productIntegrationService.getProductDetail(eq(productKey), any(ServiceErrors.class))).thenReturn(product);

        Broker adviserBroker = mock(Broker.class);
        when(adviserBroker.getDealerKey()).thenReturn(BrokerKey.valueOf("MY_DEALER_GROUP"));
        when(brokerIntegrationService.getBroker(eq(adviserPositionId), any(ServiceErrors.class))).thenReturn(adviserBroker);

        String contentId = PdsService.PDS_CMS_KEY_PREFIX + adviserBroker.getDealerKey().getId() + "_" + cpcCode;
        when(cmsService.getContent(contentId)).thenReturn(null);
        when(cmsService.getContent(keyWithCpcCode)).thenReturn(expectedUrl);

        String url = pdsService.getUrl(productKey, adviserPositionId, mock(ServiceErrors.class));
        assertThat(url, is(expectedUrl));
        verify(cmsService, times(1)).getContent(contentId);
        verify(cmsService, times(1)).getContent(keyWithCpcCode);
    }
}
