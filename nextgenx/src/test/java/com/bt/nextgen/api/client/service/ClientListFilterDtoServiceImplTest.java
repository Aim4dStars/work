package com.bt.nextgen.api.client.service;

import com.bt.nextgen.api.client.model.FilterDto;
import com.bt.nextgen.api.product.model.ProductDto;
import com.bt.nextgen.api.product.service.ProductDtoConverter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ClientListFilterDtoServiceImplTest {
    @InjectMocks
    private ClientListFilterDtoServiceImpl dtoService;
    
    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private ProductDtoConverter productDtoConverter;

    @Test
    public void findOne_whenNoAccounts_productsAreEmpty() {
        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        ServiceErrors errors = new FailFastErrorsImpl();
        when(accountService.loadWrapAccountWithoutContainers(errors)).thenReturn(accountMap);
        FilterDto result = dtoService.findOne(errors);
        assertNotNull(result.getProducts());
        assertEquals(0, result.getProducts().size());
    }

    @Test
    public void findOne_whenAccounts_productsAreSortedByName() {
        ProductKey productKey1 = ProductKey.valueOf("product1");
        ProductKey productKey2 = ProductKey.valueOf("product2");

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccount account = mock(WrapAccount.class);
        when(account.getProductKey()).thenReturn(productKey1);
        accountMap.put(AccountKey.valueOf("1"), account);
        account = mock(WrapAccount.class);
        when(account.getProductKey()).thenReturn(productKey2);
        accountMap.put(AccountKey.valueOf("2"), account);

        ServiceErrors errors = new FailFastErrorsImpl();
        when(accountService.loadWrapAccountWithoutContainers(errors)).thenReturn(accountMap);
        when(brokerIntegrationService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class)))
                .thenReturn(mock(BrokerUser.class));
        
        
        Product product1 = mock(Product.class);
        Product product2 = mock(Product.class);

        Mockito.when(productIntegrationService.getProductDetail(eq(productKey1), any(ServiceErrors.class))).thenReturn(product1);
        Mockito.when(productIntegrationService.getProductDetail(eq(productKey2), any(ServiceErrors.class))).thenReturn(product2);

        ProductDto dto1 = mock(ProductDto.class);
        when(dto1.getProductName()).thenReturn("zzzzzz");

        ProductDto dto2 = mock(ProductDto.class);
        when(dto2.getProductName()).thenReturn("aaaaaa");

        Mockito.when(productDtoConverter.convert(eq(product1))).thenReturn(dto1);
        Mockito.when(productDtoConverter.convert(eq(product2))).thenReturn(dto2);

        FilterDto result = dtoService.findOne(errors);
        assertNotNull(result.getProducts());
        assertEquals(2, result.getProducts().size());
        assertEquals("aaaaaa", result.getProducts().get(0).getProductName());
        assertEquals("zzzzzz", result.getProducts().get(1).getProductName());

    }
}
