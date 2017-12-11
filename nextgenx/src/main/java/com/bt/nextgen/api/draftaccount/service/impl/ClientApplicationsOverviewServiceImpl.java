package com.bt.nextgen.api.draftaccount.service.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.account.v3.util.AccountProductsHelper;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.draftaccount.model.ClientOverviewDto;
import com.bt.nextgen.api.draftaccount.service.ClientApplicationsOverviewService;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.not;

@SuppressWarnings("squid:S1200")
@Service
public class ClientApplicationsOverviewServiceImpl implements ClientApplicationsOverviewService {

    @Autowired
    private InvestorProfileService profileService;

    @Autowired
    private AccActivationIntegrationService accActivationIntegrationService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private BrokerHelperService brokerHelperService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private AccountProductsHelper accountProductsHelper;

    @Override
    public List<ClientOverviewDto> findAll(ServiceErrors serviceErrors) {
        List<WrapAccount> wrapAccounts = getWrapAccountsForClient(serviceErrors);
        Map<String, AccountBalance> accountBalanceMap = getAccountBalanceMap(serviceErrors);
        return mapToClientOverviewDto(wrapAccounts, accountBalanceMap, serviceErrors);
    }

    private Map<String, AccountBalance> getAccountBalanceMap(ServiceErrors serviceErrors) {
        List<AccountBalance> accountBalanceList = accountService.loadAccountBalances(serviceErrors);
        Map<String, AccountBalance> accountBalanceMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(accountBalanceList)) {
            accountBalanceMap = Lambda.index(accountBalanceList, on(AccountBalance.class).getKey().getId());
        }
        return accountBalanceMap;
    }

    private List<WrapAccount> getWrapAccountsForClient(ServiceErrors serviceErrors) {
        Collection<WrapAccount> accountList = accountService.loadOnlineWrapAccounts(serviceErrors); //US18507 - exclude offline accounts
        List<WrapAccount> pendingAccounts = Lambda.filter(having(on(WrapAccount.class).getAccountStatus(),
            not(anyOf(Matchers.equalTo(AccountStatus.DISCARD), Matchers.equalTo(AccountStatus.COMPANY_SETUP_IN_PROGRESS)))), accountList);
        return Lambda.sort(pendingAccounts, on(WrapAccount.class).getAccountName().toLowerCase());
    }

    private List<ClientOverviewDto> mapToClientOverviewDto(Collection<WrapAccount> accounts,
                                                           final Map<String, AccountBalance> accountBalanceMap,
                                                           final ServiceErrors serviceErrors) {
        final ClientKey clientKey = profileService.getActiveProfile().getClientKey();
        return Lambda.convert(accounts, new Converter<WrapAccount, ClientOverviewDto>() {
            @Override
            public ClientOverviewDto convert(WrapAccount account) {
                Product product = productIntegrationService.getProductDetail(account.getProductKey(), serviceErrors);
                AccountBalance accountBalance = accountBalanceMap.get(account.getAccountKey().getId());
                BigDecimal availableCash = new BigDecimal(0);
                BigDecimal portfolioValue = new BigDecimal(0);

                if (accountBalance != null) {
                    availableCash = accountBalance.getAvailableCash();
                    portfolioValue = accountBalance.getPortfolioValue();
                }
                final UserExperience userExperience = brokerHelperService.getUserExperience(account, serviceErrors);
                final String accountType = getAccountTypeForDisplay(account);
                final String featureKey = accountProductsHelper.getAccountFeatureKey(account, serviceErrors);

                return new ClientOverviewDto(account, accountType, getStatus(account, clientKey, serviceErrors), product.getProductName(),
                        availableCash, portfolioValue, EncodedString.fromPlainText(clientKey.getId()).toString(), userExperience, featureKey);
            }
        });
    }

    private String getAccountTypeForDisplay(WrapAccount account) {
        if (AccountStructureType.SUPER == account.getAccountStructureType()) {
            return account.getSuperAccountSubType().getAccountType();
        }
        return account.getAccountStructureType().name();
    }

    private ClientOverviewDto.Status getStatus(WrapAccount account, ClientKey clientKey, ServiceErrors serviceErrors) {
        ClientOverviewDto.Status status;

        switch (account.getAccountStatus()) {
            case ACTIVE:
                status = ClientOverviewDto.Status.ACTIVE;
                break;
            case CLOSE:
            case PEND_CLOSE:
            case PEND_CLOSE_CASHOUT:
            case PEND_CLOSE_FEES:
            case PEND_CLOSE_INTR:
                status = ClientOverviewDto.Status.CLOSED;
                break;
            case FUND_ESTABLISHMENT_IN_PROGRESS:
                status = ClientOverviewDto.Status.FUND_ESTABLISHMENT_IN_PROGRESS;
                break;
            case PEND_OPN:
            case FUND_ESTABLISHMENT_PENDING:
                AssociatedPerson client = getAssociatedPersonFromAccount(account, clientKey, serviceErrors);
                status = !client.isHasToAcceptTnC() || client.isHasApprovedTnC() ? ClientOverviewDto.Status.PENDING_APPROVAL
                    : ClientOverviewDto.Status.PENDING_YOUR_APPROVAL;
                break;
            default:
                status = ClientOverviewDto.Status.UNKNOWN;
                break;
        }
        return status;
    }

    private AssociatedPerson getAssociatedPersonFromAccount(final WrapAccount account, final ClientKey clientKey,
                                                            final ServiceErrors serviceErrors) {
        WrapAccountIdentifier wrapAccountIdentifier = new WrapAccountIdentifierImpl();
        wrapAccountIdentifier.setBpId(account.getAccountKey().getId());
        return accActivationIntegrationService.loadAccApplicationForPortfolio(wrapAccountIdentifier, clientKey,userProfileService.getActiveProfile().getJobRole(), serviceErrors);
    }
}
