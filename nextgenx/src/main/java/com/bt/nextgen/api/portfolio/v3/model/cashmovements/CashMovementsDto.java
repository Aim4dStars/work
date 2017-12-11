package com.bt.nextgen.api.portfolio.v3.model.cashmovements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;

import ch.lambdaj.Lambda;
import ch.lambdaj.group.Group;

import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.income.HoldingIncomeDetails;
import com.bt.nextgen.service.integration.portfolio.cashmovements.CashMovement;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.CashAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.CashHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.btfin.panorama.service.integration.asset.AssetType;

public class CashMovementsDto extends BaseDto implements KeyedDto<DatedValuationKey> {
    private final DatedValuationKey key;
    private WrapAccountDetail accountDetail;
    private WrapAccountValuation valuation;
    private List<OutstandingMovementsDto> outstandingCash = new ArrayList<>();
    private OutstandingIncomeDto outstandingIncome;

    public CashMovementsDto(DatedValuationKey key, Map<Pair<String, DateTime>, Asset> assets,
            Map<AssetKey, TermDepositPresentation> tds,
            WrapAccountDetail accountDetail,
            WrapAccountValuation valuation, Collection<CashMovement> movements, List<HoldingIncomeDetails> income) {
        this.key = key;
        this.accountDetail = accountDetail;
        this.valuation = valuation;
        convertCashMovements(assets, tds, movements);
        convertIncome(tds, income);
    }

    @Override
    public DatedValuationKey getKey() {
        return key;
    }

    public BigDecimal getValueDateCash() {
        BigDecimal valueDateBalance = BigDecimal.ZERO;
        for (AccountHolding holding : getCashValuation().getHoldings()) {
            if (holding instanceof CashHolding && !holding.getExternal()) {
                valueDateBalance = valueDateBalance.add(((CashHolding) holding).getValueDateBalance());
            }
        }
        return valueDateBalance;
    }

    public BigDecimal getTradeDateCash() {
        BigDecimal tradeDateBalance = BigDecimal.ZERO;
        for (AccountHolding holding : getCashValuation().getHoldings()) {
            if (holding instanceof CashHolding && !holding.getExternal()) {
                tradeDateBalance = tradeDateBalance.add(((CashHolding) holding).getBalance());
            }
        }
        return tradeDateBalance;
    }

    public BigDecimal getOther() {
        BigDecimal other = getTradeDateCash();
        other = other.subtract(getValueDateCash());
        other = other.subtract(getOutstandingCashTotal());
        other = other.subtract(getCashIncome());
        return other;
    }
    
    private BigDecimal getCashIncome() {
        return getCashValuation().getAccruedIncome();
    }

    public BigDecimal getTotalCashMovements() {
        return getOutstandingCashTotal().add(getOther()).add(outstandingIncome.getAmount());
    }

    public BigDecimal getOutstandingTotal() {
        return getOutstandingCashTotal().add(getOther());
    }

    public BigDecimal getOutstandingCashTotal() {
        BigDecimal outstandingCashTotal = BigDecimal.ZERO;
        for (AbstractOutstandingCashDto outstanding : outstandingCash) {
            outstandingCashTotal = outstandingCashTotal.add(outstanding.getAmount());
        }
        return outstandingCashTotal;
    }

    public BigDecimal getAvailableCash() {
        BigDecimal availableCashBalance = BigDecimal.ZERO;
        for (AccountHolding holding : getCashValuation().getHoldings()) {
            if (holding instanceof CashHolding && !holding.getExternal()) {
                availableCashBalance = availableCashBalance.add(((CashHolding) holding).getAvailableBalance());
            }
        }
        return availableCashBalance;
    }

    public BigDecimal getMinCash() {
        if (!accountDetail.isHasMinCash()) {
            return BigDecimal.ZERO;
        }
        return accountDetail.getMinCashAmount();
    }

    public BigDecimal getReservedCash() {
        BigDecimal valueDateCash = getValueDateCash();
        BigDecimal minCash = getMinCash();
        if (valueDateCash.compareTo(minCash) < 0) {
            return null;
        }
        return getValueDateCash().subtract(getMinCash()).subtract(getAvailableCash());
    }

    public List<OutstandingMovementsDto> getOutstandingCash() {
        return outstandingCash;
    }

    public AbstractOutstandingCashDto getOutstandingIncome() {
        return outstandingIncome;
    }

    private void convertCashMovements(Map<Pair<String, DateTime>, Asset> assets, Map<AssetKey, TermDepositPresentation> tds,
            Collection<CashMovement> movements) {
        Group<CashMovement> groupedCategories = Lambda.group(movements, Lambda.by(Lambda.on(CashMovement.class).getCategory()));
        List<Group<CashMovement>> categories = groupedCategories.subgroups();
        for (Group<CashMovement> category : categories) {
            outstandingCash.add(new OutstandingMovementsDto(assets, tds, category.findAll()));
        }
    }

    private void convertIncome(Map<AssetKey, TermDepositPresentation> tds,
            List<HoldingIncomeDetails> income) {
        outstandingIncome = new OutstandingIncomeDto(tds, income);
    }

    private CashAccountValuation getCashValuation() {
        CashAccountValuation cashValuation = null;
        for (SubAccountValuation subaccount : valuation.getSubAccountValuations()) {
            if (subaccount.getAssetType() == AssetType.CASH) {
                cashValuation = (CashAccountValuation) subaccount;
            }
        }
        return cashValuation;
    }

}
