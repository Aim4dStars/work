package com.bt.nextgen.api.portfolio.v3.service.valuation;

import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component("ValuationAggregatorV3")
public class ValuationAggregator {

    @Autowired
    private CashValuationAggregator cashValuationAggregator;

    @Autowired
    private TDValuationAggregator tdValuationAggregator;

    @Autowired
    private MFValuationAggregator mfValuationAggregator;

    @Autowired
    private MPValuationAggregator mpValuationAggregator;

    @Autowired
    private ShareValuationAggregator shareValuationAggregator;

    @Autowired
    private OtherValuationAggregator otherValuationAggregator;

    protected List<InvestmentValuationDto> getSubAccountDtos(AccountKey accountKey, SubAccountValuation subAccount,
            BigDecimal balance, ServiceErrors serviceErrors) {

        AssetType subAccountType = subAccount.getAssetType();
        List<InvestmentValuationDto> valuationDtoList = new ArrayList<>();

        if (AssetType.CASH.equals(subAccountType)) {
            valuationDtoList.addAll(cashValuationAggregator.getCashValuationDtos(subAccount.getHoldings(), balance));
        } else if (AssetType.TERM_DEPOSIT.equals(subAccountType)) {
            valuationDtoList.addAll(tdValuationAggregator.getTermDepositValuationDtos(accountKey, subAccount.getHoldings(),
                    balance,
                    serviceErrors));
        } else if (AssetType.MANAGED_FUND.equals(subAccountType)) {
            valuationDtoList.addAll(mfValuationAggregator.getManagedFundValuationDtos(subAccount.getHoldings(), balance));
        } else if (AssetType.MANAGED_PORTFOLIO.equals(subAccountType) || AssetType.TAILORED_PORTFOLIO.equals(subAccountType)) {
            valuationDtoList.addAll(mpValuationAggregator.getManagedPortfolioValuationDto(subAccount, balance));
        } else if (AssetType.SHARE.equals(subAccountType)) {
            valuationDtoList.addAll(shareValuationAggregator.getShareValuationDtos(subAccount.getHoldings(), balance));
        } else if (AssetType.INTERNATIONAL_SHARE.equals(subAccountType)) {
            valuationDtoList.addAll(otherValuationAggregator.getOtherValuationDto(subAccount.getHoldings(), balance, true));
        } else {
            valuationDtoList.addAll(otherValuationAggregator.getOtherValuationDto(subAccount.getHoldings(), balance, false));
        }

        return valuationDtoList;
    }

    public Map<AssetType, List<InvestmentValuationDto>> getValuationsByCategory(WrapAccountValuation valuation,
            ServiceErrors serviceErrors) {

        BigDecimal balance = valuation.getBalance();
        AccountKey accountKey = valuation.getAccountKey();

        AssetType assetType;
        List<InvestmentValuationDto> subAccountDtos;
        Map<AssetType, List<InvestmentValuationDto>> valuationsByCategory = new LinkedHashMap<>();

        // Split list of subAccounts into sub lists based on category
        for (SubAccountValuation subAccount : valuation.getSubAccountValuations()) {

            assetType = subAccount.getAssetType();
            subAccountDtos = getSubAccountDtos(accountKey, subAccount, balance, serviceErrors);
            List<InvestmentValuationDto> currentList = valuationsByCategory.get(assetType);

            if (currentList == null) {
                currentList = new ArrayList<InvestmentValuationDto>();
                valuationsByCategory.put(assetType, currentList);
            }

            currentList.addAll(subAccountDtos);
        }

        return valuationsByCategory;
    }

}
