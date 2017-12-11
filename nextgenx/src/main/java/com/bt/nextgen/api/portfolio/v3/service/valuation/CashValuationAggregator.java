package com.bt.nextgen.api.portfolio.v3.service.valuation;

import com.bt.nextgen.api.portfolio.v3.model.valuation.CashManagementValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.CashHolding;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component("CashValuationAggregatorV3")
public class CashValuationAggregator extends AbstractValuationAggregator {

    private static final String CATEGORY_BT_CASH = "BT Cash";
    
    public String getSubAccountCategory() {
        return CATEGORY_BT_CASH;
    }
    
    public List<InvestmentValuationDto> getCashValuationDtos(List<AccountHolding> subAccountHoldings, BigDecimal accountBalance) {
        List<InvestmentValuationDto> valuationList = new ArrayList<>();

        List<CashManagementValuationDto> dtoList = buildCashDto(subAccountHoldings, accountBalance);
        if (CollectionUtils.isNotEmpty(dtoList)) {
            valuationList.addAll(dtoList);
        }
        return valuationList;
    }

    private List<CashManagementValuationDto> buildCashDto(List<AccountHolding> subAccountHoldings, BigDecimal accountBalance) {

        List<CashManagementValuationDto> cashDtoList = new ArrayList<>();
        if (subAccountHoldings == null || CollectionUtils.isEmpty(subAccountHoldings)) {
            return cashDtoList;
        }

        for (AccountHolding cashHolding : subAccountHoldings) {
            EncodedString subAccountId = EncodedString.fromPlainText(cashHolding.getHoldingKey().getHid().getId());
            BigDecimal subAccountAvailableBalance = roundToZeroIfNegative(cashHolding.getAvailableBalance());

            CashManagementValuationDto cashManagementValuation = new CashManagementValuationDto(subAccountId.toString(),
                    (CashHolding) cashHolding, accountBalance,
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
