package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionResponseConverter;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionExerciseRightsType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;


@CorporateActionResponseConverter("CA_EXERCISE_CALL_OPTION_RESPONSE")
public class ExerciseCallOptionResponseConverterServiceImpl extends ExerciseRightsResponseConverterServiceImpl {

    @Override
    public CorporateActionDetailsDtoParams setCorporateActionDetailsDtoParams(CorporateActionContext context,
                                                                              CorporateActionDetailsDtoParams params) {
        super.setCorporateActionDetailsDtoParams(context, params);

        CorporateActionOption optionPrice = selectFirst(context.getCorporateActionDetails().getOptions(),
                having(on(CorporateActionOption.class).getKey(), equalTo(CorporateActionOptionKey.STRIKE_PRICE.getCode())));

        params.setCorporateActionPrice(optionPrice != null ? optionPrice.getBigDecimalValue() : null);

        return params;
    }

    @Override
    public CorporateActionAccountDetailsDtoParams setCorporateActionAccountDetailsDtoParams(CorporateActionContext context,
                                                                                            CorporateActionAccount account,
                                                                                            CorporateActionAccountDetailsDtoParams params) {
        // Modify available balance to the balance prior to election so not enough cash warning will not be shown
        if (CorporateActionAccountParticipationStatus.SUBMITTED.equals(account.getElectionStatus())) {
            final int selectedOptionIdx = getSelectedOptionIndex(account.getDecisions());
            final CorporateActionOption priceOption = selectFirst(context.getCorporateActionDetails().getOptions(),
                    having(on(CorporateActionOption.class).getKey(), equalTo(CorporateActionOptionKey.STRIKE_PRICE.getCode())));

            if (priceOption != null) {
                BigDecimal price = priceOption.getBigDecimalValue();
                final Map<String, CorporateActionOption> options = toOptionsMap(account.getDecisions());

                if (selectedOptionIdx == CorporateActionExerciseRightsType.PARTIAL.getId()) {
                    BigDecimal units = getBigDecimalOptionValue(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY, options);
                    params.setCash(params.getCash().add(units.multiply(price)));
                } else if (selectedOptionIdx == CorporateActionExerciseRightsType.FULL.getId()) {
                    BigDecimal oversubscribe =
                            getBigDecimalOptionValue(CorporateActionDecisionKey.EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY, options);
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

            CorporateActionOption exeAllOption = optionsMap.get(CorporateActionDecisionKey.EXERCISE_RIGHTS_ALL.getCode());

            if (exeAllOption != null && "Y".equals(exeAllOption.getValue())) {
                return CorporateActionExerciseRightsType.FULL.getId();
            }

            BigDecimal quantity = getBigDecimalOptionValue(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY, optionsMap);

            if (quantity != null && quantity.compareTo(BigDecimal.ZERO) > 0) {
                return CorporateActionExerciseRightsType.PARTIAL.getId();
            }
        }

        return CorporateActionExerciseRightsType.LAPSE.getId();
    }
}
