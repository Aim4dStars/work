package com.bt.nextgen.api.account.v2.service.valuation;

import com.bt.nextgen.api.account.v2.model.InvestmentValuationDto;
import com.bt.nextgen.api.account.v2.model.TermDepositValuationDto;
import com.bt.nextgen.api.account.v2.service.TermDepositPresentationService;
import com.bt.nextgen.api.util.TermDepositUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.TermDepositAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.TermDepositHolding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@Component
public class TDValuationAggregator extends AbstractValuationAggregator {

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Autowired
    private TermDepositPresentationService termDepositPresentationService;

    private static final String CATEGORY_TERM_DEPOSIT = "Term deposits";

    public String getSubAccountCategory() {
        return CATEGORY_TERM_DEPOSIT;
    }

    public List<InvestmentValuationDto> getTermDepositValuationDtos(AccountKey accountKey, SubAccountValuation subAccount,
            BigDecimal accountBalance, ServiceErrors serviceErrors) {
        List<InvestmentValuationDto> valuationList = new ArrayList<>();

        TermDepositAccountValuation tdAcc = (TermDepositAccountValuation) subAccount;
        for (AccountHolding tdHolding : tdAcc.getHoldings()) {
            InvestmentValuationDto valuationDto = buildTermDepositValuationDto(accountKey, (TermDepositHolding) tdHolding,
                    accountBalance, serviceErrors);
            valuationList.add(valuationDto);
        }

        return valuationList;
    }

    private TermDepositValuationDto buildTermDepositValuationDto(AccountKey accountKey, TermDepositHolding termDepositHolding,
            BigDecimal accountBalance, ServiceErrors serviceErrors) {

        String maturityInstruction = TermDepositUtil.getMaturityInstruction(termDepositHolding.getMaturityInstruction(),
                "BT Cash",
                staticIntegrationService, serviceErrors);

        BigDecimal subAccountBalance = termDepositHolding.getMarketValue();

        return new TermDepositValuationDto(termDepositPresentationService.getTermDepositPresentation(accountKey,
                termDepositHolding.getAsset().getAssetId(), serviceErrors), termDepositHolding,
                PortfolioUtils.getValuationAsPercent(subAccountBalance, accountBalance), maturityInstruction,
                termDepositHolding.getExternal());
    }
}
