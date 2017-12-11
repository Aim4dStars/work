package com.bt.nextgen.api.overview.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.overview.model.CacheRefreshDto;
import com.bt.nextgen.core.session.SessionUtils;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationType;
import com.bt.nextgen.service.integration.overview.service.AccountOverviewCacheManagementIntegrationService;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountOverviewCacheManagementDtoServiceImpl implements AccountOverviewCacheManagementDtoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountOverviewCacheManagementDtoServiceImpl.class);

    @Autowired
    private AccountOverviewCacheManagementIntegrationService accountOverviewCacheManagementService;

    // need to catch Exception to set error in DTO
    @SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck")
    @Override
    public CacheRefreshDto find(AccountKey key, ServiceErrors serviceErrors) {
        CacheRefreshDto refreshStatusDto = new CacheRefreshDto();

        try {
            final com.bt.nextgen.service.integration.account.AccountKey integrationAccountKey
                    = com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
            final com.bt.nextgen.api.account.v3.model.AccountKey accountKey
                    = new com.bt.nextgen.api.account.v3.model.AccountKey(EncodedString.toPlainText(key.getAccountId()));
            final String accountId = new EncodedString(key.getAccountId()).plainText();
            final WrapAccountIdentifier wrapAccountIdentifier = new WrapAccountIdentifierImpl();
            wrapAccountIdentifier.setBpId(accountId);

            accountOverviewCacheManagementService.clearCache(integrationAccountKey);
            accountOverviewCacheManagementService.clearCache(accountKey);
            accountOverviewCacheManagementService.clearCategorisationCache(integrationAccountKey, null);
            accountOverviewCacheManagementService.clearCategorisationCache(integrationAccountKey, CashCategorisationType.PENSION);
            accountOverviewCacheManagementService.clearCashTransactionCache(wrapAccountIdentifier);
            refreshStatusDto.setStatus("success");
            refreshStatusDto.setCacheLastRefreshedDatetime(DateTime.now());

            SessionUtils.getSession().setAttribute("overviewCacheLastRefreshedDatetime", DateTime.now());
        }
        catch (Exception e) {
            LOGGER.error("Failed to refresh caches", e);
            refreshStatusDto.setStatus("error");
        }

        return refreshStatusDto;
    }
}