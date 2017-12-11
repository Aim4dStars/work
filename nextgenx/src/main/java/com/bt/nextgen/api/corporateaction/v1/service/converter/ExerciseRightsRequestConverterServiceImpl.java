package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionsDto;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionRequestConverter;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionDecisionImpl;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionElectionGroupImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionExerciseRightsType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionPosition;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;


@CorporateActionRequestConverter("CA_EXERCISE_RIGHTS_REQUEST")
public class ExerciseRightsRequestConverterServiceImpl extends AbstractCorporateActionRequestConverterServiceImpl {
    @Override
    protected CorporateActionElectionGroup createElectionGroupCommon(CorporateActionContext context,
                                                                     CorporateActionSelectedOptionsDto selectedOptionsDto,
                                                                     List<CorporateActionPosition> positions) {
        CorporateActionSelectedOptionDto selectedOptionDto = selectedOptionsDto.getPrimarySelectedOption();

        CorporateActionElectionGroup electionGroup =
                new CorporateActionElectionGroupImpl(context.getCorporateActionDetails().getOrderNumber(), selectedOptionDto, positions,
                        new ArrayList<CorporateActionOption>());

        if (selectedOptionDto.getOptionId().equals(CorporateActionExerciseRightsType.FULL.getId())) {
            setFullRightsExercise(electionGroup, selectedOptionDto);
        } else if (selectedOptionDto.getOptionId().equals(CorporateActionExerciseRightsType.PARTIAL.getId())) {
            setPartialRightsExercise(context.getCorporateActionDetails(), electionGroup, selectedOptionDto);
        } else {
            setLapseRightsExercise(electionGroup);
        }

        // Needed?
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), "Y"));

        return electionGroup;
    }

    protected void setLapseRightsExercise(CorporateActionElectionGroup electionGroup) {
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), "0"));
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY
                .getCode(), ""));
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), ""));
    }

    protected void setPartialRightsExercise(CorporateActionDetails details, CorporateActionElectionGroup electionGroup,
                                            CorporateActionSelectedOptionDto option) {
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), ""));
        electionGroup.getOptions()
                     .add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY.getCode(), ""));

        // Divide by ratio
        BigDecimal ratio = getApplicableRatio(details);
        BigDecimal units = option.getUnits().multiply(ratio);

        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(),
                units.toPlainString()));
    }

    protected void setFullRightsExercise(CorporateActionElectionGroup electionGroup, CorporateActionSelectedOptionDto option) {
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), ""));
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(),
                "100"));

        if (option.getOversubscribe() != null && option.getOversubscribe().compareTo(BigDecimal.ZERO) > 0) {
            electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey
                    .EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY.getCode(), option.getOversubscribe().toPlainString()));
        } else {
            electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey
                    .EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY.getCode(), ""));
        }
    }

    private BigDecimal getApplicableRatio(CorporateActionDetails details) {
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

        return oldStock.setScale(4).divide(newStock.setScale(4), 4, BigDecimal.ROUND_HALF_DOWN);
    }
}
