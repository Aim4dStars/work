package com.bt.nextgen.api.income.v2.service;

import com.bt.nextgen.api.income.v2.model.CashIncomeDto;
import com.bt.nextgen.api.income.v2.model.DistributionIncomeDto;
import com.bt.nextgen.api.income.v2.model.DividendIncomeDto;
import com.bt.nextgen.api.income.v2.model.FeeRebateIncomeDto;
import com.bt.nextgen.api.income.v2.model.InterestIncomeDto;
import com.bt.nextgen.api.income.v2.model.TermDepositIncomeDto;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.income.CashIncome;
import com.bt.nextgen.service.integration.income.HoldingIncomeDetails;
import com.bt.nextgen.service.integration.income.Income;
import com.bt.nextgen.service.integration.income.IncomeType;
import com.bt.nextgen.service.integration.income.TermDepositIncome;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.CashHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.TermDepositHolding;
import com.bt.nextgen.service.wrap.integration.income.ThirdPartyDividendIncomeImpl;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class IncomeDtoFromIncomeValuationBuilder {

    CashIncomeDto toCashIncomeDto(HoldingIncomeDetails holding, Income income) {
        CashIncome cashIncome = (CashIncome) income;
        IncomeType incomeType = cashIncome.getIncomeSubtype();
        CashIncomeDto incomeDto = new CashIncomeDto(holding.getAsset().getAssetName(), holding.getAsset().getAssetCode(),
                cashIncome.getPaymentDate(), cashIncome.getAmount(), incomeType);
        return incomeDto;
    }

    CashIncomeDto toCashIncomeDto(AccountHolding holding) {
        CashHolding cashHolding = (CashHolding) holding;
        IncomeType incomeType = IncomeType.forDisplay(cashHolding.getAsset().getIncomeType());
        CashIncomeDto incomeDto = new CashIncomeDto(cashHolding.getAccountName(), "", cashHolding.getNextInterestDate(),
                cashHolding.getAccruedIncome(), incomeType);
        cashHolding.getAsset().getIncomeType();
        return incomeDto;
    }

    TermDepositIncomeDto toTermDepositIncomeDto(HoldingIncomeDetails holding, Income income,
                                                TermDepositPresentation termDepositPresentation, Boolean isWrapTD) {
        TermDepositAsset tdAsset = (TermDepositAsset) holding.getAsset();
        DateTime maturityDate = tdAsset == null ? null : tdAsset.getMaturityDate();

        TermDepositIncome termDepositIncome = (TermDepositIncome) income;
        TermDepositIncomeDto incomeDto = new TermDepositIncomeDto(termDepositPresentation.getBrandName(),
                termDepositPresentation.getBrandClass(), termDepositIncome.getPaymentDate(), maturityDate,
                termDepositIncome.getInterest(), termDepositPresentation.getTerm(),
                termDepositPresentation.getPaymentFrequency());
        incomeDto.setWrapTermDeposit(isWrapTD);
        return incomeDto;
    }

    TermDepositIncomeDto toTermDepositIncomeDto(AccountHolding holding, TermDepositPresentation termDepositPresentation) {
        TermDepositHolding tdHolding = (TermDepositHolding) holding;
        TermDepositIncomeDto incomeDto = new TermDepositIncomeDto(termDepositPresentation.getBrandName(),
                termDepositPresentation.getBrandClass(), tdHolding.getNextInterestDate(), tdHolding.getMaturityDate(),
                tdHolding.getAccruedIncome(), termDepositPresentation.getTerm(), termDepositPresentation.getPaymentFrequency());

        return incomeDto;
    }

    DividendIncomeDto toDividendIncomeDto(AccountHolding holding, Income income) {
        DividendIncomeDto dividendIncomeDto = new DividendIncomeDto(holding.getAsset().getAssetName(), holding.getAsset()
                .getAssetCode(), income);
        return dividendIncomeDto;
    }

    DividendIncomeDto toDividendIncomeDto(HoldingIncomeDetails holding, Income income) {
        DividendIncomeDto dividendIncomeDto = new DividendIncomeDto(holding.getAsset().getAssetName(), holding.getAsset()
                .getAssetCode(), income);
        if (income instanceof ThirdPartyDividendIncomeImpl &&
                SystemType.WRAP.name().equals(((ThirdPartyDividendIncomeImpl) income).getThirdPartySource())) {
            dividendIncomeDto.setWrapIncome(true);
        }
        return dividendIncomeDto;
    }

    InterestIncomeDto toInterestIncomeDto(HoldingIncomeDetails holding, Income income) {
        InterestIncomeDto interestIncomeDto = new InterestIncomeDto(holding.getAsset().getAssetName(), holding.getAsset()
                .getAssetCode(), income);
        return interestIncomeDto;
    }

    DistributionIncomeDto toDistributionIncomeDto(HoldingIncomeDetails holding, Income income) {
        DistributionIncomeDto incomeDto = new DistributionIncomeDto(holding.getAsset().getAssetName(), holding.getAsset()
                .getAssetCode(), income);
        return incomeDto;
    }

    FeeRebateIncomeDto toFeeRebateIncomeDto(HoldingIncomeDetails holding, Income income) {
        FeeRebateIncomeDto feeRebateIncomeDto = new FeeRebateIncomeDto(holding.getAsset().getAssetName(), holding.getAsset()
                .getAssetCode(), income);
        return feeRebateIncomeDto;
    }

    CashIncomeDto toCashIncomeDtoMp(Asset parentAsset, AccountHolding holding) {
        CashHolding cashHolding = (CashHolding) holding;
        IncomeType incomeType = IncomeType.forDisplay(cashHolding.getAsset().getIncomeType());
        CashIncomeDto incomeDto = new CashIncomeDto(parentAsset.getAssetName(), parentAsset.getAssetCode(),
                cashHolding.getNextInterestDate(), cashHolding.getAccruedIncome(), incomeType);
        return incomeDto;
    }

}
