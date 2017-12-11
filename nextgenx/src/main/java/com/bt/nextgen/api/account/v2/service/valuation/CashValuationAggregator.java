package com.bt.nextgen.api.account.v2.service.valuation;

import com.bt.nextgen.api.account.v2.model.CashManagementValuationDto;
import com.bt.nextgen.api.account.v2.model.InvestmentValuationDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.CashHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@Component
public class CashValuationAggregator extends AbstractValuationAggregator {

    private static final String CATEGORY_BT_CASH = "BT Cash";
    
    public String getSubAccountCategory() {
        return CATEGORY_BT_CASH;
    }
    
    public List<InvestmentValuationDto> getCashValuationDtos(SubAccountValuation subAccount, BigDecimal accountBalance) {
        List<InvestmentValuationDto> valuationList = new ArrayList<>();

        List<CashManagementValuationDto> dtoList = buildCashDto(subAccount, accountBalance);
        if (CollectionUtils.isNotEmpty(dtoList)) {
            valuationList.addAll(dtoList);
        }
        return valuationList;
    }

    private List<CashManagementValuationDto> buildCashDto(SubAccountValuation subAccount, BigDecimal accountBalance) {

        List<CashManagementValuationDto> cashDtoList = new ArrayList<>();
        if (subAccount == null || CollectionUtils.isEmpty(subAccount.getHoldings())) {
            return cashDtoList;
        }

        for (AccountHolding cashHolding : subAccount.getHoldings()) {
            EncodedString subAccountId = EncodedString.fromPlainText(cashHolding.getHoldingKey().getHid().getId());
            BigDecimal subAccountBalance = cashHolding.getMarketValue();
            BigDecimal subAccountAvailableBalance = roundToZeroIfNegative(cashHolding.getAvailableBalance());

            CashManagementValuationDto cashManagementValuation = new CashManagementValuationDto(subAccountId.toString(),
                    (CashHolding) cashHolding, PortfolioUtils.getValuationAsPercent(subAccountBalance, accountBalance),
                    subAccountAvailableBalance, cashHolding.getExternal());

            cashDtoList.add(cashManagementValuation);
        }
        return cashDtoList;
    }

    private BigDecimal roundToZeroIfNegative(BigDecimal availableBalance) {
        BigDecimal roundedBalance = availableBalance;
        if (availableBalance != null && availableBalance.compareTo(BigDecimal.ZERO) < 0) {
            roundedBalance = BigDecimal.ZERO;
        }
        return roundedBalance;
    }

}
