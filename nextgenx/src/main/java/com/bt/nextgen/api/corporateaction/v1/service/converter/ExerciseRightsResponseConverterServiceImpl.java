package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionExerciseRightsAccountElectionsDtoImpl;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOversubscription;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionRatio;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionResponseConverter;
import com.bt.nextgen.core.repository.CorporateActionSavedAccount;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElection;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionExerciseRightsType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;


@CorporateActionResponseConverter("CA_EXERCISE_RIGHTS_RESPONSE")
public class ExerciseRightsResponseConverterServiceImpl extends AbstractCorporateActionResponseConverterServiceImpl {
    @Override
    public CorporateActionAccountElectionsDto toSubmittedAccountElectionsDto(CorporateActionContext context,
                                                                             CorporateActionAccount account) {
        int idx = getSelectedOptionIndex(account.getDecisions());

        Map<String, CorporateActionOption> options = toOptionsMap(account.getDecisions());

        BigDecimal units = null;
        BigDecimal oversubscribe = null;

        if (idx == CorporateActionExerciseRightsType.PARTIAL.getId()) {
            units = getBigDecimalOptionValue(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY, options);

            if (units != null) {
                // Set the quantity for partial based on ratio
                BigDecimal ratio = getApplicableRatio(context.getCorporateActionDetails()).getRatio();
                units = ratio.multiply(units).setScale(0, RoundingMode.HALF_DOWN);
            }
        } else if (idx == CorporateActionExerciseRightsType.FULL.getId()) {
            oversubscribe = getBigDecimalOptionValue(CorporateActionDecisionKey.EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY, options);
        }

        return CorporateActionExerciseRightsAccountElectionsDtoImpl.createSingleAccountElection(idx, units, oversubscribe);
    }

    @Override
    public CorporateActionAccountElectionsDto toSavedAccountElectionsDto(CorporateActionContext context, String accountId,
                                                                         CorporateActionSavedDetails savedElections) {
        CorporateActionAccountElectionsDto electionsDto = null;

        if (savedElections.getSavedParticipation() != null) {
            CorporateActionSavedAccount savedAccount =
                    selectFirst(savedElections.getSavedParticipation().getAccounts(),
                            having(on(CorporateActionSavedAccount.class).getKey().getAccountNumber(), equalTo(accountId)));

            if (savedAccount != null && !savedAccount.getAccountElections().isEmpty()) {
                // Only one will ever exist for this CA
                CorporateActionSavedAccountElection savedAccountElection = savedAccount.getAccountElections().iterator().next();
                electionsDto = CorporateActionExerciseRightsAccountElectionsDtoImpl
                        .createSingleAccountElection(savedAccountElection.getKey().getOptionId(), savedAccountElection.getUnits(),
                                savedAccountElection.getOversubscribe());
            }
        }

        return electionsDto;
    }

    @Override
    public CorporateActionDetailsDtoParams setCorporateActionDetailsDtoParams(CorporateActionContext context,
                                                                              CorporateActionDetailsDtoParams params) {
        CorporateActionOption oversubscribeOption =
                selectFirst(context.getCorporateActionDetails().getOptions(),
                        having(on(CorporateActionOption.class).getKey(), equalTo(CorporateActionOptionKey.OVERSUBSCRIBE.getCode())));

        params.setOversubscribe(oversubscribeOption != null && "Y".equals(oversubscribeOption.getValue()) ? Boolean.TRUE : Boolean.FALSE);

        if (params.getOversubscribe()) {
            CorporateActionOption maxQuantityOption = selectFirst(context.getCorporateActionDetails().getOptions(),
                    having(on(CorporateActionOption.class).getKey(),
                            equalTo(CorporateActionOptionKey.MAX_OVERSUBSCRIBE_QUANTITY.getCode())));

            CorporateActionOption maxPercentOption = selectFirst(context.getCorporateActionDetails().getOptions(),
                    having(on(CorporateActionOption.class).getKey(),
                            equalTo(CorporateActionOptionKey.MAX_OVERSUBSCRIBE_PERCENT.getCode())));

            BigDecimal maxOversubscribeQuantity = maxQuantityOption != null ? maxQuantityOption.getBigDecimalValue() : null;
            BigDecimal maxOversubscribePercent = maxPercentOption != null ? maxPercentOption.getBigDecimalValue() : null;

            params.setOversubscription(new CorporateActionOversubscription(maxOversubscribeQuantity, maxOversubscribePercent));
        }

        CorporateActionOption price =
                selectFirst(context.getCorporateActionDetails().getOptions(),
                        having(on(CorporateActionOption.class).getKey(), equalTo(CorporateActionOptionKey.PRICE.getCode())));

        params.setCorporateActionPrice(price != null ? new BigDecimal(price.getValue()) : null);

        // Set the ratio as required by front-end calculations
        params.setApplicableRatio(getApplicableRatio(context.getCorporateActionDetails()));

        return params;
    }

