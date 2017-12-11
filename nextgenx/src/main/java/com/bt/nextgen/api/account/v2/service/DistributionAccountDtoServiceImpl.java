package com.bt.nextgen.api.account.v2.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.PropertyExtractor;
import com.bt.nextgen.api.account.v2.model.AccountAssetKey;
import com.bt.nextgen.api.account.v2.model.DistributionAccountDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.TaxLiability;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ReinvestHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.btfin.panorama.service.integration.asset.AssetKey;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Deprecated
@Service("DistributionAccountDtoServiceV2")
public class DistributionAccountDtoServiceImpl implements DistributionAccountDtoService {
    private static final String CASH_ONLY = "Cash Only";
    private static final String DISTRIBUTION_ONLY = "Distribution Only";

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    @Qualifier("avaloqPortfolioIntegrationService")
    private PortfolioIntegrationService portfolioIntegrationService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Override
    public DistributionAccountDto update(DistributionAccountDto mfa, ServiceErrors serviceErrors) {
        accountIntegrationService.updateDistributionOption(
                AccountKey.valueOf(EncodedString.toPlainText(mfa.getKey().getAccountId())),
                AssetKey.valueOf(mfa.getKey().getAssetId()), DistributionMethod.forDisplayName(mfa.getDistributionOption()),
                serviceErrors);
        return mfa;
    }

    @Override
    public DistributionAccountDto find(AccountAssetKey key, ServiceErrors serviceErrors) {
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        WrapAccountValuation valuation = portfolioIntegrationService.loadWrapAccountValuation(accountKey, new DateTime(),
                serviceErrors);
        WrapAccountDetail accountDetail = accountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);
        return find(key, valuation, accountDetail, serviceErrors);
    }

    private DistributionAccountDto find(AccountAssetKey key, WrapAccountValuation valuation, WrapAccountDetail accountDetail,
            ServiceErrors serviceErrors) {
        DistributionMethod method = getDistributionMethod(valuation, key.getAssetId());
        Asset asset = assetIntegrationService.loadAsset(key.getAssetId(), serviceErrors);
        List<DistributionMethod> availableMethods = getAvailableDistributionMethod(accountDetail, asset);
        List<String> methods = Lambda.convert(availableMethods, new PropertyExtractor<DistributionMethod, String>("displayName"));

        return new DistributionAccountDto(key, method == null ? null : method.getDisplayName(), methods);
    }

    private DistributionMethod getDistributionMethod(WrapAccountValuation valuation, String assetId) {
        for (SubAccountValuation subVal : valuation.getSubAccountValuations()) {
            if (hasReinvestInstruction(subVal)) {
                for (AccountHolding holding : subVal.getHoldings()) {
                    if (holding.getAsset().getAssetId().equals(assetId)) {
                        return ((ReinvestHolding) holding).getDistributionMethod();
                    }
                }
            }
        }
        return null;
    }

    private List<DistributionMethod> getAvailableDistributionMethod(WrapAccountDetail account, Asset asset) {
        List<DistributionMethod> availableMethods = new ArrayList<>();

        String assetDistributionMethod = "";
        if (asset instanceof ManagedFundAsset) {
            ManagedFundAsset mfAsset = (ManagedFundAsset) asset;
            assetDistributionMethod = mfAsset.getDistributionMethod();
        }
        if (CASH_ONLY.equals(assetDistributionMethod)) {
            availableMethods.add(DistributionMethod.CASH);
        } else if (DISTRIBUTION_ONLY.equals(assetDistributionMethod)) {
            availableMethods.add(DistributionMethod.REINVEST);
        } else if (account.getTaxLiability() == TaxLiability.TFN_LIABLE
                || account.getTaxLiability() == TaxLiability.NON_RESIDENT_LIABLE) {
            availableMethods.add(DistributionMethod.CASH);
        } else {
            availableMethods.add(DistributionMethod.CASH);
            availableMethods.add(DistributionMethod.REINVEST);
        }

        return availableMethods;
    }

    @Override
    public List<DistributionMethod> getAvailableDistributionMethod(Asset asset) {
        List<DistributionMethod> availableMethods = new ArrayList<>();

        String assetDistributionMethod = "";
        if (asset instanceof ManagedFundAsset) {
            ManagedFundAsset mfAsset = (ManagedFundAsset) asset;
            assetDistributionMethod = mfAsset.getDistributionMethod();
        }
        if (CASH_ONLY.equals(assetDistributionMethod)) {
            availableMethods.add(DistributionMethod.CASH);
        } else if (DISTRIBUTION_ONLY.equals(assetDistributionMethod)) {
            availableMethods.add(DistributionMethod.REINVEST);
        } else {
            availableMethods.add(DistributionMethod.CASH);
            availableMethods.add(DistributionMethod.REINVEST);
        }

        return availableMethods;
    }


    @Override
    public List<DistributionAccountDto> search(AccountAssetKey partialKey, ServiceErrors serviceErrors) {
        List<DistributionAccountDto> managedFunds = new ArrayList<>();

        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(partialKey.getAccountId()));
        WrapAccountValuation valuation = portfolioIntegrationService.loadWrapAccountValuation(accountKey, new DateTime(),
                serviceErrors);
        WrapAccountDetail accountDetail = accountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);
        for (SubAccountValuation subVal : valuation.getSubAccountValuations()) {
            if (hasReinvestInstruction(subVal)) {
                for (AccountHolding holding : subVal.getHoldings()) {
                    AccountAssetKey key = new AccountAssetKey(partialKey.getAccountId(), holding.getAsset().getAssetId());
                    managedFunds.add(find(key, valuation, accountDetail, serviceErrors));
                }
            }
        }

        return managedFunds;
    }

    private boolean hasReinvestInstruction(SubAccountValuation subVal) {
        AssetType assetType = subVal.getAssetType();
        return assetType == AssetType.MANAGED_FUND || assetType == AssetType.SHARE;
    }
}
