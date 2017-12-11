package com.bt.nextgen.api.product.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.product.v1.model.AccountProductDocumentDto;
import com.bt.nextgen.api.product.v1.model.UnkeyedProductDocumentDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.is;

/**
 * The Class AccountProductDocumentDtoServiceImpl.
 */
@Service("AccountProductDocumentSericeV1")
public class AccountProductDocumentDtoServiceImpl extends UnkeyedProductDocumentDtoService
        implements AccountProductDocumentDtoService {

    /**
     * The account service.
     */
    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    /*
     * (non-Javadoc)
     * 
     * @see com.bt.nextgen.core.api.dto.FindByKeyDtoService#find(java.lang.Object, com.bt.nextgen.service.ServiceErrors)
     */
    @Override
    public AccountProductDocumentDto find(final AccountKey accountKey, final ServiceErrors serviceErrors) {
        final String accountId = EncodedString.toPlainText(accountKey.getAccountId());
        final WrapAccount account = accountService.loadWrapAccountWithoutContainers(com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountId), serviceErrors);
        final Product whiteLabelProduct = productIntegrationService.getProductDetail(account.getProductKey(), serviceErrors);
        final Collection<Product> availableProducts = productIntegrationService.loadProducts(serviceErrors);
        final Broker dealerGroup = brokerHelperService.getDealerGroupForInvestor(account, serviceErrors);
        final List<Product> dealerGroupProductList = productIntegrationService.getDealerGroupProductList(dealerGroup.getDealerKey(), serviceErrors);
        final List<Product> accountProductList = new ArrayList<>();
        final List<Product> offers = filter(having(on(Product.class).getParentProductKey(), is(whiteLabelProduct.getProductKey())), dealerGroupProductList);
        accountProductList.add(whiteLabelProduct);
        accountProductList.addAll(offers);

        final UnkeyedProductDocumentDto unkeyedProductDocumentDto = getDocumentList(dealerGroup.getDealerKey(), accountProductList, availableProducts, serviceErrors);
        return new AccountProductDocumentDto(accountKey, unkeyedProductDocumentDto.getProductList(),
                unkeyedProductDocumentDto.getBrandList(), unkeyedProductDocumentDto.getDocumentTags(),
                unkeyedProductDocumentDto.isManagedFundAvailable());
    }
}
