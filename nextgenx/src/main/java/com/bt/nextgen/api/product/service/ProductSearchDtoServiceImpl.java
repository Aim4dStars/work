package com.bt.nextgen.api.product.service;

import com.bt.nextgen.api.product.model.ProductDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Deprecated
@Service
public class ProductSearchDtoServiceImpl implements ProductSearchDtoService {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private ProductDtoConverter productDtoConverter;

    @Override
    public List<ProductDto> findAll(ServiceErrors serviceErrors) {
        final Collection<WrapAccount> accounts = accountService.loadWrapAccountWithoutContainers(serviceErrors).values();
        final Set<ProductKey> productKeys = new HashSet<>();
        final List<ProductDto> productList = new ArrayList<>(accounts.size());
        for (WrapAccount wrapAccount : accounts){
            final ProductKey productKey = wrapAccount.getProductKey();
            if (productKeys.add(productKey)){
                productList.add(productDtoConverter.convert(productIntegrationService.getProductDetail(productKey, serviceErrors)));
            }
        }
        return productList;
    }
}
