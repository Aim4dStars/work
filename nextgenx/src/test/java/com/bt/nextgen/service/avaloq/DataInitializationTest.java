package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositAssetRateIntegrationService;
import com.bt.nextgen.service.avaloq.paginated.CacheManagedPaginatedBrokerIntegrationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by L069552 on 7/09/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class DataInitializationTest {

    @InjectMocks
    DataInitialization dataInitialization;

    @Mock
    private CacheManagedTermDepositAssetRateIntegrationService rateIntegrationService;

    @Mock
    private CacheManagedPaginatedBrokerIntegrationService paginatedBrokerIntegrationService;

    @Mock
    private FeatureTogglesService togglesService;

    @Mock
    private FeatureToggles featureToggles;

    @Before
    public void setUp(){
        when(togglesService.findOne(any(ServiceErrors.class))).thenReturn(featureToggles);
    }

    @Test
    public void testLoadTermDepositAssetRates(){
        dataInitialization.loadTermDepositAssetRates();
        verify(rateIntegrationService,times(1)).clearCache();
        verify(rateIntegrationService,times(1)).loadTermDepositAssetsToCache(any(ServiceErrors.class));
        verify(rateIntegrationService,times(0)).loadTermDepositBaseProductListToCache(any(ServiceErrors.class));
    }

    @Test
    public void testLoadTermDepositRatesForOldFDDList(){
        dataInitialization.loadTermDepositProductRates();
        verify(rateIntegrationService,times(1)).clearCache();
        verify(rateIntegrationService,times(0)).loadTermDepositAssetsToCache(any(ServiceErrors.class));
        verify(rateIntegrationService,times(1)).loadTermDepositBaseProductListToCache(any(ServiceErrors.class));
    }

    @Test(expected = Exception.class)
    public void testLoadTermDepositAssetRatesException() {
        doThrow(new Exception()).when(rateIntegrationService).loadTermDepositAssetsToCache(any(ServiceErrors.class));
        dataInitialization.loadTermDepositAssetRates();
    }

    @Test(expected = Exception.class)
    public void testLoadTermDepositProductRatesException() {
        doThrow(new Exception()).when(rateIntegrationService).loadTermDepositBaseProductListToCache(any(ServiceErrors.class));
        dataInitialization.loadTermDepositProductRates();
    }

    @Test(expected = Exception.class)
    public void testLoadBrokersException() {
        doThrow(new Exception()).when(paginatedBrokerIntegrationService).populatePaginatedBrokerCache(any(ServiceErrors.class));
        dataInitialization.loadBrokers();
    }
}
