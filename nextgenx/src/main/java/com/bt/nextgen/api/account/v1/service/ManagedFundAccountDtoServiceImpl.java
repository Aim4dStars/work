package com.bt.nextgen.api.account.v1.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.PropertyExtractor;
import com.bt.nextgen.api.account.v1.model.AccountAssetKey;
import com.bt.nextgen.api.account.v1.model.ManagedFundAccountDto;
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
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedFundHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.btfin.panorama.service.integration.asset.AssetKey;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Use V2
 */
@Deprecated
@Service("ManagedFundAccountDtoServiceV1")
public class ManagedFundAccountDtoServiceImpl implements ManagedFundAccountDtoService {
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
    public ManagedFundAccountDto update(ManagedFundAccountDto mfa, ServiceErrors serviceErrors) {
        accountIntegrationService.updateDistributionOption(
                AccountKey.valueOf(EncodedString.toPlainText(mfa.getKey().getAccountId())),
                AssetKey.valueOf(mfa.getKey().getAssetId()), DistributionMethod.forDisplayName(mfa.getDistributionOption()),
                serviceErrors);
        return mfa;
    }

    @Override
    public ManagedFundAccountDto find(AccountAssetKey key, ServiceErrors serviceErrors) {
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        WrapAccountValuation valuation = portfolioIntegrationService.loadWrapAccountValuation(accountKey, new DateTime(),
                serviceErrors);
        WrapAccountDetail accountDetail = accountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);
        return find(key, valuation, accountDetail, serviceErrors);
    }

    private ManagedFundAccountDto find(AccountAssetKey key, WrapAccountValuation valuation, WrapAccountDetail accountDetail,
            ServiceErrors serviceErrors) {
        DistributionMethod method = getDistributionMethod(valuation, key.getAssetId());
        Asset asset = assetIntegrationService.loadAsset(key.getAssetId(), serviceErrors);
        List<DistributionMethod> availableMethods = getAvailableDistributionMethod(accountDetail, asset);
        List<String> methods = Lambda.convert(availableMethods, new PropertyExtractor<DistributionMethod, String>("displayName"));

        return new ManagedFundAccountDto(key, method == null ? null : method.getDisplayName(), methods);

    }

    private DistributionMethod getDistributionMethod(WrapAccountValuation valuation, String assetId) {
        for (SubAccountValuation subVal : valuation.getSubAccountValuations()) {
            if (subVal.getAssetType() == AssetType.MANAGED_FUND) {
                for (AccountHolding holding : subVal.getHoldings()) {
                    if (holding.getAsset().getAssetId().equals(assetId)) {
                        return ((ManagedFundHolding) holding).getDistributionMethod();
                    }
                }
            }
        }
        return null;
    }

    public List<DistributionMethod> getAvailableDistributionMethod(WrapAccountDetail account, Asset asset) {
        List<DistributionMethod> availableMethods = new ArrayList<>();

        ManagedFundAsset mfAsset = (ManagedFundAsset) asset;
        String assetDistributionMethod = mfAsset.getDistributionMethod();
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
    public List<ManagedFundAccountDto> search(AccountAssetKey partialKey, ServiceErrors serviceErrors) {
        List<ManagedFundAccountDto> managedFunds = new ArrayList<>();

        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(partialKey.getAccountId()));
        WrapAccountValuation valuation = portfolioIntegrationService.loadWrapAccountValuation(accountKey, new DateTime(),
                serviceErrors);
        WrapAccountDetail accountDetail = accountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);
        for (SubAccountValuation subVal : valuation.getSubAccountValuations()) {
            if (subVal.getAssetType() == AssetType.MANAGED_FUND) {
                for (AccountHolding holding : subVal.getHoldings()) {
                    AccountAssetKey key = new AccountAssetKey(partialKey.getAccountId(), holding.getAsset().getAssetId());
                    managedFunds.add(find(key, valuation, accountDetail, serviceErrors));
                }
            }
        }

        return managedFunds;
    }
}
