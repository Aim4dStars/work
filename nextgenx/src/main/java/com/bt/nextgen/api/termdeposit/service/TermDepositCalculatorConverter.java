package com.bt.nextgen.api.termdeposit.service;

import com.bt.nextgen.api.termdeposit.model.Brand;
import com.bt.nextgen.api.termdeposit.model.TermDepositBankRates;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorDto;
import com.bt.nextgen.api.termdeposit.util.TermDepositAssetRateUtil;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.termdeposit.web.model.TermDepositRateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.bt.nextgen.core.web.Format.asCurrency;

/**
 * Created by M044020 on 25/07/2017.
 */
@Component
public class TermDepositCalculatorConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TermDepositCalculatorConverter.class);
    private static final int YEAR = 12;
    private static final BigDecimal BIGDECIMAL_DEFAULT = new BigDecimal("0.0");

    public TermDepositCalculatorDto toTermDepositCalculatorDto(final List<Brand> brands,
            final List<TermDepositInterestRate> termDepositInterestRates, final BigDecimal amount) {
        LOGGER.debug("Creating model for amount {}", amount);
        if (CollectionUtils.isEmpty(brands) || CollectionUtils.isEmpty(termDepositInterestRates) || amount == null) {
            LOGGER.warn("Could not create the model as either brands, termDepositInterestRates or the amount is null.");
            return null;
        }
        final TermDepositCalculatorDto termDepositCalculatorDto = new TermDepositCalculatorDto();
        final Map<Brand, TermDepositBankRates> banksMap = new TreeMap<>();
        TermDepositBankRates model;
        for (final Brand brand : brands) {
            LOGGER.info("TermDepositCalculatorDtoServiceImpl.toTermDepositCalculatorDto(): Bank Id: {}", brand.getId());
            model = new TermDepositBankRates();
            banksMap.put(brand, model);
            model.setBrandId(brand.getId());
            populateRatesForBrands(termDepositInterestRates, brand, model, amount);
            model.setTermMap(new TreeMap<>(model.getTermMap()));
        }
        getBestRates(banksMap);
        termDepositCalculatorDto.setTermDepositBankRates(new ArrayList<>(banksMap.values()));
        return termDepositCalculatorDto;
    }

    private void populateRatesForBrands(final List<TermDepositInterestRate> termDepositInterestRates, final Brand brand,
            final TermDepositBankRates model, final BigDecimal amount) {
        for (TermDepositInterestRate interestRate : termDepositInterestRates) {
            if (brand.getId().equalsIgnoreCase(interestRate.getIssuerId())) {
                final TermDepositRateModel mRate = getRateModelForAsset(interestRate, model, amount);
                model.getTermMap().put(interestRate.getTerm(), mRate);
            }
        }
    }

    private TermDepositRateModel getRateModelForAsset(final TermDepositInterestRate interestRate,
            final TermDepositBankRates model, final BigDecimal amount) {
        LOGGER.info("Asset Id: {}, Frequency: {}", interestRate.getAssetKey().getId(),
                interestRate.getPaymentFrequency().getDisplayName());
        TermDepositRateModel mRate;
        final TermDepositRateModel termMap = model.getTermMap().get(interestRate.getTerm());
        mRate = (termMap != null) ? model.getTermMap().get(interestRate.getTerm()) : new TermDepositRateModel();
        final Term term = interestRate.getTerm();
        final BigDecimal yearlyRate = interestRate.getYearlyRate();

        if (yearlyRate.compareTo(BIGDECIMAL_DEFAULT) != 0) {
            commonTermValues(mRate, yearlyRate, amount, term);
        }
        if (term.getMonths() < YEAR) {
            monthlyTermValues(mRate, yearlyRate, amount, term, interestRate.getAssetKey().getId());
        }
        if (term.getMonths() >= YEAR) {
            yearlyTermValues(mRate, amount, term, interestRate);
        }
        return mRate;
    }

    private void commonTermValues(final TermDepositRateModel mRate, final BigDecimal yearlyRate,
            final BigDecimal amount, final Term term) {
        mRate.setInterestPerTerm(AvaloqUtils.asAvaloqRate(yearlyRate));
        mRate.setInterestRatePerYear(
                asCurrency(TermDepositAssetRateUtil.getTotalInterestEarned(yearlyRate, amount, 12)));
        mRate.setTotalInterestEarnedYearly(
                asCurrency(TermDepositAssetRateUtil.getTotalInterestEarned(yearlyRate, amount, term.getMonths())));
        mRate.setMaturityValueYearly(
                asCurrency(TermDepositAssetRateUtil.getTotalAmountAtMaturity(yearlyRate, amount, term.getMonths())));
    }

    private void monthlyTermValues(final TermDepositRateModel mRate, final BigDecimal yearlyRate,
            final BigDecimal amount, final Term term, final String assetId) {
        mRate.setMaturityId(assetId);
        mRate.setTotalInterestEarnedMonthly(
                asCurrency(TermDepositAssetRateUtil.getTotalInterestEarned(yearlyRate, amount, term.getMonths())));
        mRate.setMaturityValueMonthly(
                asCurrency(TermDepositAssetRateUtil.getTotalAmountAtMaturity(yearlyRate, amount, term.getMonths())));
    }

    private void yearlyTermValues(final TermDepositRateModel mRate, final BigDecimal amount, final Term term,
            final TermDepositInterestRate interestRate) {
        final String assetId = interestRate.getAssetKey().getId();
        final BigDecimal monthlyRate = interestRate.getMonthlyRate();
        if (term.getMonths() == YEAR && interestRate.getPaymentFrequency() == PaymentFrequency.AT_MATURITY) {
            mRate.setMaturityId(assetId);
        } else if (interestRate.getPaymentFrequency() == PaymentFrequency.MONTHLY) {
            mRate.setMonthlyId(assetId);
        } else {
            mRate.setYearlyId(assetId);
        }
        if (monthlyRate.compareTo(BIGDECIMAL_DEFAULT) != 0) {
            mRate.setTermDepositMonthlyIterest(AvaloqUtils.asAvaloqRate(monthlyRate));
            mRate.setInterestRatePerMonth(asCurrency(TermDepositAssetRateUtil.getInterestPerMonth(monthlyRate, amount)));
            mRate.setTotalInterestEarnedMonthly(
                    asCurrency(TermDepositAssetRateUtil.getTotalInterestEarned(monthlyRate, amount, term.getMonths())));
            mRate.setMaturityValueMonthly(
                    asCurrency(TermDepositAssetRateUtil.getTotalAmountAtMaturity(monthlyRate, amount, term.getMonths())));
        }
    }

    /**
     * Method to set the best rate for each term and the highest rate for the 6 months term.
     */
    private static void getBestRates(final Map<Brand, TermDepositBankRates> banksMap) {
        final Map<Term, BigDecimal> bestTermMap = new LinkedHashMap<>();
        final Map<Term, BigDecimal> termMap = new LinkedHashMap<>();
        boolean firstIteration = true;
        // Iterating to find the best rates for each term.
        for (final TermDepositBankRates model : banksMap.values()) {
            if (firstIteration) {
                for (final Term term : model.getTermMap().keySet()) {
                    termMap.put(term, new BigDecimal(
                            AvaloqUtils.deformatRate(model.getTermMap().get(term).getInterestPerTerm())));
                    bestTermMap.putAll(termMap);
                }
                firstIteration = false;
            } else {
                for (final Term term : model.getTermMap().keySet()) {
                    bestTermMap.put(term, compareRate(
                            new BigDecimal(AvaloqUtils.deformatRate(model.getTermMap().get(term).getInterestPerTerm())),
                            termMap.get(term)));
                }
                termMap.putAll(bestTermMap);
            }
        }
        // Iterating again to set the best rate in term flag & highest rate in 6-months term for each model.
        for (final TermDepositBankRates model : banksMap.values()) {
            for (final Term term : model.getTermMap().keySet()) {
                if (new BigDecimal(AvaloqUtils.deformatRate(model.getTermMap().get(term).getInterestPerTerm()))
                        .compareTo(bestTermMap.get(term)) == 0)
                {
                    LOGGER.info("Best Rate for the term {} months = {}", term.getMonths(), bestTermMap.get(term));
                    model.getTermMap().get(term).setBestRateFlag(true);
                    if (term.getMonths() == 6) {
                        model.getTermMap().get(term).setHighestRateFlag(true);
                    }
                }
            }
        }
    }

    private static BigDecimal compareRate(final BigDecimal bestRate, final BigDecimal currentRate) {
        if (currentRate != null && currentRate.compareTo(bestRate) > 0) {
            return currentRate;
        }
        return bestRate;
    }
}
