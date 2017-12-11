package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.util.ArrayList;

import com.btfin.panorama.core.conversion.CodeCategory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionsDto;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionRequestConverter;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionDecisionImpl;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionElectionGroupImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionNonProRataPriorityOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionPosition;

@CorporateActionRequestConverter("CA_NON_PRO_RATA_PRIORITY_OFFER_REQUEST")
public class NonProRataPriorityOfferRequestConverterServiceImpl extends AbstractCorporateActionRequestConverterServiceImpl {
    @Autowired
    private StaticIntegrationService staticCodeService;

    @Override
    protected CorporateActionElectionGroup createElectionGroup(CorporateActionContext context,
                                                               CorporateActionSelectedOptionsDto optionsDto) {
        CorporateActionSelectedOptionDto optionDto = optionsDto.getPrimarySelectedOption();

        CorporateActionElectionGroup electionGroup =
                new CorporateActionElectionGroupImpl(context.getCorporateActionDetails().getOrderNumber(), optionDto, new
                        ArrayList<CorporateActionPosition>(), new ArrayList<CorporateActionOption>());

        if (optionDto.getOptionId().equals(CorporateActionNonProRataPriorityOfferType.TAKE_UP.getId())) {
            electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.SUBSCRIBED_QUANTITY.getCode(),
                    optionDto.getUnits().toPlainString()));
            electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(), ""));
        } else {
            electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.SUBSCRIBED_QUANTITY.getCode(), ""));
            electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(),
                    getAvaloqNoActionOptionId()));
        }

        return electionGroup;
    }

    private String getAvaloqNoActionOptionId() {
        final Code code = staticCodeService.loadCodeByUserId(CodeCategory.CA_SHARE_PURCHASE_PLAN_OPTION,
                CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_VALUE, null);

        return code != null ? code.getCodeId() : "";
    }
}
