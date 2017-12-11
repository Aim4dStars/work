package com.bt.nextgen.api.product.service;

import com.bt.nextgen.api.product.model.ProductDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductSearchDtoServiceImplTest {

    @InjectMocks
    ProductSearchDtoServiceImpl productSearchDtoService;

    @Mock
    AccountIntegrationService accountService;

    @Mock
    ProductIntegrationService productIntegrationService;

    @Mock
    private ProductDtoConverter productDtoConverter;

    Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
    ProductImpl product = new ProductImpl();

    @Before
    public void setUp(){
        AccountKey accountKey1 = AccountKey.valueOf("74611");
        ProductKey productKey1 = ProductKey.valueOf("68445");
        WrapAccountImpl wrapAccount1 = new WrapAccountImpl();
        wrapAccount1.setAccountKey(accountKey1);
        wrapAccount1.setProductKey(productKey1);
        accountMap.put(accountKey1, wrapAccount1);

        AccountKey accountKey2 = AccountKey.valueOf("74643");
        ProductKey productKey2 = ProductKey.valueOf("68445");
        WrapAccountImpl wrapAccount2 = new WrapAccountImpl();
        wrapAccount2.setAccountKey(accountKey2);
        wrapAccount2.setProductKey(productKey2);
        accountMap.put(accountKey2, wrapAccount2);

        product.setProductKey(productKey1);
        product.setProductName("White Label 060f52dc6d17421eaf1632ac9efae210");

        ProductDto dto = new ProductDto();
        dto.setKey(new com.bt.nextgen.api.product.model.ProductKey(EncodedString.fromPlainText(productKey1.getId()).toString()));
        dto.setProductName("White Label 060f52dc6d17421eaf1632ac9efae210");
        when(productDtoConverter.convert((Product) anyObject())).thenReturn(dto);
    }

    @Test
    public void findAllTest(){
        when(accountService.loadWrapAccountWithoutContainers((ServiceErrors) Matchers.anyObject())).thenReturn(accountMap);
        when(productIntegrationService.getProductDetail((ProductKey)Matchers.anyObject(),(ServiceErrors) Matchers.anyObject())).thenReturn(product);
        List<ProductDto> response = productSearchDtoService.findAll(new ServiceErrorsImpl());
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(new EncodedString(response.get(0).getKey().getProductId()).plainText(),"68445");
        assertEquals(response.get(0).getProductName(),"White Label 060f52dc6d17421eaf1632ac9efae210");
    }
}
