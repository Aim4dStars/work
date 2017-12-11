package com.bt.nextgen.service.integration.options.service;

import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.btfin.panorama.service.integration.broker.ExternalBrokerKey;
import com.bt.nextgen.service.integration.options.model.CategoryKey;
import com.bt.nextgen.service.integration.options.model.CategoryType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class BrokerHierarchyFactoryTest {
    @InjectMocks
    public BrokerHierarchyFactory brokerFactory;

    @Mock
    public BrokerIntegrationService brokerService;

    @Before
    public void setUp() {
        final Broker adviser = Mockito.mock(Broker.class);
        Mockito.when(adviser.getBrokerType()).thenReturn(BrokerType.ADVISER);
        Mockito.when(adviser.getParentKey()).thenReturn(BrokerKey.valueOf("officeKey"));
        Mockito.when(adviser.getExternalBrokerKey()).thenReturn(ExternalBrokerKey.valueOf("id.adv"));

        final Broker office = Mockito.mock(Broker.class);
        Mockito.when(office.getBrokerType()).thenReturn(BrokerType.OFFICE);
        Mockito.when(office.getParentKey()).thenReturn(BrokerKey.valueOf("practiceKey"));
        Mockito.when(office.getExternalBrokerKey()).thenReturn(ExternalBrokerKey.valueOf("id.ofc"));

        final Broker practice = Mockito.mock(Broker.class);
        Mockito.when(practice.getBrokerType()).thenReturn(BrokerType.PRACTICE);
        Mockito.when(practice.getParentKey()).thenReturn(BrokerKey.valueOf("dealerKey"));
        Mockito.when(practice.getExternalBrokerKey()).thenReturn(ExternalBrokerKey.valueOf("id.prc"));

        final Broker dealer = Mockito.mock(Broker.class);
        Mockito.when(dealer.getBrokerType()).thenReturn(BrokerType.DEALER);
        Mockito.when(dealer.getParentKey()).thenReturn(BrokerKey.valueOf("sDealerKey"));
        Mockito.when(dealer.getExternalBrokerKey()).thenReturn(ExternalBrokerKey.valueOf("id.dlr"));

        final Broker sDealer = Mockito.mock(Broker.class);
        Mockito.when(sDealer.getBrokerType()).thenReturn(BrokerType.SUPER_DEALER);
        Mockito.when(sDealer.getExternalBrokerKey()).thenReturn(ExternalBrokerKey.valueOf("id.sdlr"));

        final Broker adv2 = Mockito.mock(Broker.class);
        Mockito.when(adv2.getBrokerType()).thenReturn(BrokerType.ADVISER);
        Mockito.when(adv2.getParentKey()).thenReturn(BrokerKey.valueOf("dealerKey"));
        Mockito.when(adv2.getExternalBrokerKey()).thenReturn(ExternalBrokerKey.valueOf("id.sdlr"));

        Mockito.when(brokerService.getBroker(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<Broker>() {
                    @Override
                    public Broker answer(InvocationOnMock invocation) throws Throwable {
                        String brokerId = ((BrokerKey) invocation.getArguments()[0]).getId();
                        if ("adviserKey".equals(brokerId)) {
                            return adviser;
                        }
                        if ("adviserKey2".equals(brokerId)) {
                            return adv2;
                        }
                        if ("officeKey".equals(brokerId)) {
                            return office;
                        }
                        if ("practiceKey".equals(brokerId)) {
                            return practice;
                        }
                        if ("dealerKey".equals(brokerId)) {
                            return dealer;
                        }
                        if ("sDealerKey".equals(brokerId)) {
                            return sDealer;
                        }
                        return null;
                    }
                });
    }

    @Test
    public void testBuildHierarchy_whenInvokedWithAFullHierarchy_thenTheHierarchyIsOrderedCorrectly() {
        List<CategoryKey> categories = brokerFactory.buildHierarchy(BrokerKey.valueOf("adviserKey"), new FailFastErrorsImpl());
        Assert.assertEquals(5, categories.size());
        Assert.assertEquals(CategoryType.ADVISER, categories.get(0).getCategory());
        Assert.assertEquals(CategoryType.OFFICE, categories.get(1).getCategory());
        Assert.assertEquals(CategoryType.PRACTICE, categories.get(2).getCategory());
        Assert.assertEquals(CategoryType.DEALER, categories.get(3).getCategory());
        Assert.assertEquals(CategoryType.SUPER_DEALER, categories.get(4).getCategory());
        Assert.assertEquals("id.adv", categories.get(0).getCategoryId());
        Assert.assertEquals("id.ofc", categories.get(1).getCategoryId());
        Assert.assertEquals("id.prc", categories.get(2).getCategoryId());
        Assert.assertEquals("id.dlr", categories.get(3).getCategoryId());
        Assert.assertEquals("id.sdlr", categories.get(4).getCategoryId());
    }


}
