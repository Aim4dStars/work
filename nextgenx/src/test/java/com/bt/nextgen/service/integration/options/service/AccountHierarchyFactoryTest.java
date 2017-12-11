package com.bt.nextgen.service.integration.options.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.ProductSubscription;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.options.model.CategoryKey;
import com.bt.nextgen.service.integration.options.model.CategoryType;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.joda.time.DateTime;
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

import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AccountHierarchyFactoryTest {
    @Mock
    private ProductHierarchyFactory productFactory;

    @InjectMocks
    private AccountHierarchyFactory accountFactory;

    @Mock
    private AccountIntegrationService accountService;

    @Before
    public void setUp() {
        ProductSubscription productSubscription = Mockito.mock(ProductSubscription.class);
        Mockito.when(productSubscription.getSubscribedDateTo()).thenReturn(DateTime.now().plusDays(1));
        Mockito.when(productSubscription.getSubscribedProductId()).thenReturn("productKey");

        WrapAccount account = Mockito.mock(WrapAccount.class);
        Mockito.when(account.getProductKey()).thenReturn(ProductKey.valueOf("productKey"));
        Mockito.when(account.getAccountStructureType()).thenReturn(AccountStructureType.SMSF);

        Mockito.when(
                accountService.loadWrapAccountWithoutContainers(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(account);
    }

    @Test
    public void testBuildHierarchy_whenInvokedAnAccount_thenAccountsProductIsSentToTheProductFactory() {

        final MutableBoolean productCalled = new MutableBoolean(false);

        Mockito.when(productFactory.buildHierarchy(Mockito.any(ProductKey.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<CategoryKey>>() {
                    @Override
                    public List<CategoryKey> answer(InvocationOnMock invocation) throws Throwable {
                        String productId = ((ProductKey) invocation.getArguments()[0]).getId();
                        if ("productKey".equals(productId)) {
                            productCalled.setValue(true);
                        }
                        return Collections.emptyList();
                    }
                });

        accountFactory.buildHierarchy(AccountKey.valueOf("accountKey"), new FailFastErrorsImpl());
        Assert.assertTrue(productCalled.getValue());
    }

    @Test
    public void testBuildHierarchy_testBuildHierarchy_whenInvokedAnAccount_thenAccountsStructureIsTheFirstCategory() {
        List<CategoryKey> categories = accountFactory.buildHierarchy(AccountKey.valueOf("accountKey"), new FailFastErrorsImpl());
        Assert.assertEquals(1, categories.size());
        Assert.assertEquals(CategoryType.STRUCTURE, categories.get(0).getCategory());
        Assert.assertEquals("SMSF", categories.get(0).getCategoryId());
    }

    @Test
    public void testBuildHierarchy_testBuildHierarchy_whenInvokedASuperAccount_thenAccountsSubTypeIsTheFirstCategory() {
        WrapAccount account = Mockito.mock(WrapAccount.class);
        Mockito.when(account.getProductKey()).thenReturn(ProductKey.valueOf("productKey"));
        Mockito.when(account.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        Mockito.when(account.getSuperAccountSubType()).thenReturn(AccountSubType.PENSION);
        Mockito.when(
                accountService.loadWrapAccountWithoutContainers(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(account);

        List<CategoryKey> categories = accountFactory.buildHierarchy(AccountKey.valueOf("accountKey"), new FailFastErrorsImpl());
        Assert.assertEquals(2, categories.size());
        Assert.assertEquals(CategoryType.ACCOUNT_SUB_TYPE, categories.get(0).getCategory());
        Assert.assertEquals("PENSION", categories.get(0).getCategoryId());
    }

}
