package com.bt.nextgen.api.account.v3.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.account.v3.model.AccountCashSweepDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.util.AccountProductsHelper;
import com.bt.nextgen.api.account.v3.util.AccountSubscriptionUtil;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AvaloqContainerIntegrationService;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.account.UpdateCashSweepAccountResponse;
import com.bt.nextgen.service.integration.account.UpdateCashSweepInvestmentResponse;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Implementation for {@link AccountCashSweepDtoService}
 */
@Service("AccountCashSweepDtoServiceV3")
public class AccountCashSweepDtoServiceImpl implements AccountCashSweepDtoService {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    @Qualifier("avaloqContainerIntegrationService")
    private AvaloqContainerIntegrationService containerIntegrationService;

    @Autowired
    private AccountProductsHelper accountProductsHelper;

    @Autowired
    private CmsService cmsService;

    private static final Logger logger = LoggerFactory.getLogger(AccountCashSweepDtoServiceImpl.class);
    private static final String ACCOUNT_UPDATE_ERROR = "Err.IP-0934";
    private static final String INVESTMENTS_UPDATE_ERROR = "Err.IP-0933";

    /**
     * Gets the cash sweep details for the account
     *
     * @param accountKey    - Account identifier
     * @param serviceErrors - Error object to capture service level errors
     */
    @Override
    public AccountCashSweepDto find(AccountKey accountKey, ServiceErrors serviceErrors) {
        final com.bt.nextgen.service.integration.account.AccountKey key =
                com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString.toPlainText(accountKey.getAccountId()));
        final WrapAccountDetailImpl accountDetail = (WrapAccountDetailImpl) accountIntegrationService.loadWrapAccountDetail(key, serviceErrors);

        if (accountDetail != null) {
            final SubAccountKey directSubAccountKey = getDirectSubAccountKey(accountDetail);
            if (directSubAccountKey != null) {
                final SubAccount subAcctDetail = containerIntegrationService.loadSubAccountDetails(directSubAccountKey.getId(), serviceErrors);
                if (subAcctDetail != null) {
                    return new AccountCashSweepDto(accountKey, accountDetail.getMinCashAmount(), accountDetail.isCashSweepApplied(), accountDetail.getMinCashSweepAmount(),
                            accountProductsHelper.getCashSweepAssets(key, subAcctDetail, serviceErrors));
                }
            }
        }
        return null;
    }

    /**
     * Updates the cash sweep details for the account
     *
     * @param accountCashSweepDto - Object for the cash sweep details to be updated
     * @param serviceErrors       - Error object to capture service level errors
     */
    @Override
    public AccountCashSweepDto update(AccountCashSweepDto accountCashSweepDto, ServiceErrors serviceErrors) {
        final com.bt.nextgen.service.integration.account.AccountKey accountKey =
                com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString.toPlainText(accountCashSweepDto.getKey().getAccountId()));
        final AccountCashSweepDto updatedCashSweepDto = new AccountCashSweepDto(accountCashSweepDto.getKey());
        final WrapAccountDetail accountDetail = accountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);

        if (accountDetail != null) {
            // Not using FailFastError because we need to capture the error for displaying on UI
            final ServiceErrors domainErrors = new ServiceErrorsImpl();
            updateCashSweepInvestmentDetails(accountDetail, accountCashSweepDto, updatedCashSweepDto, domainErrors);
            if (!domainErrors.hasErrors()) {
                updateCashSweepAccountDetails(accountDetail, accountCashSweepDto, updatedCashSweepDto, domainErrors);
            }
        }
        return updatedCashSweepDto;
    }

    /**
     * Updates the flag and the balances for the cash sweep for an account.
     *
     * @param wrapAccountDetail   -  Account details see:{@link WrapAccountDetail}
     * @param accountCashSweepDto - Object for the cash sweep details to be updated
     * @param updatedCashSweepDto - updated  cash sweep object for UI
     * @param serviceErrors       - Error object to capture service level errors
     */
    private void updateCashSweepAccountDetails(WrapAccountDetail wrapAccountDetail, AccountCashSweepDto accountCashSweepDto,
                                               AccountCashSweepDto updatedCashSweepDto, ServiceErrors serviceErrors) {

        if (accountCashSweepDto.isCashSweepAllowed() != null || accountCashSweepDto.getMinCashSweepAmount() != null) {
            final UpdateCashSweepAccountResponse response = accountIntegrationService.updateCashSweepAccountDetails(
                    AccountSubscriptionUtil.createCashSweepAccountRequest(wrapAccountDetail, accountCashSweepDto), serviceErrors);
            if (response == null) {
                updatedCashSweepDto.setError(new DomainApiErrorDto(null, ACCOUNT_UPDATE_ERROR, cmsService.getContent(ACCOUNT_UPDATE_ERROR)));
            } else {
                // Clear the account detail cache after successful update
                accountIntegrationService.clearWrapAccountDetail(wrapAccountDetail.getAccountKey());

                updatedCashSweepDto.setCashSweepAllowed(response.isCashSweepApplied());
                updatedCashSweepDto.setMinCashSweepAmount(response.getMinCashSweepAmount());
                updatedCashSweepDto.setMinCashAmount(wrapAccountDetail.getMinCashAmount());
            }
        }
    }

    /**
     * Updates the investment list for the cash sweep for an account. It is updated on the related direct container
     *
     * @param wrapAccountDetail   -  Account details see:{@link WrapAccountDetail}
     * @param accountCashSweepDto - Object for the cash sweep details to be updated
     * @param updatedCashSweepDto - updated  cash sweep object for UI
     * @param serviceErrors       - Error object to capture service level errors
     */
    private void updateCashSweepInvestmentDetails(WrapAccountDetail wrapAccountDetail, AccountCashSweepDto accountCashSweepDto,
                                                  AccountCashSweepDto updatedCashSweepDto, ServiceErrors serviceErrors) {

        if (CollectionUtils.isNotEmpty(accountCashSweepDto.getCashSweepInvestments())) {
            final UpdateCashSweepInvestmentResponse response = containerIntegrationService.updateCashSweepInvestmentDetails(
                    AccountSubscriptionUtil.createCashSweepRequest(getDirectSubAccountKey(wrapAccountDetail), accountCashSweepDto.getCashSweepInvestments()), serviceErrors);
            if (response == null) {
                updatedCashSweepDto.setError(new DomainApiErrorDto(null, INVESTMENTS_UPDATE_ERROR, cmsService.getContent(INVESTMENTS_UPDATE_ERROR)));
            } else {
                updatedCashSweepDto.setCashSweepInvestments(accountProductsHelper.convertToCashSweepInvestmentDto(response.getCashSweepInvestmentAssets(), serviceErrors));
            }
        }
    }

    /**
     * Gets the direct sub-account for an account
     *
     * @param accountDetail - Account details see:{@link WrapAccountDetail}
     * @return {@link SubAccount}
     */
    private SubAccountKey getDirectSubAccountKey(WrapAccountDetail accountDetail) {
        final SubAccount directSubAccount = Lambda.selectFirst(accountDetail.getSubAccounts(),
                Lambda.having(Lambda.on(SubAccount.class).getSubAccountType(), Matchers.equalTo(ContainerType.DIRECT)));
        if (directSubAccount == null) {
            logger.error("Direct sub-account not found for the account: {}", accountDetail.getAccountKey().getId());
            return null;
        }
        return directSubAccount.getSubAccountKey();
    }
}
