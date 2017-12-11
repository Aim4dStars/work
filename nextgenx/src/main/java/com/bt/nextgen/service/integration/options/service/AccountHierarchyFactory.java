package com.bt.nextgen.service.integration.options.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.options.model.CategoryKey;
import com.bt.nextgen.service.integration.options.model.CategoryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccountHierarchyFactory implements HierarchyFactory<AccountKey> {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private ProductHierarchyFactory productHierarchyFactory;

    @Override
    public List<CategoryKey> buildHierarchy(AccountKey accountKey, ServiceErrors serviceErrors) {
        final List<CategoryKey> hierarchy = new ArrayList<>();
        final WrapAccount account = accountService.loadWrapAccountWithoutContainers(accountKey, serviceErrors);
        if (account.getSuperAccountSubType() != null) {
            hierarchy.add(CategoryKey.valueOf(CategoryType.ACCOUNT_SUB_TYPE, account.getSuperAccountSubType().name()));
        }
        hierarchy.add(CategoryKey.valueOf(CategoryType.STRUCTURE, account.getAccountStructureType().name()));
        hierarchy.addAll(productHierarchyFactory.buildHierarchy(account.getProductKey(), serviceErrors));
        return hierarchy;
    }
}
