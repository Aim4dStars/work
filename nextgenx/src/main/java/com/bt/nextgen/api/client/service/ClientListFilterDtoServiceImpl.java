package com.bt.nextgen.api.client.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.adviser.model.AdviserSearchDto;
import com.bt.nextgen.api.client.model.FilterDto;
import com.bt.nextgen.api.product.model.ProductDto;
import com.bt.nextgen.api.product.service.ProductDtoConverter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
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

@Service
public class ClientListFilterDtoServiceImpl implements ClientListFilterDtoService{

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private ProductDtoConverter productDtoConverter;

    @Override
    public FilterDto findOne(ServiceErrors serviceErrors) {
        Set<ProductKey> products = new HashSet<>();
        Set<BrokerKey> advisers = new HashSet<>();
        List<ProductDto> productList = new ArrayList<>();
        List<AdviserSearchDto> adviserList = new ArrayList<>();
        Collection<WrapAccount> accounts = accountService.loadWrapAccountWithoutContainers(serviceErrors).values();
        for (WrapAccount wrapAccount : accounts){
            if (products.add(wrapAccount.getProductKey())){
                productList.add(productDtoConverter.convert(productIntegrationService.getProductDetail(wrapAccount.getProductKey(), serviceErrors)));
            }
            if (advisers.add(wrapAccount.getAdviserPositionId())){
                AdviserSearchDto adviserSearchDto = new AdviserSearchDto();
                BrokerUser adviser = brokerIntegrationService.getAdviserBrokerUser(wrapAccount.getAdviserPositionId(),serviceErrors);
                adviserSearchDto.setFirstName(adviser.getFirstName());
                adviserSearchDto.setLastName(adviser.getLastName());
                adviserList.add(adviserSearchDto);
            }
        }
        productList = Lambda.sort(productList, Lambda.on(ProductDto.class).getProductName());
        return new FilterDto(adviserList, productList);
    }
}
