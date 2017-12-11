package com.bt.nextgen.api.corporateaction.v1.service.converter;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionRequestConverter;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionDecisionImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;


@CorporateActionRequestConverter("CA_EXERCISE_CALL_OPTION_REQUEST")
public class ExerciseCallOptionRequestConverterServiceImpl extends ExerciseRightsRequestConverterServiceImpl {
    @Override
    protected void setLapseRightsExercise(CorporateActionElectionGroup electionGroup) {
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_NO_ACTION.getCode(), "Y"));
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), "0"));
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), ""));
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_RIGHTS_ALL.getCode(), "N"));
    }

    @Override
    protected void setPartialRightsExercise(CorporateActionDetails details, CorporateActionElectionGroup electionGroup, CorporateActionSelectedOptionDto option) {
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_NO_ACTION.getCode(), "N"));
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), ""));
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(),
				option.getUnits().toPlainString()));
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_RIGHTS_ALL.getCode(), "N"));
    }

    @Override
    protected void setFullRightsExercise(CorporateActionElectionGroup electionGroup, CorporateActionSelectedOptionDto option) {
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_NO_ACTION.getCode(), "N"));
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(),
                "100"));
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), ""));
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.EXERCISE_RIGHTS_ALL.getCode(), "Y"));
    }
}
