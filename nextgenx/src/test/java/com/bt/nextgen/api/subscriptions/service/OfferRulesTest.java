package com.bt.nextgen.api.subscriptions.service;

import ch.lambdaj.function.matcher.Predicate;
import com.bt.nextgen.api.subscriptions.model.Offer;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by L062329 on 22/11/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class OfferRulesTest {

    @InjectMocks
    private OfferRules  rules;

    @Mock
    AccountIntegrationService accountIntegrationService;

    @Mock
    BrokerIntegrationService brokerIntegrationService;

    @Mock
    ProductIntegrationService productIntegrationService;

    @Before
    public void setUp() {

        WrapAccountDetailImpl wrapAccount=new WrapAccountDetailImpl();
        wrapAccount.setAccountStructureType(AccountStructureType.SMSF);
        wrapAccount.setAdviserPositionId(BrokerKey.valueOf("1234"));

        ProductImpl product=new ProductImpl();
        product.setProductName("BT Fund Administration");
        List<Product> products=new ArrayList<>();
        products.add(product);

        BrokerImpl broker=new BrokerImpl(BrokerKey.valueOf("1234"), BrokerType.ADVISER);
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers((AccountKey) Matchers.anyObject(),
                (ServiceErrorsImpl)Matchers.anyObject())).thenReturn(wrapAccount);
        Mockito.when(brokerIntegrationService.getBroker((BrokerKey)Matchers.anyObject(),
                (ServiceErrorsImpl)Matchers.anyObject())).thenReturn(broker);
        Mockito.when(productIntegrationService.getSubscriptionProducts((BrokerKey)Matchers.anyObject(),
                (ServiceErrorsImpl)Matchers.anyObject())).thenReturn(products);

    }

    @Test
    public void testGetPredicate() throws Exception {

        WrapAccountIdentifier id = new WrapAccountIdentifierImpl();
        id.setBpId("test");
        Predicate predicate = rules.getPredicate(Subscriptions.FA.getOffer(), id);
        assertTrue(predicate.apply(Subscriptions.FA.getOffer()));

        Offer offer = new Offer(null);
        predicate = rules.getPredicate(offer, id);
        assertFalse(predicate.apply(offer));
    }

    @Test
    public void testFundAdminPredicate() throws Exception {
        WrapAccountIdentifier id = new WrapAccountIdentifierImpl();
        id.setBpId("test");
        WrapAccountDetailImpl wrapAccount=new WrapAccountDetailImpl();
        wrapAccount.setAccountStructureType(AccountStructureType.SMSF);
        wrapAccount.setAdviserPositionId(BrokerKey.valueOf("1234"));

        ProductImpl product=new ProductImpl();
        product.setProductName("BT Fund Administration");
        List<Product> products=new ArrayList<>();
        products.add(product);

        BrokerImpl broker=new BrokerImpl(BrokerKey.valueOf("1234"), BrokerType.ADVISER);
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers((AccountKey) Matchers.anyObject(),
                (ServiceErrorsImpl)Matchers.anyObject())).thenReturn(wrapAccount);
        Mockito.when(brokerIntegrationService.getBroker((BrokerKey)Matchers.anyObject(),
                (ServiceErrorsImpl)Matchers.anyObject())).thenReturn(broker);
        Mockito.when(productIntegrationService.getSubscriptionProducts((BrokerKey)Matchers.anyObject(),
                (ServiceErrorsImpl)Matchers.anyObject())).thenReturn(products);

        Predicate predicate=rules.fundAdminPredicate(id);
        Offer offer=new Offer(Subscriptions.FA);
        assertTrue(predicate.apply(offer));

        ProductImpl product1=new ProductImpl();
        product.setProductName("BT Fund Administration");
        List<Product> products1=new ArrayList<>();
        products1.add(product1);

        Mockito.when(productIntegrationService.getSubscriptionProducts((BrokerKey)Matchers.anyObject(),
                (ServiceErrorsImpl)Matchers.anyObject())).thenReturn(products1);

         predicate=rules.fundAdminPredicate(id);
         offer=new Offer(Subscriptions.FA);
        assertFalse(predicate.apply(offer));


    }

    @Test
    public void testDefaultPredicate() throws Exception {
        //TODO:  write test case
    }
}