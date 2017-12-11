package com.bt.nextgen.api.order.service.helper;

import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.user.UserKey;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BrokerHelperTest {
    @InjectMocks
    private BrokerHelper helper;

    @Mock
    protected BrokerIntegrationService brokerService;

    @Mock
    protected AccountIntegrationService accountIntegrationService;

    @Mock
    protected AssetDtoConverter assetDtoConverter;

    @Mock
    protected AssetIntegrationService assetService;

    BrokerUser brokerUser;

    @Test
    public void testGetAdviserFullName_whenBroker_thenNameReturned() {
        brokerUser = Mockito.mock(BrokerUser.class);
        Mockito.when(brokerUser.getFirstName()).thenReturn("firsty");
        Mockito.when(brokerUser.getLastName()).thenReturn("lastly");

        Mockito.when(brokerService.getBrokerUser(Mockito.any(UserKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(brokerUser);

        String name = helper.getAdviserFullName("owner", new ServiceErrorsImpl());
        Assert.assertEquals(brokerUser.getFirstName() + " " + brokerUser.getLastName(), name);
    }

    @Test
    public void testGetAdviserFullName_whenBrokerNull_thenEmptyStringReturned() {
        String name = helper.getAdviserFullName("owner", new ServiceErrorsImpl());
        Assert.assertEquals("", name);
    }
}
