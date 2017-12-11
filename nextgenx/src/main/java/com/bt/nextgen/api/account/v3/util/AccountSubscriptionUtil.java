package com.bt.nextgen.api.account.v3.util;

import com.bt.nextgen.api.account.v3.model.AccountCashSweepDto;
import com.bt.nextgen.api.account.v3.model.CashSweepInvestmentDto;
import com.bt.nextgen.api.account.v3.model.DirectOffer;
import com.bt.nextgen.api.account.v3.model.InitialInvestmentDto;
import com.bt.nextgen.service.avaloq.account.CashSweepAccountRequestImpl;
import com.bt.nextgen.service.avaloq.account.CashSweepInvestmentRequestImpl;
import com.bt.nextgen.service.avaloq.account.InitialInvestmentRequestImpl;
import com.bt.nextgen.service.avaloq.account.SubscriptionRequestImpl;
import com.bt.nextgen.service.integration.account.CashSweepAccountRequest;
import com.btfin.panorama.service.integration.account.CashSweepInvestmentAsset;
import com.bt.nextgen.service.integration.account.CashSweepInvestmentRequest;
import com.btfin.panorama.service.integration.account.InitialInvestmentAsset;
import com.bt.nextgen.service.integration.account.InitialInvestmentRequest;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.account.SubscriptionRequest;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.account.direct.CashSweepInvestmentAssetImpl;
import com.bt.nextgen.service.integration.account.direct.InitialInvestmentAssetImpl;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountSubscriptionUtil {

    private AccountSubscriptionUtil() {
        // private constructor to hide the implicit public one
    }

    /**
     * @param accountDetail - Account detail for account key and modification sequence no.
     * @param directOffer   - The offer to add to the account
     * @return - SubscriptionRequest
     */
    public static SubscriptionRequest createSubscriptionRequest(WrapAccountDetail accountDetail, DirectOffer directOffer) {
        final SubscriptionRequest subscriptionRequest = new SubscriptionRequestImpl();
        subscriptionRequest.setAccountKey(accountDetail.getAccountKey());
        subscriptionRequest.setProductShortName(directOffer.getSubscriptionProduct());
        subscriptionRequest.setModificationIdentifier(new BigDecimal(accountDetail.getModificationSeq()));
        return subscriptionRequest;
    }

    /**
     * @param accountDetail           - Account detail for account key and modification sequence no.
     * @param initialInvestmentAssets - Initial investment asset details for switching simple/active account
     * @return - InitialInvestmentRequest
     */
    public static InitialInvestmentRequest createInvestmentRequest(WrapAccountDetail accountDetail,
                                                                   List<InitialInvestmentDto> initialInvestmentAssets) {

        return new InitialInvestmentRequestImpl(accountDetail.getAccountKey(),
                extractInitialInvestments(initialInvestmentAssets),
                new BigDecimal(accountDetail.getModificationSeq()));
    }

    private static List<InitialInvestmentAsset> extractInitialInvestments(List<InitialInvestmentDto> investmentAssetDtoList) {
        final List<InitialInvestmentAsset> initialInvestmentAssets = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(investmentAssetDtoList)) {
            InitialInvestmentAssetImpl initialInvestmentAsset;
            for (InitialInvestmentDto initialInvestmentAssetDto : investmentAssetDtoList) {
                if (initialInvestmentAssetDto != null) {
                    initialInvestmentAsset = new InitialInvestmentAssetImpl();
                    initialInvestmentAsset.setInitialInvestmentAssetId(initialInvestmentAssetDto.getAsset().getAssetId());
                    initialInvestmentAsset.setInitialInvestmentAmount(initialInvestmentAssetDto.getAmount());
                    initialInvestmentAssets.add(initialInvestmentAsset);
                }
            }
        }
        return initialInvestmentAssets;
    }

    /**
     * Creates a request to update Cash sweep details on the account (sweep flag, min sweep amount)
     *
     * @param accountDetail       - Account detail for account key and modification sequence no.
     * @param accountCashSweepDto - Cash sweep details {@link AccountCashSweepDto}
     * @return - {@link CashSweepInvestmentRequest}
     */
    public static CashSweepAccountRequest createCashSweepAccountRequest(WrapAccountDetail accountDetail, AccountCashSweepDto accountCashSweepDto) {
        return new CashSweepAccountRequestImpl(accountDetail.getAccountKey(), accountCashSweepDto.isCashSweepAllowed(),
                new BigDecimal(accountDetail.getModificationSeq()), accountCashSweepDto.getMinCashSweepAmount());
    }

    /**
     * Creates a request to update Cash sweep details on the account (investment list)
     *
     * @param directSubAccountKey     - Direct sub-account identifier
     * @param cashSweepInvestments - List of investments
     * @return - {@link CashSweepInvestmentRequest}
     */
    public static CashSweepInvestmentRequest createCashSweepRequest(SubAccountKey directSubAccountKey, List<CashSweepInvestmentDto> cashSweepInvestments) {
        return new CashSweepInvestmentRequestImpl(directSubAccountKey, extractCashSweepInvestments(cashSweepInvestments));
    }

    private static List<CashSweepInvestmentAsset> extractCashSweepInvestments(List<CashSweepInvestmentDto> cashSweepInvestmentDtoList) {
        final List<CashSweepInvestmentAsset> cashSweepInvestments = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(cashSweepInvestmentDtoList)) {
            CashSweepInvestmentAssetImpl cashSweepInvestmentAsset;
            for (CashSweepInvestmentDto cashSweepInvestmentDto : cashSweepInvestmentDtoList) {
                cashSweepInvestmentAsset = new CashSweepInvestmentAssetImpl();
                cashSweepInvestmentAsset.setInvestmentAssetId(cashSweepInvestmentDto.getAsset().getAssetId());
                cashSweepInvestmentAsset.setSweepPercent(cashSweepInvestmentDto.getAllocationPercent());
                cashSweepInvestments.add(cashSweepInvestmentAsset);
            }
        }
        return cashSweepInvestments;
    }
}
