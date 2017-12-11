package com.bt.nextgen.api.order.service.helper;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.order.OrderItem;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AssetHelperTest {
    @InjectMocks
    private AssetHelper helper;

    @Mock
    protected BrokerIntegrationService brokerService;

    @Mock
    protected AccountIntegrationService accountIntegrationService;

    @Mock
    protected AssetDtoConverter assetDtoConverter;

    @Mock
    protected AssetIntegrationService assetService;

    WrapAccountDetail account;
    Broker broker;

    @Before
    public void setup() throws Exception {
        account = Mockito.mock(WrapAccountDetail.class);
        Mockito.when(account.getAdviserKey()).thenReturn(BrokerKey.valueOf("adviser key"));

        broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getDealerKey()).thenReturn(BrokerKey.valueOf("dealer key"));

        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(account);
        Mockito.when(brokerService.getBroker(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(broker);
        Mockito.when(assetService.loadAssets(Mockito.anyList(), Mockito.any(ServiceErrors.class)))
                .thenReturn(new HashMap<String, Asset>());
        Mockito.when(assetService.loadTermDepositRates(Mockito.any(BrokerKey.class), Mockito.any(DateTime.class),
                Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(new HashMap<String, TermDepositAssetDetail>());
        Mockito.when(assetDtoConverter.toAssetDto(Mockito.anyMap(), Mockito.anyMap()))
                .thenReturn(new HashMap<String, AssetDto>());
    }

    @Test
    public void testGetAssetsForOrders_whenOrdersPassed_thenAssetsLoaded() {
        OrderItem item = Mockito.mock(OrderItem.class);
        Mockito.when(item.getAssetId()).thenReturn("bhp");
        List<OrderItem> orders = new ArrayList<>();
        orders.add(item);

        helper.getAssetsForOrders(AccountKey.valueOf("account"), orders, new ServiceErrorsImpl());

        ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        verify(assetService, atLeastOnce()).loadAssets(argument.capture(), Mockito.any(ServiceErrors.class));
        Assert.assertEquals("bhp", argument.getValue().get(0));
    }
}