    @Override
    public List<CorporateActionOptionDto> toElectionOptionDtos(CorporateActionContext context, ServiceErrors serviceErrors) {
        final int selectedOptionIdx = getSelectedOptionIndex(context.getCorporateActionDetails().getDecisions());
        final List<CorporateActionOptionDto> optionDtos = new ArrayList<>(3);
        int idx = 0;

        optionDtos.add(new CorporateActionOptionDto(CorporateActionExerciseRightsType.LAPSE.getId(), getOptionFieldHeader(idx), "Lapse",
                selectedOptionIdx == CorporateActionExerciseRightsType.LAPSE.getId()));

        if (!context.isDealerGroupOrInvestmentManager()) {
            idx++;
            optionDtos.add(new CorporateActionOptionDto(CorporateActionExerciseRightsType.PARTIAL.getId(), getOptionFieldHeader(idx),
                    "Partial exercise",
                    selectedOptionIdx == CorporateActionExerciseRightsType.PARTIAL.getId()));
        }

        idx++;
        optionDtos.add(new CorporateActionOptionDto(CorporateActionExerciseRightsType.FULL.getId(), getOptionFieldHeader(idx),
                "Full exercise", selectedOptionIdx == CorporateActionExerciseRightsType.FULL.getId()));

        return optionDtos;
    }

    @Override
    public CorporateActionAccountDetailsDtoParams setCorporateActionAccountDetailsDtoParams(CorporateActionContext context,
                                                                                            CorporateActionAccount account,
                                                                                            CorporateActionAccountDetailsDtoParams params) {
        BigDecimal ratio = getApplicableRatio(context.getCorporateActionDetails()).getRatio();

        if (ratio.compareTo(BigDecimal.ZERO) >= 0) {
            params.setOriginalHolding(params.getHolding());
            params.setHolding(ratio.multiply(new BigDecimal(params.getHolding())).intValue());
        }

        // Modify available balance to the balance prior to election so not enough cash warning will not be shown
        if (CorporateActionAccountParticipationStatus.SUBMITTED.equals(account.getElectionStatus())) {
            final int selectedOptionIdx = getSelectedOptionIndex(account.getDecisions());
            final CorporateActionOption priceOption =
                    selectFirst(context.getCorporateActionDetails().getOptions(), having(on(CorporateActionOption.class).getKey(),
                            equalTo(CorporateActionOptionKey.PRICE.getCode())));

            if (priceOption != null) {
                final Map<String, CorporateActionOption> options = toOptionsMap(account.getDecisions());
                BigDecimal price = priceOption.getBigDecimalValue();

                if (selectedOptionIdx == CorporateActionExerciseRightsType.PARTIAL.getId()) {
                    BigDecimal units = getBigDecimalOptionValue(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY, options);
                    params.setCash(params.getCash().add(units.multiply(price)));
                } else if (selectedOptionIdx == CorporateActionExerciseRightsType.FULL.getId()) {
                    BigDecimal oversubscribe = getBigDecimalOptionValue(CorporateActionDecisionKey
                            .EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY, options);
                    BigDecimal holding = new BigDecimal(params.getHolding());
                    BigDecimal blockedCash = oversubscribe != null ? holding.add(oversubscribe).multiply(price) : holding.multiply(price);
                    params.setCash(params.getCash().add(blockedCash));
                }
            }
        }

        return params;
    }

    /**
     * Determine the index of default option, which is mapped to par_list.
     *
     * @param decisions list of Avaloq "default" options.
     * @return the index, starting from 1, where the default option is located.  Returns -1 if not found.
     */
    @Override
    protected int getSelectedOptionIndex(List<CorporateActionOption> decisions) {
        if (decisions != null) {
            Map<String, CorporateActionOption> optionsMap = toOptionsMap(decisions);

            BigDecimal percent = getBigDecimalOptionValue(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT, optionsMap);

            if (percent != null) {
                if (percent.compareTo(CorporateActionConverterConstants.DECIMAL_ONE_HUNDRED) == 0) {
                    return CorporateActionExerciseRightsType.FULL.getId();
                } else if (percent.compareTo(BigDecimal.ZERO) > 0) {
                    return CorporateActionExerciseRightsType.PARTIAL.getId();
                }
            }

            BigDecimal quantity = getBigDecimalOptionValue(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY, optionsMap);

            if (quantity != null && quantity.compareTo(BigDecimal.ZERO) > 0) {
                return CorporateActionExerciseRightsType.PARTIAL.getId();
            }
        }

        return CorporateActionExerciseRightsType.LAPSE.getId();
    }


    private CorporateActionRatio getApplicableRatio(CorporateActionDetails details) {
        BigDecimal oldStock = BigDecimal.ONE;
        BigDecimal newStock = BigDecimal.ONE;

        CorporateActionOption oldStockOption = selectFirst(details.getOptions(),
                having(on(CorporateActionOption.class).getKey(), equalTo(CorporateActionOptionKey.OLD_STOCK_HELD.getCode())));

        CorporateActionOption newStockOption = selectFirst(details.getOptions(),
                having(on(CorporateActionOption.class).getKey(), equalTo(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode())));

        if (oldStockOption != null && oldStockOption.hasValue()) {
            oldStock = oldStockOption.getBigDecimalValue();

            if (oldStock.compareTo(BigDecimal.ZERO) == 0) {
                oldStock = BigDecimal.ONE;
            }
        }

        if (newStockOption != null && newStockOption.hasValue()) {
            newStock = newStockOption.getBigDecimalValue();
        }

        return new CorporateActionRatio(oldStock, newStock);
    }
}
